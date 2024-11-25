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
package io.micronaut.spring.annotation.context;

import io.micronaut.context.annotation.Secondary;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.annotation.TypedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import java.util.List;
import org.springframework.context.annotation.Fallback;

public class FallbackAnnotationMapper
    implements TypedAnnotationMapper<Fallback> {
    @Override
    public Class<Fallback> annotationType() {
        return Fallback.class;
    }

    @Override
    public List<AnnotationValue<?>> map(AnnotationValue<Fallback> annotation, VisitorContext visitorContext) {
        return List.of(AnnotationValue.builder(Secondary.class).build());
    }
}
