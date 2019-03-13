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
package io.micronaut.spring.annotation.cache;

import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * Maps the Spring cache annotations.
 *
 * @since 1.0
 * @author graemerocher
 */
public class CacheableAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final String[] cacheNames = annotation.getValue(String[].class).orElseGet(() -> annotation.get("cacheNames", String[].class).orElse(null));

        if (cacheNames != null) {
            final AnnotationValueBuilder<?> builder = buildAnnotation()
                    .member("value", cacheNames)
                    .member("cacheNames", cacheNames);


            return Collections.singletonList(builder.build());
        }

        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "org.springframework.cache.annotation.Cacheable";
    }

    /**
     * Builds the target annotation.
     * @return The annotation builder
     */
    protected @Nonnull AnnotationValueBuilder<? extends Annotation> buildAnnotation() {
        return AnnotationValue.builder(Cacheable.class);
    }
}
