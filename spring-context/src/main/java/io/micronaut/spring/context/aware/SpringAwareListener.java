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
package io.micronaut.spring.context.aware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import io.micronaut.context.BeanProvider;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.context.event.BeanInitializedEventListener;
import io.micronaut.context.event.BeanInitializingEvent;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.naming.NameUtils;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.BeanIdentifier;
import io.micronaut.spring.beans.MicronautContextInternal;
import io.micronaut.spring.context.MicronautApplicationContext;
import io.micronaut.spring.context.env.MicronautEnvironment;
import io.micronaut.spring.context.factory.MicronautBeanFactory;
import jakarta.inject.Singleton;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Role;

/**
 * Enables support for the interfaces {@link EnvironmentAware}, {@link ApplicationContextAware}, and {@link BeanFactoryAware}.
 *
 * @author graemerocher
 */
@Singleton
@Internal
public class SpringAwareListener implements BeanInitializedEventListener<Object>, BeanCreatedEventListener<Object> {

    public static final int ROLE_APPLICATION = org.springframework.beans.factory.config.BeanDefinition.ROLE_APPLICATION;
    private final BeanProvider<MicronautBeanFactory> beanFactoryProvider;
    private final BeanProvider<MicronautEnvironment> environmentProvider;
    private final BeanProvider<MicronautApplicationContext> applicationContextProvider;

    private Collection<BeanPostProcessor> beanPostProcessors;

    /**
     * Default constructor.
     * @param beanFactoryProvider The bean factory provider
     * @param environmentProvider The env provider
     * @param applicationContextProvider The context provider
     */
    public SpringAwareListener(
        BeanProvider<MicronautBeanFactory> beanFactoryProvider,
        BeanProvider<MicronautEnvironment> environmentProvider,
        BeanProvider<MicronautApplicationContext> applicationContextProvider) {
        this.beanFactoryProvider = beanFactoryProvider;
        this.environmentProvider = environmentProvider;
        this.applicationContextProvider = applicationContextProvider;
    }

    @Override
    public Object onInitialized(BeanInitializingEvent<Object> event) {
        final Object bean = event.getBean();
        if (bean instanceof MicronautContextInternal) {
            return bean;
        }
        wireAwareObjects(bean);
        return bean;
    }

    private void wireAwareObjects(Object bean) {
        if (bean instanceof BeanClassLoaderAware) {
            ((BeanClassLoaderAware) bean).setBeanClassLoader(Objects.requireNonNull(applicationContextProvider.get().getClassLoader()));
        }
        if (bean instanceof EnvironmentAware) {
            ((EnvironmentAware) bean).setEnvironment(environmentProvider.get());
        }
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(beanFactoryProvider.get());
        }
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(applicationContextProvider.get());
        }
        if (bean instanceof ResourceLoaderAware) {
            ((ResourceLoaderAware) bean).setResourceLoader(applicationContextProvider.get());
        }
    }

    @Override
    public Object onCreated(BeanCreatedEvent<Object> event) {
        final Object bean = event.getBean();
        if (bean instanceof MicronautContextInternal) {
            return bean;
        }
        return onBeanCreated(event.getBeanDefinition(), bean, resolveBeanName(event));
    }

    private String resolveBeanName(BeanCreatedEvent<Object> event) {
        BeanIdentifier beanIdentifier = event.getBeanIdentifier();
        String name = beanIdentifier.getName();
        if (name.equals("Primary")) {
            return NameUtils.decapitalize(event.getBeanDefinition().getBeanType().getSimpleName());
        }
        return name;
    }

    /**
     * Execute when a bean is created.
     *
     * @param o
     * @param bean           The bean.
     * @return The result
     * @deprecated Use {@link #onBeanCreated(BeanDefinition, Object, String)}
     */
    @Deprecated
    public Object onBeanCreated(Object o, Object bean) {
        return onBeanCreated(null, bean, null);
    }

    /**
     * Execute when a bean is created.
     *
     * @param beanDefinition The bean definition
     * @param bean           The bean.
     * @param beanName       The bean name
     * @return The result
     */
    public Object onBeanCreated(@Nullable BeanDefinition<Object> beanDefinition, Object bean, String beanName) {
        wireAwareObjects(bean);
        if (!(bean instanceof BeanPostProcessor)) {
            // init provider
            initProcessors();
            Collection<BeanPostProcessor> processors = beanPostProcessors;
            for (BeanPostProcessor processor : processors) {
                Object o = processor.postProcessBeforeInitialization(bean, beanName);
                if (o == null) {
                    break;
                } else {
                    bean = o;
                }
            }
        }
        if (bean instanceof InitializingBean) {
            try {
                ((InitializingBean) bean).afterPropertiesSet();
            } catch (Exception e) {
                throw new BeanCreationException(e.getMessage(), e);
            }
        }

        if (!(bean instanceof BeanPostProcessor)) {
            int role = beanDefinition != null ? beanDefinition.intValue(Role.class)
                                                              .orElse(ROLE_APPLICATION) : ROLE_APPLICATION;
            if (role == ROLE_APPLICATION) {
                initProcessors();
                for (BeanPostProcessor processor : beanPostProcessors) {
                    Object o = processor.postProcessAfterInitialization(bean, beanName);
                    if (o == null) {
                        break;
                    } else {
                        bean = o;
                    }
                }
            }

        }
        return bean;
    }

    private void initProcessors() {
        if (beanPostProcessors == null) {
            beanPostProcessors = new ArrayList<>();
            MicronautBeanFactory micronautBeanFactory = beanFactoryProvider.get();
            Collection<BeanPostProcessor> processors = micronautBeanFactory.getBeanContext().getBeansOfType(BeanPostProcessor.class);
            beanPostProcessors.addAll(processors);
        }
    }

    /**
     * Reset the bean processors.
     */
    public void resetPostProcessors() {
        this.beanPostProcessors = null;
    }
}
