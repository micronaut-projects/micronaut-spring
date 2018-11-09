package io.micronaut.spring.web.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.annotation.Patch;

import java.lang.annotation.Annotation;

public class PatchMappingAnnotationMapper extends RequestMappingAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.PatchMapping";
    }

    @Override
    protected AnnotationValueBuilder<?> newBuilder(HttpMethod httpMethod, AnnotationValue<Annotation> annotation) {
        return AnnotationValue.builder(Patch.class);
    }

    @Override
    protected boolean isHttpMethodMapping(HttpMethod method) {
        return true;
    }
}
