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
package io.micronaut.spring.beans;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.micronaut.core.annotation.Internal;

/**
 * Allows the spring import annotation to be represented as a repeated annotation.
 * This is an internal annotation and should not be used directly.
 *
 * @author graemerocher
 * @since 4.3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(SpringImport.List.class)
@Internal
public @interface SpringImport {
    /**
     * The type to import.
     * @return The type to import
     */
    Class<?> value();

    /**
     * Repeatable wrapper type.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        SpringImport[] value();
    }
}
