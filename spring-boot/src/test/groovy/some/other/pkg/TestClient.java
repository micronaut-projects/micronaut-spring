package some.other.pkg;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;

@Client("http://localhost:${spring.test.port}/spring/test")
public interface TestClient {

    @Get("/")
    String hello();
}
