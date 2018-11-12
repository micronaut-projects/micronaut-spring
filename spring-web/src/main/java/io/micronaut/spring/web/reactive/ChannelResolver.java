package io.micronaut.spring.web.reactive;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.server.netty.HttpContentProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;

import java.util.Optional;

public interface ChannelResolver {
    Optional<Channel> resolveChannel(HttpRequest<?> request);

    Optional<HttpContentProcessor<ByteBufHolder>> resolveContentProcessor(HttpRequest<?> request);
}
