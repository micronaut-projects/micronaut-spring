package greeting.example;


import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Validated
public class GreetingService {

    private final AtomicLong counter = new AtomicLong();

    private final GreetingConfiguration greetingConfiguration;

    public GreetingService(GreetingConfiguration greetingConfiguration) {
        this.greetingConfiguration = greetingConfiguration;
    }

    public Greeting greeting( @Pattern(regexp = "\\D+") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(greetingConfiguration.getTemplate(), name));
    }
}
