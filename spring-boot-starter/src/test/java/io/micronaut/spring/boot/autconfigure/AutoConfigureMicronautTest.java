package io.micronaut.spring.boot.autconfigure;

import javax.sql.DataSource;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.codec.MediaTypeCodecRegistry;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.test.database.replace=any")
public class  AutoConfigureMicronautTest {

    @Autowired
    DataSource dataSource;

    @Autowired
    ReceiveDatasource datasource;

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

@Singleton
class ReceiveDatasource {
    @Inject DataSource dataSource;
}
