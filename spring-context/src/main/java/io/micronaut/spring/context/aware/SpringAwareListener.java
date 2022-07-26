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
import java.util.function.Supplier;

import io.micronaut.context.BeanProvider;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.context.event.BeanInitializedEventListener;
import io.micronaut.context.event.BeanInitializingEvent;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.util.SupplierUtil;
import io.micronaut.spring.context.MicronautApplicationContext;
import io.micronaut.spring.beans.MicronautContextInternal;
import io.micronaut.spring.context.factory.MicronautBeanFactory;
import io.micronaut.spring.context.env.MicronautEnvironment;
import jakarta.inject.Singleton;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;

/**
 * Enables support for the interfaces {@link EnvironmentAware}, {@link ApplicationContextAware}, and {@link BeanFactoryAware}.
 *
 * @author graemerocher
 */
@Singleton
@Internal
public class SpringAwareListener implements BeanInitializedEventListener<Object>, BeanCreatedEventListener<Object> {

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
    public SpringAwareListener(BeanProvider<MicronautBeanFactory> beanFactoryProvider, BeanProvider<MicronautEnvironment> environmentProvider, BeanProvider<MicronautApplicationContext> applicationContextProvider) {
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
        wireAwareObjects(bean, event.getBeanDefinition().getName());
        return bean;
    }

    private void wireAwareObjects(Object bean, String beanName) {
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

    }

    @Override
    public Object onCreated(BeanCreatedEvent<Object> event) {
        final Object bean = event.getBean();
        if (bean instanceof MicronautContextInternal) {
            return bean;
        }
        return onBeanCreated(bean, event.getBeanIdentifier().getName());
    }

    /**
     * Execute when a bean is created.
     *
     * @param bean           The bean.
     * @return The result
     * @deprecated Use {@link #onBeanCreated(Object, String)}
     */
    @Deprecated
    public Object onBeanCreated(Object bean) {
        return onBeanCreated(bean, null);
    }

    /**
     * Execute when a bean is created.
     *
     * @param bean           The bean.
     * @param beanName       The bean name
     * @return The result
     */
    public Object onBeanCreated(Object bean, String beanName) {
        wireAwareObjects(bean, beanName);
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
}
