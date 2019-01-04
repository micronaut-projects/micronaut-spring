package greeting.example;


import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Validated
class GreetingService(
        val greetingConfiguration: GreetingConfiguration) {

    val counter: AtomicLong = AtomicLong()
    val lastGreeting: AtomicReference<Greeting> = AtomicReference()


    fun greeting(@Pattern(regexp = "\\D+") name: String): Greeting {
        val greeting = Greeting(counter.incrementAndGet(),
                String.format(greetingConfiguration.template, name)
        )
        lastGreeting.set(greeting)
        return greeting
    }

    fun getLastGreeting(): Optional<Greeting> {
        return Optional.ofNullable(lastGreeting.get())
    }
}
