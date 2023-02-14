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
package io.micronaut.spring.web.annotation;

import io.micronaut.core.annotation.AnnotationUtil;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
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
public class RequestBodyAnnotationMapping extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.RequestBody";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        List<AnnotationValue<?>> mappedAnnotations = new ArrayList<>();
        final boolean required = annotation.booleanValue("required").orElse(true);
        final AnnotationValueBuilder<?> builder = AnnotationValue.builder(Body.class);
        final AnnotationValueBuilder<Bindable> bindableBuilder = AnnotationValue.builder(Bindable.class);
        mappedAnnotations.add(builder.build());
        mappedAnnotations.add(bindableBuilder.build());
        if (!required) {
            mappedAnnotations.add(AnnotationValue.builder(AnnotationUtil.NULLABLE).build());
        }
        return mappedAnnotations;
    }
}
