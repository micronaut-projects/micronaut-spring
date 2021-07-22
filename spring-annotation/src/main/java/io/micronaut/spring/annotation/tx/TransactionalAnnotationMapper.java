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
package io.micronaut.spring.annotation.tx;

import io.micronaut.aop.InterceptorBinding;
import io.micronaut.aop.InterceptorKind;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * Maps {@code @Transactional} to {@code io.micronaut.spring.tx.annotation.Transactional}.
 *
 * @author graemerocher
 * @since 1.0
 */
public class TransactionalAnnotationMapper implements NamedAnnotationMapper {

    @Override
    public String getName() {
        return "org.springframework.transaction.annotation.Transactional";
    }

    @Override
    public List<AnnotationValue<?>> map(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        return Collections.singletonList(
                AnnotationValue.builder(InterceptorBinding.class)
                        .value(getName())
                        .member("kind", InterceptorKind.AROUND)
                        .build()
        );
    }
}
