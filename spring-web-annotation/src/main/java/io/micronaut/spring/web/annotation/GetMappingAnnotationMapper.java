package io.micronaut.spring.web.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.annotation.Get;

import java.lang.annotation.Annotation;

public class GetMappingAnnotationMapper extends RequestMappingAnnotationMapper {

    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.GetMapping";
    }

    @Override
    protected AnnotationValueBuilder<?> newBuilder(HttpMethod httpMethod, AnnotationValue<Annotation> annotation) {
        return AnnotationValue.builder(Get.class);
    }

    @Override
    protected boolean isHttpMethodMapping(HttpMethod method) {
        return true;
    }
}
