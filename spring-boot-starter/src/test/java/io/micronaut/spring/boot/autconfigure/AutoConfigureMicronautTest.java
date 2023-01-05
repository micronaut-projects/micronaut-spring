package io.micronaut.spring.boot.autconfigure;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.codec.MediaTypeCodecRegistry;
import io.micronaut.spring.boot.starter.EnableMicronaut;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.test.database.replace=any")
public class AutoConfigureMicronautTest {

    @Autowired
    DataSource dataSource;

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

