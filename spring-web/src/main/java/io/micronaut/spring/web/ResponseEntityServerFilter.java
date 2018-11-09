package io.micronaut.spring.web;

import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpHeaders;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * A filter that adds support for {@link ResponseEntity} as a return type.
 *
 * @author graemerocher
 * @since 1.0
 */
@Filter("/**")
public class ResponseEntityServerFilter implements HttpServerFilter {
    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        final Publisher<MutableHttpResponse<?>> responsePublisher = chain.proceed(request);
        return Publishers.map(responsePublisher, mutableHttpResponse -> {
            final Object body = mutableHttpResponse.body();
            if (body instanceof ResponseEntity) {
                ResponseEntity entity = (ResponseEntity) body;
                mutableHttpResponse.status(entity.getStatusCodeValue());
                final HttpHeaders headers = entity.getHeaders();
                final MutableHttpHeaders micronautHeaders = mutableHttpResponse.getHeaders();
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    final String key = entry.getKey();
                    final List<String> value = entry.getValue();
                    for (String v : value) {
                        micronautHeaders.add(key, v);
                    }
                }
                final Object b = entity.getBody();
                if (b != null) {
                    ((MutableHttpResponse<Object>)mutableHttpResponse).body(b);
                }
            }
            return mutableHttpResponse;
        });
    }
}
