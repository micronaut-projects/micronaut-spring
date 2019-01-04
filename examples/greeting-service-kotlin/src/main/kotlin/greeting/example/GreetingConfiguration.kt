package greeting.example;

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("greeting")
class GreetingConfiguration {
    var template: String = "Hello, %s!"
}