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

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import javax.inject.Singleton;
import java.util.Optional;

/**
 * Binds the Spring {@link Model} argument for controllers.
 *
 * @author graemerocher
 * @since 1.0
 */
@Requires(classes = Model.class)
@Singleton
@Internal
public class ModelRequestArgumentBinder implements TypedRequestArgumentBinder<Model> {

    /**
     * The name of the request attribute to use.
     */
    public static final String ATTRIBUTE = "io.micronaut.spring.MODEL";

    @Override
    public BindingResult<Model> bind(ArgumentConversionContext<Model> context, HttpRequest<?> source) {
        final Optional<Model> attribute = source.getAttribute(ATTRIBUTE, Model.class);
        if (!attribute.isPresent()) {
            final ConcurrentModel concurrentModel = new ConcurrentModel();
            source.setAttribute(ATTRIBUTE, concurrentModel);
            return () -> Optional.of(concurrentModel);
        }
        return () -> attribute;
    }

    @Override
    public Argument<Model> argumentType() {
        return Argument.of(Model.class);
    }
}
