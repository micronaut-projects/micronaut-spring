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
package io.micronaut.spring.web.bind;

import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;
import jakarta.inject.Singleton;
import org.springframework.http.HttpMethod;

import java.util.Optional;

/**
 * Adds ability to bind {@link HttpMethod}.
 *
 * @author graemerocher
 * @since 1.0
 */
@Singleton
public class HttpMethodArgumentBinder implements TypedRequestArgumentBinder<HttpMethod> {
    @Override
    public Argument<HttpMethod> argumentType() {
        return Argument.of(HttpMethod.class);
    }

    @Override
    public BindingResult<HttpMethod> bind(ArgumentConversionContext<HttpMethod> context, HttpRequest<?> source) {
        return () -> Optional.of(HttpMethod.valueOf(source.getMethod().name()));
    }
}
