package io.micronaut.spring.web.bind;

import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;
import io.micronaut.spring.web.reactive.ChannelResolver;
import io.micronaut.spring.web.reactive.MicronautServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class ServerHttpRequestBinder implements TypedRequestArgumentBinder<ServerHttpRequest> {

    private final ChannelResolver channelResolver;

    public ServerHttpRequestBinder(ChannelResolver channelResolver) {
        this.channelResolver = channelResolver;
    }

    @Override
    public Argument<ServerHttpRequest> argumentType() {
        return Argument.of(ServerHttpRequest.class);
    }

    @Override
    public BindingResult<ServerHttpRequest> bind(ArgumentConversionContext<ServerHttpRequest> context, HttpRequest<?> source) {
        return () -> Optional.of(new MicronautServerHttpRequest(
                source,
                channelResolver
        ));
    }
}
