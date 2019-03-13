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
package io.micronaut.spring.web.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.http.annotation.Controller;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.context.ComponentAnnotationMapper;
import io.micronaut.validation.Validated;

import java.lang.annotation.Annotation;
import java.util.List;

public class RestControllerAnnotationMapper extends ComponentAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.RestController";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final List<AnnotationValue<?>> annotationValues = super.mapInternal(annotation, visitorContext);
        annotationValues.add(AnnotationValue.builder(Controller.class).build());
        annotationValues.add(AnnotationValue.builder(Validated.class).build());
        return annotationValues;
    }
}
