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
package io.micronaut.spring.web.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.ArrayUtils;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Head;
import io.micronaut.http.annotation.HttpMethodMapping;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Trace;
import io.micronaut.inject.annotation.NamedAnnotationTransformer;
import io.micronaut.inject.visitor.VisitorContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps Spring RequestMapping to Micronaut.
 *
 * @author graemerocher
 * @since 1.0
 */
public class RequestMappingAnnotationTransformer implements NamedAnnotationTransformer {
    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.RequestMapping";
    }

    /**
     * Whether the given method is an HTTP method mapping.
     * @param method The method, can be null
     * @return True if it is
     */
    protected boolean isHttpMethodMapping(@Nullable HttpMethod method) {
        return method != null;
    }

    /**
     * Construct a new builder for the given http method.
     * @param httpMethod The method
     * @param annotation The annotation
     * @return The builder
     */
    protected @NonNull AnnotationValueBuilder<?> newBuilder(
            @Nullable HttpMethod httpMethod,
            AnnotationValue<Annotation> annotation) {

        if (httpMethod != null) {
            return switch (httpMethod) {
                case TRACE -> AnnotationValue.builder(Trace.class);
                case DELETE -> AnnotationValue.builder(Delete.class);
                case GET -> AnnotationValue.builder(Get.class);
                case HEAD -> AnnotationValue.builder(Head.class);
                case POST -> AnnotationValue.builder(Post.class);
                case PUT -> AnnotationValue.builder(Put.class);
                case PATCH -> AnnotationValue.builder(Patch.class);
                case OPTIONS -> AnnotationValue.builder(Options.class);
                default -> AnnotationValue.builder("io.micronaut.http.annotation.UriMapping");
            };
        } else {
            return AnnotationValue.builder("io.micronaut.http.annotation.UriMapping");
        }
    }

    private String computePath(AnnotationValue<Annotation> annotation) {
        return annotation.stringValue().orElseGet(() -> annotation.stringValue("path").orElse("/"));
    }

    @Override
    public List<AnnotationValue<?>> transform(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        var annotations = new ArrayList<AnnotationValue<?>>();

        final String path = computePath(annotation);
        var method = annotation.enumValue("method", HttpMethod.class).orElse(null);

        annotations.add(newBuilder(method, annotation).value(path).build());

        final String[] consumes = annotation.stringValues("consumes");
        final String[] produces = annotation.stringValues("produces");

        if (ArrayUtils.isNotEmpty(consumes)) {
            annotations.add(AnnotationValue.builder(Consumes.class).member("value", consumes).build());
        }
        if (ArrayUtils.isNotEmpty(produces)) {
            annotations.add(AnnotationValue.builder(Produces.class).member("value", produces).build());
        }

        if (isHttpMethodMapping(method)) {
            annotations.add(AnnotationValue.builder(HttpMethodMapping.class).value(path).build());
        }
        return annotations;
    }
}
