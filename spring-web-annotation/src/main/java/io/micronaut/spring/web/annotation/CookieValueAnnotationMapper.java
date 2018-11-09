package io.micronaut.spring.web.annotation;

import io.micronaut.http.annotation.CookieValue;

public class CookieValueAnnotationMapper extends WebBindAnnotationMapper<CookieValue> {
    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.CookieValue";
    }

    @Override
    Class<CookieValue> annotationType() {
        return CookieValue.class;
    }
}
