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
package io.micronaut.spring.web.annotation.exchange;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.http.annotation.Post;
import org.jspecify.annotations.Nullable;

/**
 * Maps Spring PostExchange to Micronaut.
 *
 * @since 5.10.0
 */
public class PostExchangeAnnotationTransformer extends HttpExchangeAnnotationTransformer {

    @Override
    public String getName() {
        return "org.springframework.web.service.annotation.PostExchange";
    }

    @Override
    protected AnnotationValueBuilder<?> newBuilder(@Nullable String httpMethod) {
        return AnnotationValue.builder(Post.class);
    }

    @Override
    protected boolean isHttpMethodMapping(@Nullable String method) {
        return true;
    }
}
