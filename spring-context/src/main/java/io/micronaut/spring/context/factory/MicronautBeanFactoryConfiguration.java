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
package io.micronaut.spring.context.factory;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.ArgumentUtils;

import java.util.Collections;
import java.util.List;

/**
 * Configuration for the Micronaut bean factory.
 *
 * @author graemerocher
 * @since 1.0
 */
@ConfigurationProperties(MicronautBeanFactoryConfiguration.PREFIX)
public class MicronautBeanFactoryConfiguration {
    public static final String PREFIX = "micronaut.spring.context";

    private List<Class<?>> beanExcludes = Collections.emptyList();

    /**
     * The bean types to exclude from being exposed by Spring's {@link org.springframework.beans.factory.BeanFactory} interface.
     * @return The bean types
     */
    public @NonNull List<Class<?>> getBeanExcludes() {
        return beanExcludes;
    }

    /**
     * The bean types to exclude from being exposed by Spring's {@link org.springframework.beans.factory.BeanFactory} interface.
     * @param beanExcludes The bean types
     */
    public void setBeanExcludes(@NonNull List<Class<?>> beanExcludes) {
        ArgumentUtils.requireNonNull("beanExcludes", beanExcludes);
        this.beanExcludes = beanExcludes;
    }
}
