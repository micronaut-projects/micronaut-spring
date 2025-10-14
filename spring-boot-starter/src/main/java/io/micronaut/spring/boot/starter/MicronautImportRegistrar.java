/*
 * Copyright 2017-2022 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.spring.boot.starter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.sql.DataSource;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.Qualifier;
import io.micronaut.context.RuntimeBeanDefinition;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Infrastructure;
import io.micronaut.context.annotation.Primary;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.naming.Named;
import io.micronaut.core.reflect.InstantiationUtils;
import io.micronaut.core.util.ArrayUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.annotation.MutableAnnotationMetadata;
import io.micronaut.inject.qualifiers.Qualifiers;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotationMetadata;

/**
 * An {@link ImportBeanDefinitionRegistrar} that exposes all Micronaut beans as Spring beans using {@link EnableMicronaut}.
 *
 * <p>The beans to be exposed can be limited by a {@link MicronautBeanFilter} configured via {@link EnableMicronaut#filter()}.</p>
 *
 * @author graemerocher
 * @since 4.3.0
 */
@Internal
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
public final class MicronautImportRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanFactoryAware {
    private Environment environment;
    private BeanFactory beanFactory;

    private final List<ExposedBeanData> exposedBeans = new ArrayList<>();

    public MicronautImportRegistrar() {
        exposedBeans.add(new ExposedBeanData(DataSource.class, null, null, (name) -> {
            if ("dataSource".equals(name)) {
                return "default";
            }
            return name;
        }));
    }

    @Override
    public void registerBeanDefinitions(
        AnnotationMetadata importingClassMetadata,
        BeanDefinitionRegistry registry,
        BeanNameGenerator importBeanNameGenerator) {
        if (registry.containsBeanDefinition("micronautApplicationContext")) {
            // already registered
            return;
        }


        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        String[] activeProfiles = environment != null ? environment.getActiveProfiles() : StringUtils.EMPTY_STRING_ARRAY;
        ApplicationContextBuilder builder = ApplicationContext.builder(activeProfiles);
        if (beanFactory != null) {
            ObjectProvider<ApplicationArguments> beanProvider = beanFactory.getBeanProvider(ApplicationArguments.class);
            beanProvider.ifAvailable(args ->
                builder.args(args.getSourceArgs())
            );
        }
        if (environment instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment ce = (ConfigurableEnvironment) environment;
            List<io.micronaut.context.env.PropertySource> cePropertySources = propertySourcesForConfigurableEnvironment(ce);
            builder.propertySources(cePropertySources.toArray(new io.micronaut.context.env.PropertySource[0]));
        }
        builder.singletons(
            environment,
            beanFactory
        );
        ApplicationContext context = builder
            .banner(false)
            .deduceEnvironment(false)
            .build();
        context.start();
        GenericBeanDefinition ppd = new GenericBeanDefinition();
        ppd.setBeanClass(MicronautPostProcess.class);
        ppd.setInstanceSupplier(() -> new MicronautPostProcess(context));
        registry.registerBeanDefinition("micronautPostProcess", ppd);
        genericBeanDefinition.setBeanClass(context.getClass());
        genericBeanDefinition.setInstanceSupplier(() -> context);
        genericBeanDefinition.setDestroyMethodName("stop");
        registry.registerBeanDefinition(
            "micronautApplicationContext",
            genericBeanDefinition
        );
        MergedAnnotation<EnableMicronaut> enableMicronautAnn = importingClassMetadata.getAnnotations().get(EnableMicronaut.class);
        MicronautBeanFilter beanFilter = new MicronautBeanFilter() {
            @Override
            public boolean excludes(@NonNull BeanDefinition<?> definition) {
                return definition.isAbstract() ||
                    Stream.of(
                        org.springframework.context.ApplicationContext.class,
                        ConversionService.class,
                        Environment.class,
                        ApplicationEventPublisher.class,
                        BeanFactory.class
                    ).anyMatch(t -> t.isAssignableFrom(definition.getBeanType()));
            }
        };
        if (enableMicronautAnn.isPresent()) {
            if (enableMicronautAnn.hasNonDefaultValue("filter")) {

                Class<?> filter = enableMicronautAnn.getClass("filter");
                Object filterObject = InstantiationUtils.tryInstantiate(filter).orElse(null);
                if (filterObject instanceof MicronautBeanFilter) {
                    MicronautBeanFilter specificFilter = (MicronautBeanFilter) filterObject;
                    MicronautBeanFilter currentFilter = beanFilter;
                    beanFilter = new MicronautBeanFilter() {
                        @Override
                        public boolean includes(@NonNull BeanDefinition<?> definition) {
                            return currentFilter.includes(definition) && specificFilter.includes(definition);
                        }

                        @Override
                        public boolean excludes(@NonNull BeanDefinition<?> definition) {
                            return currentFilter.excludes(definition) || specificFilter.excludes(definition);
                        }
                    };
                }
            }

            EnableMicronaut enableMicronaut = enableMicronautAnn.synthesize();
            EnableMicronaut.ExposedBean[] exposedBeans = enableMicronaut.exposeToMicronaut();
            if (ArrayUtils.isNotEmpty(exposedBeans)) {
                for (EnableMicronaut.ExposedBean exposedBean : exposedBeans) {
                    ExposedBeanData exposedBeanData = new ExposedBeanData(
                        exposedBean.beanType(),
                        StringUtils.isNotEmpty(exposedBean.name()) ? exposedBean.name() : null,
                        StringUtils.isNotEmpty(exposedBean.qualifier()) ? exposedBean.qualifier() : null,
                        null);
                    this.exposedBeans.remove(exposedBeanData);
                    this.exposedBeans.add(exposedBeanData);
                }
            }
        }

        Collection<BeanDefinition<?>> allBeanDefinitions = context.getAllBeanDefinitions();
        for (BeanDefinition<?> definition : allBeanDefinitions) {
            if (beanFilter.includes(definition) &&
                !beanFilter.excludes(definition)) {
                Class<?> beanType = definition.getBeanType();
                if (definition.isEnabled(context)) {
                    if (definition.isIterable()) {
                        Collection<? extends BeanDefinition<?>> beanDefinitions = context.getBeanDefinitions(beanType);
                        for (BeanDefinition<?> beanDefinition : beanDefinitions) {
                            registerBeanWithContext(
                                registry,
                                context,
                                beanDefinition,
                                beanType
                            );
                        }
                    } else {
                        registerBeanWithContext(registry, context, definition, beanType);
                    }
                }
            }
        }
    }

    private static void registerBeanWithContext(BeanDefinitionRegistry registry, ApplicationContext context, BeanDefinition<?> definition, Class<?> beanType) {
        String scope = definition.getScopeName().orElse(null);
        GenericBeanDefinition gbd = new GenericBeanDefinition();
        boolean isContextScope = Context.class.getName().equals(scope);
        gbd.setPrimary(definition.isPrimary());
        gbd.setLazyInit(!isContextScope);
        int role = definition.hasDeclaredAnnotation(Infrastructure.class) ? org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE : org.springframework.beans.factory.config.BeanDefinition.ROLE_APPLICATION;
        gbd.setRole(role);
        if (definition.isSingleton() || isContextScope || definition.isIterable()) {
            gbd.setScope("singleton");
        } else {
            // perhaps support other scopes in the future
            gbd.setScope("prototype");
        }

        gbd.setBeanClass(beanType);
        gbd.setInstanceSupplier(() ->
            context.getBean(definition)
        );
        Qualifier<?> qualifier = definition.getDeclaredQualifier();
        String beanName = computeBeanName(registry, definition, gbd, qualifier);
        gbd.setDescription("Bean named [" + beanName + "] of type [" + beanType.getName() + "] (Imported from Micronaut)");
        if (!registry.containsBeanDefinition(beanName)) {
            registry.registerBeanDefinition(
                beanName,
                gbd
            );
        }
    }

    private static String computeBeanName(BeanDefinitionRegistry registry, BeanDefinition<?> definition, GenericBeanDefinition gbd, Qualifier<?> qualifier) {
        String beanName;
        if (qualifier != null) {
            if (qualifier instanceof Named) {
                beanName = ((Named) qualifier).getName();
                if ("Primary".equals(beanName)) {
                    beanName = AnnotationBeanNameGenerator.INSTANCE.generateBeanName(
                        gbd,
                        registry
                    );
                } else {
                    beanName = definition.getBeanType().getName() + "(" + beanName + ")";
                }
            } else {
                beanName = AnnotationBeanNameGenerator.INSTANCE.generateBeanName(
                    gbd,
                    registry
                );
            }
        } else {
            beanName = AnnotationBeanNameGenerator.INSTANCE.generateBeanName(
                gbd,
                registry
            );
        }
        return beanName;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @NonNull
    private List<io.micronaut.context.env.PropertySource> propertySourcesForConfigurableEnvironment(@NonNull ConfigurableEnvironment ce) {
        List<io.micronaut.context.env.PropertySource> result = new ArrayList<>();
        MutablePropertySources propertySources = ce.getPropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            if (propertySource instanceof MapPropertySource) {
                MapPropertySource mps = (MapPropertySource) propertySource;
                Map<String, Object> source = mps.getSource();
                    result.add(io.micronaut.context.env.PropertySource.of(
                        mps.getName(),
                        source
                    ));
            }
        }
        return result;
    }

    private final class MicronautPostProcess implements BeanFactoryPostProcessor {

        private final ApplicationContext context;

        public MicronautPostProcess(ApplicationContext context) {
            this.context = context;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            for (ExposedBeanData exposedBean : exposedBeans) {
                String[] beanNames = beanFactory.getBeanNamesForType(exposedBean.beanType);
                for (String beanName : beanNames) {
                    org.springframework.beans.factory.config.BeanDefinition beanDefinition =
                        beanFactory.getBeanDefinition(beanName);
                    String qualifier = exposedBean.nameTransformer.apply(beanName);
                    RuntimeBeanDefinition.Builder<DataSource> builder = RuntimeBeanDefinition.builder(DataSource.class, () ->
                            beanFactory.getBean(beanName, DataSource.class)
                        ).qualifier(Qualifiers.byName(qualifier))
                        .singleton(beanDefinition.isSingleton());

                    if (beanDefinition.isPrimary()) {
                        MutableAnnotationMetadata metadata = new MutableAnnotationMetadata();
                        metadata.addDeclaredAnnotation(Primary.class.getName(), Collections.emptyMap());
                        builder.annotationMetadata(metadata);
                    }
                    context.registerBeanDefinition(
                        builder.build()
                    );
                }

            }
        }
    }

    private static final class ExposedBeanData {
        final Class<?> beanType;
        @Nullable
        final String beanName;
        @Nullable
        final String qualifier;

        @NonNull
        final UnaryOperator<String> nameTransformer;

        private ExposedBeanData(
            Class<?> beanType,
            @Nullable String beanName,
            @Nullable String qualifier,
            @Nullable UnaryOperator<String> nameTransformer) {
            this.beanType = beanType;
            this.beanName = beanName;
            this.qualifier = qualifier;
            this.nameTransformer = nameTransformer != null ? nameTransformer : UnaryOperator.identity();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ExposedBeanData that = (ExposedBeanData) o;
            return beanType.equals(that.beanType) && Objects.equals(beanName, that.beanName) && Objects.equals(qualifier, that.qualifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(beanType, beanName, qualifier);
        }
    }
}
