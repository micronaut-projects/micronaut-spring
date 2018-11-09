package io.micronaut.spring.web.annotation;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") @Pattern(regexp = "\\D+") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @PostMapping("/greeting")
    public Greeting greetingByPost(@RequestBody Greeting greeting) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, greeting.getContent()));
    }

    @DeleteMapping("/greeting")
    public ResponseEntity<?> deleteGreeting() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Foo", "Bar");
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }


    @RequestMapping("/greeting-status")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Greeting greetingWithStatus(@RequestParam(value="name", defaultValue="World") @Pattern(regexp = "\\D+") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }
}