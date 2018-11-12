package io.micronaut.spring.web.reactive;

import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.server.HttpServerConfiguration;
import io.micronaut.http.server.netty.DefaultHttpContentProcessor;
import io.micronaut.http.server.netty.HttpContentProcessor;
import io.micronaut.http.server.netty.NettyHttpRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
@Requires(classes = NettyHttpRequest.class)
@Requires(beans = HttpServerConfiguration.class)
@Replaces(DefaultChannelResolver.class)
@Primary
public class MicronautNettyChannelResolver implements ChannelResolver {

    private final HttpServerConfiguration serverConfiguration;

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
