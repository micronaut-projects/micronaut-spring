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
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Maps Actuator Endpoint to Micronaut Endpoint.
 *
 * @author graemerocher
 * @since 1.0
 */
public class EndpointAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final Optional<String> id = annotation.get("id", String.class);
        if (id.isPresent()) {
            final AnnotationValueBuilder<?> builder = AnnotationValue.builder("io.micronaut.management.endpoint.annotation.Endpoint");
            final Boolean enableByDefault = annotation.get("enableByDefault", boolean.class).orElse(true);
            builder.value(id.get());
            builder.member("id", id.get());
            builder.member("defaultEnabled", enableByDefault);
            return Collections.singletonList(builder.build());
        }

        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "org.springframework.boot.actuate.endpoint.annotation.Endpoint";
    }
}
