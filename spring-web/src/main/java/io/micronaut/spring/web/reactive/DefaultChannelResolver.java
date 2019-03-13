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
package io.micronaut.spring.web.reactive;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.server.netty.HttpContentProcessor;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;

import javax.inject.Singleton;
import java.util.Optional;

/**
 * Default implementation of {@link ChannelResolver}.
 *
 * @author graemerocher
 * @since 1.0
 */
@Singleton
public class DefaultChannelResolver implements ChannelResolver {
    @Override
    public Optional<Channel> resolveChannel(HttpRequest<?> request) {
        return Optional.empty();
    }

    @Override
    public Optional<HttpContentProcessor<ByteBufHolder>> resolveContentProcessor(HttpRequest<?> request) {
        return Optional.empty();
    }
}
