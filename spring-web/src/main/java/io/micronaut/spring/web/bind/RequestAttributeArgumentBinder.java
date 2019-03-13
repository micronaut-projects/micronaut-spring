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
package io.micronaut.spring.web.bind;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.AnnotatedRequestArgumentBinder;
import org.springframework.web.bind.annotation.RequestAttribute;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
@Requires(classes = RequestAttribute.class)
public class RequestAttributeArgumentBinder implements AnnotatedRequestArgumentBinder<RequestAttribute, Object> {

    @Override
    public Class<RequestAttribute> getAnnotationType() {
        return RequestAttribute.class;
    }

    @Override
    public BindingResult<Object> bind(ArgumentConversionContext<Object> context, HttpRequest<?> source) {
        final AnnotationMetadata annotationMetadata = context.getAnnotationMetadata();
        final boolean required = annotationMetadata.getValue("required", boolean.class).orElse(true);
        final String name = annotationMetadata.getValue(RequestAttribute.class, String.class).orElseGet(() ->
                annotationMetadata.getValue(RequestAttribute.class, "name", String.class).orElse(context.getArgument().getName())
        );

        return new BindingResult<Object>() {
            @Override
            public Optional<Object> getValue() {
                return source.getAttributes().get(name, context);
            }

            @Override
            public boolean isSatisfied() {
                return !required;
            }
        };
    }
}
