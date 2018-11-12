package io.micronaut.spring.web.bind;

import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;
import org.springframework.http.HttpMethod;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class HttpMethodArgumentBinder implements TypedRequestArgumentBinder<HttpMethod> {
    @Override
    public Argument<HttpMethod> argumentType() {
        return Argument.of(HttpMethod.class);
    }

    @Override
    public BindingResult<HttpMethod> bind(ArgumentConversionContext<HttpMethod> context, HttpRequest<?> source) {
        return () -> Optional.of(HttpMethod.valueOf(source.getMethod().name()));
    }
}
