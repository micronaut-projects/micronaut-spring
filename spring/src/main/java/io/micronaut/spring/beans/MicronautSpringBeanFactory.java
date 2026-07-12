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
import io.micronaut.context.exceptions.BeanInstantiationException;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;

import java.util.Optional;

/**
 * A spring FactoryBean for adding Micronaut beans to a
 * Spring application context.
 *
 * @author jeffbrown
 * @since 1.0
 */
class MicronautSpringBeanFactory implements FactoryBean<Object> {

    private @Nullable Class<?> micronautBeanType;
    private @Nullable ApplicationContext micronautContext;
    private boolean isMicronautSingleton;

    /**
     * Sets the type of bean this factory will create.
     *
     * @param micronautBeanType The type of bean this factory will create
     */
    public void setMicronautBeanType(Class<?> micronautBeanType) {
        this.micronautBeanType = micronautBeanType;
    }

    /**
     * Sets the Micronaut application context.
     *
     * @param micronautContext The Micronaut application context
     */
    public void setMicronautContext(ApplicationContext micronautContext) {
        this.micronautContext = micronautContext;
    }

    /**
     * Sets whether the Micronaut bean is a singleton.
     *
     * @param isMicronautSingleton indicates if the Micronaut bean is a singleton
     */
    public void setMicronautSingleton(boolean isMicronautSingleton) {
        this.isMicronautSingleton = isMicronautSingleton;
    }

    @Override
    public Object getObject() throws Exception {
        Class<?> beanType = micronautBeanType;
        ApplicationContext context = micronautContext;
        if (beanType == null || context == null) {
            throw new BeanInstantiationException("Micronaut bean factory is not configured");
        }
        Optional<?> bean = context.findBean(beanType);
        if (bean.isPresent()) {
            return bean.get();
        }

        throw new BeanInstantiationException("Could Not Create Bean [" + beanType + "]");
    }

    @Override
    public @Nullable Class<?> getObjectType() {
        return micronautBeanType;
    }

    @Override
    public boolean isSingleton() {
        return isMicronautSingleton;
    }
}
