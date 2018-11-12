package io.micronaut.spring.web.annotation;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;

@Client("/")
public interface GreetingClient {

    @GetMapping("/")
    String home();

    @PostMapping("/request")
    @Header(name = "Foo", value = "Bar")
    Greeting requestTest(@RequestBody Greeting greeting);

    @GetMapping("/greeting{?name}")
    Greeting greet(@Nullable String name);

    @PostMapping("/greeting")
    Greeting greetByPost(@RequestBody Greeting greeting);

    @DeleteMapping("/greeting")
    HttpResponse<?> deletePost();

    @GetMapping("/nested/greeting{?name}")
    Greeting nestedGreet(@Nullable String name);


    @GetMapping("/greeting-status{?name}")
    HttpResponse<Greeting> greetWithStatus(@Nullable String name);
}
