package io.micronaut.spring.web.annotation;

import io.micronaut.http.annotation.Header;

public class RequestHeaderAnnotationMapper extends WebBindAnnotationMapper<Header> {
    @Override
    Class<Header> annotationType() {
        return Header.class;
    }

    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.RequestHeader";
    }
}
