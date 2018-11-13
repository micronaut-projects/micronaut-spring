package io.micronaut.spring.annotation.cache;

import io.micronaut.cache.annotation.CachePut;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;

import java.lang.annotation.Annotation;

public class CachePutAnnotationMapper extends CacheableAnnotationMapper {

    @Override
    public String getName() {
        return "org.springframework.cache.annotation.CachePut";
    }

    @Override
    protected AnnotationValueBuilder<? extends Annotation> buildAnnotation() {
        return AnnotationValue.builder(CachePut.class);
    }
}
