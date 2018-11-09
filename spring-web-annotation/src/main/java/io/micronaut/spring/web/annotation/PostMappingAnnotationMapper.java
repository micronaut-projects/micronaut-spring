package io.micronaut.spring.web.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.annotation.Post;

import java.lang.annotation.Annotation;

public class PostMappingAnnotationMapper extends RequestMappingAnnotationMapper {


    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.PostMapping";
    }

    @Override
    protected AnnotationValueBuilder<?> newBuilder(HttpMethod httpMethod, AnnotationValue<Annotation> annotation) {
        return AnnotationValue.builder(Post.class);
    }

    @Override
    protected boolean isHttpMethodMapping(HttpMethod method) {
        return true;
    }
}
