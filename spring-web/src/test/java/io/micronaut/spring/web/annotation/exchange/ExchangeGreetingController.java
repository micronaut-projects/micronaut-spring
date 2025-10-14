/*
 * Copyright 2017-2024 original authors
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
package io.micronaut.spring.web.annotation.exchange;

import io.micronaut.http.HttpResponse;
import io.micronaut.spring.web.annotation.Greeting;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

@RestController
@RequestMapping("/exchange")
public class ExchangeGreetingController implements ExchangeGreetingApi {

    private static final String TEMPLATE_NESTED = "Hello Nested, %s!";
    private static final String TEMPLATE = "Hello, %s!";

    private final AtomicLong counter = new AtomicLong();

    @Override
    public String home(@Nullable Model model) {
        model.addAttribute("message", "Welcome to Micronaut for Spring!");
        return "welcome";
    }

    @Override
    public String withOptVar(@Nullable String optVar) {
        return optVar == null ? "optVar is null!" : String.format(TEMPLATE, optVar);
    }

    @Override
    public Greeting greeting(@Nullable String name) {
        return new Greeting(counter.incrementAndGet(), TEMPLATE.formatted(name));
    }

    @Override
    public Greeting greetingByPost(Greeting greeting) {
        return new Greeting(counter.incrementAndGet(), TEMPLATE.formatted(greeting.getContent()));
    }

    @Override
    public HttpResponse<?> deleteGreeting(@Nullable String myHeader) {
        var headers = new HashMap<CharSequence, CharSequence>();
        headers.put("Foo", "Bar");
        if (myHeader != null) {
            headers.put("myHeader", myHeader);
        }
        return HttpResponse.noContent().headers(headers);
    }

    @Override
    public Greeting greetingWithStatus(String name) {
        return new Greeting(counter.incrementAndGet(), TEMPLATE.formatted(name));
    }

    @Override
    public Greeting greetingNested(String name) {
        return new Greeting(counter.incrementAndGet(), String.format(TEMPLATE_NESTED, name));
    }

    @PostExchange("/request")
    public Flux<String> request(ServerHttpRequest request, HttpMethod method) {
        assertEquals("/exchange/request", request.getPath().value());
        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals(HttpMethod.POST, method);
        assertEquals("Bar", request.getHeaders().getFirst("Foo"));
        return request.getBody().map(dataBuffer -> {
            var bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        });
    }
}
