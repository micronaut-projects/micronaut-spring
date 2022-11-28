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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;


/**
 * Annotation that can be added to a Spring application case in the case where auto-configuration is disabled.
 *
 * @since 4.3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Import(MicronautImportRegistrar.class)
public @interface EnableMicronaut {
    /**
     * A filter to apply to exclude or include specific bean types from being exposed as Spring beans.
     * @return The bean filter
     */
    Class<? extends MicronautBeanFilter> filter() default MicronautBeanFilter.class;

    /**
     * Defines one or more types that represent Spring beans that should be exposed to the Micronaut context.
     * <p>Note care should be taken that circular dependencies are not introduced between the Spring context and the Micronaut context</p>
     * @return The exposed bean.
     */
    ExposedBean[] exposeToMicronaut() default  {};

    /**
     * Used to allow to expose beans from the Spring to the Micronaut context.
     */
    @interface ExposedBean {
        /**
         * @return The bean type.
         */
        Class<?> beanType();

        /**
         * @return The name of the bean in the Spring context otherwise all beans of the type are searched.
         */
        String name() default "";

        /**
         * @return The qualifier to use for the Micronaut bean that is registered, defaults to the Spring Bean name.
         */
        String qualifier() default "";
    }
}
