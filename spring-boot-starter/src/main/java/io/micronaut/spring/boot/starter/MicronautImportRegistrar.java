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

import java.util.Collection;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.Qualifier;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Infrastructure;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.naming.Named;
import io.micronaut.core.reflect.InstantiationUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanDefinition;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * An {@link ImportBeanDefinitionRegistrar} that exposes all Micronaut beans as Spring beans using {@link EnableMicronaut}.
 *
 * <p>The beans to be exposed can be limited by a {@link MicronautBeanFilter} configured via {@link EnableMicronaut#filter()}.</p>
 *
 * @author graemerocher
 * @since 4.3.0
 */
public class MicronautImportRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanFactoryAware {
    private Environment environment;
    private BeanFactory beanFactory;

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
        ApplicationContext context = builder
            .banner(false)
            .deduceEnvironment(false)
            .build()
            .start();
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
                return definition.isAbstract() || definition.isIterable() ||
                    org.springframework.context.ApplicationContext.class.isAssignableFrom(definition.getBeanType());
            }
        };
        if (enableMicronautAnn.isPresent() && enableMicronautAnn.hasNonDefaultValue("filter")) {
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

        Collection<BeanDefinition<?>> allBeanDefinitions = context.getAllBeanDefinitions();
        for (BeanDefinition<?> definition : allBeanDefinitions) {
            if (beanFilter.includes(definition) &&
                !beanFilter.excludes(definition)) {
                Class<?> beanType = definition.getBeanType();
                if (definition.isEnabled(context)) {
                    String scope = definition.getScopeName().orElse(null);
                    GenericBeanDefinition gbd = new GenericBeanDefinition();
                    boolean isContextScope = Context.class.getName().equals(scope);
                    gbd.setPrimary(definition.isPrimary());
                    gbd.setLazyInit(!isContextScope);
                    int role = definition.hasDeclaredAnnotation(Infrastructure.class) ? org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE : org.springframework.beans.factory.config.BeanDefinition.ROLE_APPLICATION;
                    gbd.setRole(role);
                    if (gbd.isSingleton() || isContextScope) {
                        gbd.setScope("singleton");
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
            }
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
}
