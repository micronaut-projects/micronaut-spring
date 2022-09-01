/*
 * Copyright 2017-2022 original authors
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
package io.micronaut.spring.annotation.beans;

import java.util.Arrays;
import java.util.List;

import io.micronaut.context.annotation.Bean;
import io.micronaut.core.annotation.Internal;
import org.springframework.context.annotation.Import;

import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.annotation.TypedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;

/**
 * Maps {@code io.micronaut.spring.beans.SpringImport} to Micronaut Framework {@link Import} annotation.
 * @author graemerocher
 * @since 4.3.0
 */
@Internal
public final class ImportAnnotationMapper implements TypedAnnotationMapper<Import> {

    @Override
    public Class<Import> annotationType() {
        return Import.class;
    }

    @Override
    public List<AnnotationValue<?>> map(AnnotationValue<Import> annotation, VisitorContext visitorContext) {
        return Arrays.asList(
            AnnotationValue.builder("io.micronaut.spring.beans.SpringImport")
                .member(AnnotationMetadata.VALUE_MEMBER, annotation.annotationClassValues(AnnotationMetadata.VALUE_MEMBER))
                .build(),
            annotation,
            AnnotationValue.builder(Bean.class).build()
        );
    }
}
