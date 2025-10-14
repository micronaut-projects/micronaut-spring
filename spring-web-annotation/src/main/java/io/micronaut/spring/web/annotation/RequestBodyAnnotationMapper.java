/*
 * Copyright 2017-2024 original authors
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
package io.micronaut.spring.web.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.bind.annotation.Bindable;
import io.micronaut.http.annotation.Body;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps Spring RequestBody to Micronaut.
 *
 * @author graemerocher
 * @since 1.0
 */
public class RequestBodyAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.RequestBody";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        var mappedAnnotations = new ArrayList<AnnotationValue<?>>();
        mappedAnnotations.add(AnnotationValue.builder(Body.class).build());
        mappedAnnotations.add(AnnotationValue.builder(Bindable.class).build());

        var isRequired = annotation.booleanValue("required").orElse(true);
        if (!isRequired) {
            mappedAnnotations.add(AnnotationValue.builder(Nullable.class).build());
        }
        return mappedAnnotations;
    }
}
