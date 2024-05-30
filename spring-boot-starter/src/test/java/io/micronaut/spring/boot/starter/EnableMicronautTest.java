package io.micronaut.spring.boot.starter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.http.codec.MediaTypeCodecRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
    classes = Application.class,
    properties = {
        "foos.one.id=1",
        "foos.two.id=2",
        "foos.default.id=0"
    }
)
class EnableMicronautTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    MediaTypeCodecRegistry codecRegistry;

    @Autowired
    FooBean fooBean;

    @Autowired
    List<FooBean> fooBeanList;

    @Autowired
    CommonBeans commonBeans;

    @Test
    void testEnableMicronaut() {
        assertNotNull(context);
        assertNotNull(codecRegistry);
        assertNotNull(fooBean);
        assertEquals("default", fooBean.getConfiguration().getName());
        assertEquals(3, fooBeanList.size());
        assertTrue(context.isRunning());
        assertFalse(
            context.getAllBeanDefinitions()
                   .stream()
                   .map(bean -> bean.getBeanType())
                   .anyMatch(Objects::isNull)
        );
    }
}

@EnableMicronaut
class Application {

}

@EachProperty(value = "foos", primary = "default")
class FooConfiguration {
    private final String name;

    FooConfiguration(@Parameter String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

@EachBean(FooConfiguration.class)
class FooBean {
    private final FooConfiguration configuration;

    FooBean(@Parameter FooConfiguration configuration) {
        this.configuration = configuration;
    }

    public FooConfiguration getConfiguration() {
        return configuration;
    }
}

@Bean
class CommonBeans {
    private final BeanFactory beanFactory;
    private final Environment env;

    CommonBeans(BeanFactory beanFactory, Environment env) {
        this.beanFactory = beanFactory;
        this.env = env;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public Environment getEnv() {
        return env;
    }
}
