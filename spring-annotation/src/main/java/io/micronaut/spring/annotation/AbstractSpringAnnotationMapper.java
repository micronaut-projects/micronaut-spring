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
package io.micronaut.spring.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.version.VersionUtils;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * Abstract mapper for Spring annotations.
 *
 * @author graemerocher
 * @since 1.0
 */
public abstract class AbstractSpringAnnotationMapper implements NamedAnnotationMapper {
    @Override
    public final List<AnnotationValue<?>> map(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        if (annotation == null || visitorContext == null) {
            return Collections.emptyList();
        }

        if (VersionUtils.isAtLeastMicronautVersion("1.0.1")) {
            return mapInternal(annotation, visitorContext);
        } else {
            visitorContext.info("Annotation mapper [" + getClass().getName() + "] requires Micronaut 1.0.1 or above. Please upgrade to continue.", null);
            return Collections.emptyList();
        }
    }

    /**
     * Internal map implemenation that subclasses should implement.
     * @param annotation The annotation
     * @param visitorContext The visitor context
     * @return A list of annotations
     */
    protected abstract @Nonnull List<AnnotationValue<?>> mapInternal(
            @Nonnull AnnotationValue<Annotation> annotation,
            @Nonnull VisitorContext visitorContext);
}
