package greeting.example

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*

import javax.validation.constraints.Pattern

@RestController
class GreetingController(val greetingService: GreetingService) {

    @GetMapping("/greeting")
    fun greeting(@RequestParam(value = "name", defaultValue = "World") @Pattern(regexp = "\\D+") name: String): Greeting {
        return greetingService.greeting(name)
    }

    @PostMapping("/greeting")
    fun greetingByPost(@RequestBody greeting: Greeting): Greeting {
        return greetingService.greeting(greeting.content)
    }

    @DeleteMapping("/greeting")
    @Hidden
    fun deleteGreeting(): ResponseEntity<Any> {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add("Foo", "Bar")
        return ResponseEntity(headers, HttpStatus.NO_CONTENT)
    }


    @GetMapping("/greeting-status")
    @ResponseStatus(code = HttpStatus.CREATED)
    fun greetingWithStatus(
            @RequestParam(value = "name", defaultValue = "World")
            @Pattern(regexp = "\\D+") name: String): Greeting {
        return greetingService.greeting(name)
    }

    @GetMapping(path = ["/"], produces = ["text/html"])
    fun home(model: Model): String {
        model.addAttribute(
                "message",
                "Welcome to Micronaut for Spring")
        return "home"
    }
}