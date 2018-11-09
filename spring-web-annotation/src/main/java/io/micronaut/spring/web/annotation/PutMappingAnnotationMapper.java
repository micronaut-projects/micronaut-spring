package io.micronaut.spring.web.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.annotation.Put;

import java.lang.annotation.Annotation;

public class PutMappingAnnotationMapper extends RequestMappingAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.PutMapping";
    }

    @Override
    protected AnnotationValueBuilder<?> newBuilder(HttpMethod httpMethod, AnnotationValue<Annotation> annotation) {
        return AnnotationValue.builder(Put.class);
    }

    @Override
    protected boolean isHttpMethodMapping(HttpMethod method) {
        return true;
    }
}
