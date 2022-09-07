package io.micronaut.spring.boot.starter;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.codec.MediaTypeCodecRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Application.class)
class EnableMicronautTest {

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

@EnableMicronaut
class Application {

}
