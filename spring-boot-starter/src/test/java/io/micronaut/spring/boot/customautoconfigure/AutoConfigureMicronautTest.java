package io.micronaut.spring.boot.customautoconfigure;

import java.util.Map;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.codec.MediaTypeCodecRegistry;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.spring.boot.starter.EnableMicronaut;
import io.micronaut.spring.boot.starter.MicronautBeanFilter;
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
@EnableMicronaut(filter = MyFilter.class)
class Application {

}

class MyFilter implements MicronautBeanFilter {
    @Override
    public boolean excludes(BeanDefinition<?> definition) {
        return MediaTypeCodecRegistry.class.isAssignableFrom(definition.getBeanType());
    }
}
