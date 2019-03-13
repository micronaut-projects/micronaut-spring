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
package io.micronaut.spring.boot.annotation;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.AnnotationClassValue;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Maps ConditionalOnBean to Micronaut Requires.
 *
 * @author graemerocher
 * @since 1.0
 */
public class ConditionalOnBeanAnnotationMapper extends AbstractSpringAnnotationMapper {

    @Override
    public String getName() {
        return "org.springframework.boot.autoconfigure.condition.ConditionalOnBean";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final Optional<AnnotationClassValue[]> classValues = annotation.getValue(AnnotationClassValue[].class);
        if (classValues.isPresent()) {
            final AnnotationClassValue<?>[] types = classValues.get();
            return Collections.singletonList(
                    AnnotationValue.builder(Requires.class)
                            .member(requiresMethodName(), types).build()
            );
        } else {
            final Optional<String[]> types = annotation.get(typesMemberName(), String[].class);
            if (types.isPresent()) {
                final AnnotationClassValue[] classesValues = Arrays.stream(types.get()).map(AnnotationClassValue::new).toArray(AnnotationClassValue[]::new);
                return Collections.singletonList(
                        AnnotationValue.builder(Requires.class)
                                .member(requiresMethodName(), classesValues).build()
                );
            }
        }

        return Collections.emptyList();
    }

    /**
     * The annotation member name that specifies the types.
     * @return The name
     */
    protected @Nonnull String typesMemberName() {
        return "types";
    }

    /**
     * The annotation member name for requires.
     * @return The member name
     */
    protected @Nonnull String requiresMethodName() {
        return "beans";
    }
}
