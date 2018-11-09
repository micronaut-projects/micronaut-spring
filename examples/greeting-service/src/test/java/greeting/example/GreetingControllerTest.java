package greeting.example;

import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class GreetingControllerTest {

    @Inject
    GreetingClient greetingClient;

    @Test
    void testGreetingService() {
        assertEquals(
                "Hola, John!",
                greetingClient.greet("John").getContent()
        );
    }
}
