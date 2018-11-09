package io.micronaut.spring.web.annotation;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

import javax.annotation.Nullable;

@Client("/")
public interface GreetingClient {

    @Get("/greeting{?name}")
    Greeting greet(@Nullable String name);

    @Post("/greeting")
    Greeting greetByPost(@Body Greeting greeting);

    @Delete("/greeting")
    HttpResponse<?> deletePost();

    @Get("/nested/greeting{?name}")
    Greeting nestedGreet(@Nullable String name);


    @Get("/greeting-status{?name}")
    HttpResponse<Greeting> greetWithStatus(@Nullable String name);
}
