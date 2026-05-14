/*
 * Copyright 2017-2020 original authors
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
package io.micronaut.spring.beans;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.Qualifier;
import io.micronaut.context.env.PropertySource;
import io.micronaut.core.util.ArrayUtils;
import io.micronaut.inject.qualifiers.Qualifiers;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Adds Micronaut beans to a Spring application context.  This processor will
 * find all of the Micronaut beans of the specified types
 * and add them as beans to the Spring application context.
 *
 * @author jeffbrown
 * @since 1.0
 */
public class MicronautBeanProcessor implements BeanFactoryPostProcessor, DisposableBean, EnvironmentAware {

    private static final String MICRONAUT_BEAN_TYPE_PROPERTY_NAME = "micronautBeanType";
    private static final String MICRONAUT_CONTEXT_PROPERTY_NAME = "micronautContext";
    private static final String MICRONAUT_SINGLETON_PROPERTY_NAME = "micronautSingleton";

    protected @Nullable ApplicationContext micronautContext;
    protected final List<Class<?>> micronautBeanQualifierTypes;
    private @Nullable Environment environment;

    /**
     *
     * @param qualifierTypes The types associated with the
     *                   Micronaut beans which should be added to the
     *                   Spring application context.
     */
    public MicronautBeanProcessor(Class<?>... qualifierTypes) {
        this.micronautBeanQualifierTypes = Arrays.asList(qualifierTypes);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ApplicationContextBuilder builder = ApplicationContext.builder();

        Environment currentEnvironment = environment;
        if (currentEnvironment != null) {
            String[] profiles = getProfiles(currentEnvironment);
            builder.environments(profiles);

            if (currentEnvironment instanceof ConfigurableEnvironment configurableEnv) {
                builder.propertySourcesLocator(env -> {
                    List<PropertySource> propertySources = new ArrayList<>();
                    int order = 0;
                    for (org.springframework.core.env.PropertySource<?> springSource : configurableEnv.getPropertySources()) {
                        if (springSource instanceof EnumerablePropertySource<?> enumerableSource) {
                            final int currentOrder = order++;
                            propertySources.add(new PropertySource() {
                                @Override
                                public String getName() {
                                    return springSource.getName();
                                }

                                @Override
                                public @Nullable Object get(String key) {
                                    return enumerableSource.getProperty(key);
                                }

                                @Override
                                public Iterator<String> iterator() {
                                    return Arrays.asList(enumerableSource.getPropertyNames()).iterator();
                                }

                                @Override
                                public int getOrder() {
                                    return currentOrder;
                                }
                            });
                        }
                    }
                    return propertySources;
                });
            }
        }

        micronautContext = builder.build();
        micronautContext.start();

        micronautBeanQualifierTypes
                .forEach(micronautBeanQualifierType -> {
            Qualifier<Object> micronautBeanQualifier = micronautBeanQualifierType.isAnnotation() ? Qualifiers.byStereotype((Class<? extends Annotation>) micronautBeanQualifierType) : Qualifiers.byType(micronautBeanQualifierType);
            micronautContext.getBeanDefinitions(micronautBeanQualifier)
                    .forEach(definition -> {
                        final BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                                .rootBeanDefinition(MicronautSpringBeanFactory.class.getName());
                        beanDefinitionBuilder.addPropertyValue(MICRONAUT_BEAN_TYPE_PROPERTY_NAME, definition.getBeanType());
                        beanDefinitionBuilder.addPropertyValue(MICRONAUT_CONTEXT_PROPERTY_NAME, micronautContext);
                        beanDefinitionBuilder.addPropertyValue(MICRONAUT_SINGLETON_PROPERTY_NAME, definition.isSingleton());
                        ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition(definition.getName(), beanDefinitionBuilder.getBeanDefinition());
                    });
        });
    }

    private static String[] getProfiles(Environment environment) {
        if (ArrayUtils.isNotEmpty(environment.getActiveProfiles())) {
            return environment.getActiveProfiles();
        } else {
            return environment.getDefaultProfiles();
        }
    }

    @Override
    public void destroy() throws Exception {
        ApplicationContext context = micronautContext;
        if (context != null) {
            context.close();
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
