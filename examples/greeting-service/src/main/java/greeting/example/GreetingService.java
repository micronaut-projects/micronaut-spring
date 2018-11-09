package greeting.example;


import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Validated
public class GreetingService {

    private final AtomicLong counter = new AtomicLong();

    private final GreetingConfiguration greetingConfiguration;

    private AtomicReference<Greeting> lastGreeting = new AtomicReference<>();

    public GreetingService(GreetingConfiguration greetingConfiguration) {
        this.greetingConfiguration = greetingConfiguration;
    }

    public Greeting greeting( @Pattern(regexp = "\\D+") String name) {
        final Greeting greeting = new Greeting(counter.incrementAndGet(),
                String.format(greetingConfiguration.getTemplate(), name));
        lastGreeting.set(greeting);
        return greeting;
    }

    public Optional<Greeting> getLastGreeting() {
        return Optional.ofNullable(lastGreeting.get());
    }
}
