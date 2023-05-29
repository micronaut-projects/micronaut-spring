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

import io.micronaut.core.annotation.Internal;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;
import io.micronaut.http.server.HttpServerConfiguration;
import io.micronaut.spring.web.reactive.ChannelResolver;
import io.micronaut.spring.web.reactive.MicronautServerHttpRequest;
import jakarta.inject.Singleton;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Optional;

/**
 * Binds the {@link ServerHttpRequest} object.
 *
 * @author graemerocher
 * @since 1.0
 */
@Singleton
@Internal
public class ServerHttpRequestBinder implements TypedRequestArgumentBinder<ServerHttpRequest> {

    private final ChannelResolver channelResolver;
    private final HttpServerConfiguration serverConfiguration;

    /**
     * The channel resolver.
     *
     * @param channelResolver     The channel resolver
     * @param serverConfiguration The server configuration
     */
    public ServerHttpRequestBinder(ChannelResolver channelResolver, HttpServerConfiguration serverConfiguration) {
        this.channelResolver = channelResolver;
        this.serverConfiguration = serverConfiguration;
    }

    @Override
    public Argument<ServerHttpRequest> argumentType() {
        return Argument.of(ServerHttpRequest.class);
    }

    @Override
    public BindingResult<ServerHttpRequest> bind(ArgumentConversionContext<ServerHttpRequest> context, HttpRequest<?> source) {
        return () -> Optional.of(new MicronautServerHttpRequest(
                source,
                channelResolver,
                serverConfiguration
        ));
    }
}
