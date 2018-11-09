package io.micronaut.spring.web.annotation;

import io.micronaut.http.annotation.QueryValue;

public class RequestParamAnnotationMapper extends WebBindAnnotationMapper<QueryValue> {
    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.RequestParam";
    }

    @Override
    Class<QueryValue> annotationType() {
        return QueryValue.class;
    }
}
