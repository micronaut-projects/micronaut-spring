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

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.inject.BeanDefinition;

/**
 * Allows specifying a filter to include or exclude certain beans from being exposed to Spring.
 *
 * @author graemerocher
 * @since 4.3.0
 */
@Introspected
public interface MicronautBeanFilter {

    /**
     * Return whether to include the given bean reference as Spring bean.
     * @param definition The definition
     * @return True if the definition should be exposed as Spring bean
     */
    default boolean includes(@NonNull BeanDefinition<?> definition) {
        return true;
    }

    /**
     * Return whether to exclude the given bean definition as Spring bean.
     * @param definition The definition
     * @return True if the definition should be exposed as Spring bean
     */
    default boolean excludes(@NonNull BeanDefinition<?> definition) {
        return false;
    }
}
