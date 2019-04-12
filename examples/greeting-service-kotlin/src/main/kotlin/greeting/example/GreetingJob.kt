package greeting.example;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class GreetingJob(val greetingService: GreetingService) {

    @Scheduled(fixedDelayString = "30s")
    fun printLastGreeting() {
        val lastGreeting = greetingService.getLastGreeting()
        lastGreeting.ifPresent {
            var content = it.content
            println("Last Greeting was = $content")
        }
    }
}
