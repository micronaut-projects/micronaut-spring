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
package io.micronaut.spring.annotation.cache;

import io.micronaut.cache.annotation.CachePut;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.core.annotation.NonNull;

import java.lang.annotation.Annotation;

/**
 * Maps the Spring cache annotations.
 *
 * @since 1.0
 * @author graemerocher
 */
public class CachePutAnnotationMapper extends CacheableAnnotationMapper {

    @Override
    public String getName() {
        return "org.springframework.cache.annotation.CachePut";
    }

    @Override
    protected @NonNull AnnotationValueBuilder<? extends Annotation> buildAnnotation() {
        return AnnotationValue.builder(CachePut.class);
    }
}
