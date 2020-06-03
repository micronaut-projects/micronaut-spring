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
package io.micronaut.spring.boot.annotation;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.ConfigurationReader;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.naming.NameUtils;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Maps ConfigurationProperties to Micronaut ConfigurationProperties.
 *
 * @author graemerocher
 * @since 1.0
 */
public class ConfigurationPropertiesAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.boot.context.properties.ConfigurationProperties";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        String prefix = annotation.get("prefix", String.class).orElseGet(() -> annotation.getValue(String.class).orElse(null));
        if (prefix != null) {
            prefix = NameUtils.hyphenate(prefix, true);
            return Arrays.asList(
                    AnnotationValue.builder(ConfigurationReader.class)
                            .member("prefix", prefix)
                            .build(),
                    AnnotationValue.builder(ConfigurationProperties.class)
                            .value(prefix)
                            .build()
            );
        } else {
            return Collections.emptyList();
        }
    }
}
