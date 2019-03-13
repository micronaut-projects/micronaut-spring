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
package io.micronaut.spring.annotation.context;

import io.micronaut.context.annotation.Factory;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps {@code @Configuration} to {@link Factory}.
 *
 * @author graemerocher
 * @since 1.0
 */
public class ConfigurationAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.context.annotation.Configuration";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        List<AnnotationValue<?>> mappedAnnotations = new ArrayList<>(2);
        mappedAnnotations.add(AnnotationValue.builder(Factory.class)
                .build());

        mappedAnnotations.add(AnnotationValue.builder(
                "io.micronaut.spring.context.aop.SpringConfigurationAdvice"
        ).build());
        return mappedAnnotations;
    }
}
