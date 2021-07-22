package greeting.example

import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

@MicronautTest
class GreetingControllerTest {

    @Inject
    lateinit var greetingClient : GreetingClient

    @Test
    fun testGreetingService() {
        assertEquals(
                "Hola, John!",
                greetingClient.greet("John").content
        )
    }
}
