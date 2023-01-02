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

import java.util.Optional;

/**
 * Channel resolver interface.
 *
 * @author graemerocher
 * @since 1.0
 */
public interface ChannelResolver {

    /**
     * Resolve the backing netty channel.
     * @param request The request
     * @return The channel
     */
    Optional<Channel> resolveChannel(HttpRequest<?> request);

    /**
     * Resolve the content processor.
     * @param request The request
     * @return The processor
     */
    Optional<HttpContentProcessor> resolveContentProcessor(HttpRequest<?> request);
}
