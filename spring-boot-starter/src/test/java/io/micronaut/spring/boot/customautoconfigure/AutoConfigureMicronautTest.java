package io.micronaut.spring.boot.customautoconfigure;

import java.util.Map;

import javax.sql.DataSource;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.codec.MediaTypeCodecRegistry;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.spring.boot.starter.EnableMicronaut;
import io.micronaut.spring.boot.starter.MicronautBeanFilter;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AutoConfigureMicronautTest {
    @Autowired
    ReceiveDatasource datasource;

    @Autowired
    ApplicationContext context;

    @Autowired
    ListableBeanFactory beanFactory;

    @Test
    void testEnableMicronaut() {
        assertNotNull(context);
        assertTrue(context.isRunning());
        Map<String, MediaTypeCodecRegistry> beansOfType = beanFactory.getBeansOfType(MediaTypeCodecRegistry.class);
        assertTrue(beansOfType.isEmpty());
    }
}


@SpringBootApplication
@EnableMicronaut(filter = MyFilter.class, exposeToMicronaut =  @EnableMicronaut.ExposedBean(
    beanType = DataSource.class
))
class Application {

}

class MyFilter implements MicronautBeanFilter {
    @Override
    public boolean excludes(@NonNull BeanDefinition<?> definition) {
        return MediaTypeCodecRegistry.class.isAssignableFrom(definition.getBeanType());
    }
}
@Singleton
class ReceiveDatasource {
    @Inject
    DataSource dataSource;
}
