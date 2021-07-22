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

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.annotation.Produces;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps Actuator ReadOperation to Micronaut Read.
 *
 * @author graemerocher
 * @since 1.0
 */
public class ReadOperationAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final String[] produces = annotation.get("produces", String[].class).orElse(null);
        final AnnotationValue<?> readOp = AnnotationValue.builder("io.micronaut.management.endpoint.annotation." + operationName()).build();
        List<AnnotationValue<?>> annotationValues = new ArrayList<>(2);

        annotationValues.add(readOp);
        if (produces != null) {
            final AnnotationValue<Produces> producesAnn = AnnotationValue.builder(Produces.class).member("value", produces).build();
            annotationValues.add(producesAnn);
        }
        return annotationValues;
    }

    @Override
    public String getName() {
        return "org.springframework.boot.actuate.endpoint.annotation." + operationName() + "Operation";
    }

    /**
     * The operation name.
     * @return The operation name
     */
    protected @NonNull String operationName() {
        return "Read";
    }
}
