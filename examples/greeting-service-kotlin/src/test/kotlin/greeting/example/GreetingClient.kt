package greeting.example


import io.micronaut.http.HttpStatus
import io.micronaut.http.client.annotation.Client
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.annotation.Nullable

@Client("/")
interface GreetingClient {

    @GetMapping("/greeting{?name}")
    fun greet(@Nullable name : String? ) : Greeting

    @PostMapping("/greeting")
    fun greetByPost(@RequestBody greeting : Greeting) : Greeting

    @DeleteMapping("/greeting")
    fun deletePost() : HttpStatus

    @GetMapping("/nested/greeting{?name}")
    fun nestedGreet(@Nullable name : String?) : Greeting

    @GetMapping("/greeting-status{?name}")
    fun greetWithStatus(@Nullable name : String?) : ResponseEntity<Greeting>
}
