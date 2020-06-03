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

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Status;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * Maps Spring ResponseStatus to Micronaut.
 *
 * @author graemerocher
 * @since 1.0
 */
public class ResponseStatusAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.ResponseStatus";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        try {
            final String code = annotation.getValue(String.class).orElse(annotation.get("code", String.class).orElse(null));
            final HttpStatus status = HttpStatus.valueOf(code);

            return Collections.singletonList(
                    AnnotationValue.builder(Status.class).value(status).build()
            );
        } catch (IllegalArgumentException e) {
            // ignore
        }
        return Collections.emptyList();
    }
}
