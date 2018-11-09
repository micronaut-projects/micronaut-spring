package io.micronaut.spring.annotation.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MyConfiguration {

    @Bean
    @Primary
    public MyBean myBean() {
        return new MyBean("default");
    }


    @Bean("another")
    public MyBean anotherBean() {
        return new MyBean("another");
    }


    public static class MyBean {
        private final String name;

        MyBean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
