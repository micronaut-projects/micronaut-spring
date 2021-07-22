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
package io.micronaut.spring.web.reactive;

import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.server.HttpServerConfiguration;
import io.micronaut.http.server.netty.DefaultHttpContentProcessor;
import io.micronaut.http.server.netty.HttpContentProcessor;
import io.micronaut.http.server.netty.NettyHttpRequest;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * Micronaut specific {@link ChannelResolver} implementation.
 *
 * @author graemerocher
 * @since 1.0
 */
@Singleton
@Requires(classes = NettyHttpRequest.class)
@Requires(beans = HttpServerConfiguration.class)
@Replaces(DefaultChannelResolver.class)
@Primary
public class MicronautNettyChannelResolver implements ChannelResolver {

    private final HttpServerConfiguration serverConfiguration;

    /**
     * Default constructor.
     * @param serverConfiguration The server config
     */
    public MicronautNettyChannelResolver(HttpServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;
    }

    @Override
    public Optional<Channel> resolveChannel(HttpRequest<?> request) {
        if (request instanceof NettyHttpRequest) {
            final Channel channel = ((NettyHttpRequest<?>) request).getChannelHandlerContext().channel();
            return Optional.of(channel);
        }
        return Optional.empty();
    }

    @Override
    public Optional<HttpContentProcessor<ByteBufHolder>> resolveContentProcessor(HttpRequest<?> request) {
        if (request instanceof NettyHttpRequest) {
            final NettyHttpRequest<?> nettyHttpRequest = (NettyHttpRequest<?>) request;
            return Optional.of(
                    new DefaultHttpContentProcessor(
                            nettyHttpRequest,
                            serverConfiguration
                    )
            );
        }
        return Optional.empty();
    }
}
