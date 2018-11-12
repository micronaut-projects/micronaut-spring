package io.micronaut.spring.web.reactive;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.server.netty.HttpContentProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;

import javax.inject.Singleton;
import java.util.Optional;

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
