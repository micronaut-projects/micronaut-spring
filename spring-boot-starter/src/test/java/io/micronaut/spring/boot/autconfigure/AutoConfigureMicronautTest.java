package io.micronaut.spring.boot.autconfigure;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.codec.MediaTypeCodecRegistry;
import io.micronaut.spring.boot.starter.EnableMicronaut;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AutoConfigureMicronautTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    MediaTypeCodecRegistry codecRegistry;

    @Test
    void testEnableMicronaut() {
        assertNotNull(context);
        assertNotNull(codecRegistry);
        assertTrue(context.isRunning());
    }
}


@SpringBootApplication
class Application {

}
