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
package io.micronaut.spring.web.annotation.exchange;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import io.micronaut.core.util.ArrayUtils;
import io.micronaut.core.util.StringUtils;
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
import io.micronaut.http.annotation.UriMapping;
import io.micronaut.inject.annotation.NamedAnnotationTransformer;
import io.micronaut.inject.visitor.VisitorContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Maps Spring HttpExchange to Micronaut.
 *
 * @since 5.10.0
 */
public class HttpExchangeAnnotationTransformer implements NamedAnnotationTransformer {

    @Override
    public String getName() {
        return "org.springframework.web.service.annotation.HttpExchange";
    }

    /**
     * Whether the given method is an HTTP method mapping.
     * @param method The method, can be null
     * @return True if it is
     */
    protected boolean isHttpMethodMapping(@Nullable String method) {
        return method != null;
    }

    /**
     * Construct a new builder for the given http method.
     * @param method HTTP method
     * @return The builder
     */
    @NonNull
    protected AnnotationValueBuilder<?> newBuilder(@Nullable String method) {

        if (method != null) {
            return switch (method.toUpperCase(Locale.ROOT)) {
                case "GET" -> AnnotationValue.builder(Get.class);
                case "POST" -> AnnotationValue.builder(Post.class);
                case "PATCH" -> AnnotationValue.builder(Patch.class);
                case "PUT" -> AnnotationValue.builder(Put.class);
                case "DELETE" -> AnnotationValue.builder(Delete.class);
                case "HEAD" -> AnnotationValue.builder(Head.class);
                case "OPTIONS" -> AnnotationValue.builder(Options.class);
                case "TRACE" -> AnnotationValue.builder(Trace.class);
                default -> AnnotationValue.builder(UriMapping.class);
            };
        } else {
            return AnnotationValue.builder(UriMapping.class);
        }
    }

    private String computePath(AnnotationValue<Annotation> annotation) {
        return annotation.stringValue().orElseGet(() -> annotation.stringValue("url").orElse(UriMapping.DEFAULT_URI));
    }

    @Override
    public List<AnnotationValue<?>> transform(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        var annotations = new ArrayList<AnnotationValue<?>>();

        final String path = computePath(annotation);
        var method = annotation.stringValue("method").orElse(null);

        annotations.add(newBuilder(method).value(path).build());

        var contentType = annotation.stringValue("contentType").orElse(null);
        if (StringUtils.isNotEmpty(contentType)) {
            annotations.add(AnnotationValue.builder(Consumes.class).member("contentType", contentType).build());
        }

        final String[] accept = annotation.stringValues("accept");
        if (ArrayUtils.isNotEmpty(accept)) {
            annotations.add(AnnotationValue.builder(Produces.class).member("value", accept).build());
        }

        if (isHttpMethodMapping(method)) {
            annotations.add(AnnotationValue.builder(HttpMethodMapping.class).value(path).build());
        }
        return annotations;
    }
}
