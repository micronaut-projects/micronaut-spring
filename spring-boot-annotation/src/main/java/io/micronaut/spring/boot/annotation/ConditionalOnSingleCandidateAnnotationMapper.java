/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.spring.boot.annotation;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.AnnotationClassValue;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Maps ConditionalOnSingleCandidate to Micronaut Requires.
 *
 * @author graemerocher
 * @since 1.0
 */
public class ConditionalOnSingleCandidateAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final AnnotationClassValue annotationClassValue = annotation.getValue(AnnotationClassValue.class).orElseGet(() ->
                annotation.get("type", String.class).map(AnnotationClassValue::new).orElse(null)
        );
        if (annotationClassValue != null) {

            return Arrays.asList(
                    AnnotationValue.builder(Requires.class)
                            .member("condition", new AnnotationClassValue<>(
                                    "io.micronaut.spring.boot.condition.RequiresSingleCandidateCondition"
                            ))
                            .build(),
                    AnnotationValue.builder("io.micronaut.spring.boot.condition.RequiresSingleCandidate")
                            .member("value", annotationClassValue)
                            .build()
            );
        }
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate";
    }
}
