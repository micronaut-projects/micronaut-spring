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
package io.micronaut.spring.annotation.context;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Maps the Spring Profile annotation to {@link Requires}.
 *
 * @author graemerocher
 * @since 1.0
 */
public class ProfileAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        Optional<String[]> value = annotation.getValue(String[].class);
        return value.<List<AnnotationValue<?>>>map(strings -> Collections.singletonList(
                AnnotationValue.builder(Requires.class)
                        .member("env", strings).build()
        )).orElse(Collections.emptyList());
    }

    @Override
    public String getName() {
        return "org.springframework.context.annotation.Profile";
    }
}
