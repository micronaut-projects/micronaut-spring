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
package io.micronaut.spring.annotation.context;

import io.micronaut.core.annotation.AnnotationUtil;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps {@code @Autowired} to javax.inject.Inject.
 *
 * @author graemerocher
 * @since 1.0
 */
public class AutowiredAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.beans.factory.annotation.Autowired";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final boolean required = annotation.getValue(Boolean.class).orElse(true);

        List<AnnotationValue<?>> annotations = new ArrayList<>(2);
        annotations.add(AnnotationValue.builder(AnnotationUtil.INJECT).build());
        if (!required) {
            annotations.add(AnnotationValue.builder(Nullable.class).build());
        }
        return annotations;
    }
}
