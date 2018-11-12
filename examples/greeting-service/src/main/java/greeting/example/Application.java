package greeting.example;

import io.micronaut.runtime.Micronaut;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;

@OpenAPIDefinition(
    info = @Info(
        title = "Greeting Service",
        version = "0.0",
        description = "Implements a Greeting API API",
        license = @License(name = "Apache 2.0", url = "http://foo.bar")
    )
)
@SpringBootApplication
public class Application {

    public static void main(String... args) {
        Micronaut.run(Application.class);
//        org.springframework.boot.SpringApplication.run(Application.class);
    }
}
