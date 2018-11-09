package io.micronaut.spring.boot.annotation;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("foo.bar")
public class MyConfigurationProperties {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
