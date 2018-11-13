package io.micronaut.spring.annotation.cache;

import io.micronaut.cache.annotation.CacheInvalidate;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;

import java.lang.annotation.Annotation;

public class CacheEvictAnnotationMapper extends CacheableAnnotationMapper {

    @Override
    public String getName() {
        return "org.springframework.cache.annotation.CacheEvict";
    }

    @Override
    protected AnnotationValueBuilder<? extends Annotation> buildAnnotation() {
        return AnnotationValue.builder(CacheInvalidate.class);
    }
}
