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

import io.micronaut.http.HttpRequest;
import io.micronaut.http.server.netty.HttpContentProcessor;
import io.netty.channel.Channel;
import jakarta.inject.Singleton;

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

    /**
     * Always returns {@link Optional#empty()}; this implementation resolves no content processor.
     *
     * @param request The request
     * @return Always {@link Optional#empty()}
     * @deprecated {@link HttpContentProcessor} is deprecated for removal in Micronaut core and is
     * no longer used by the Micronaut HTTP server. This method will be removed from
     * {@link ChannelResolver} in the next major version.
     */
    @Override
    @Deprecated(since = "6.2.0", forRemoval = true)
    public Optional<HttpContentProcessor> resolveContentProcessor(HttpRequest<?> request) {
        return Optional.empty();
    }
}
