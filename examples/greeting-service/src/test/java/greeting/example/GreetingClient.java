package greeting.example;


import io.micronaut.http.client.annotation.Client;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;

@Client("/")
public interface GreetingClient {

    @GetMapping("/greeting{?name}")
    Greeting greet(@Nullable String name);

    @PostMapping("/greeting")
    Greeting greetByPost(@RequestBody Greeting greeting);

    @DeleteMapping("/greeting")
    HttpStatus deletePost();

    @GetMapping("/nested/greeting{?name}")
    Greeting nestedGreet(@Nullable String name);

    @GetMapping("/greeting-status{?name}")
    ResponseEntity<Greeting> greetWithStatus(@Nullable String name);
}
