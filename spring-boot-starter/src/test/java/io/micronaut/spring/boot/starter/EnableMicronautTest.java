package io.micronaut.spring.boot.starter;

import java.util.List;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.http.codec.MediaTypeCodecRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void testEnableMicronaut() {
        assertNotNull(context);
        assertNotNull(codecRegistry);
        assertNotNull(fooBean);
        assertEquals("default", fooBean.getConfiguration().getName());
        assertEquals(3, fooBeanList.size());
        assertTrue(context.isRunning());
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
