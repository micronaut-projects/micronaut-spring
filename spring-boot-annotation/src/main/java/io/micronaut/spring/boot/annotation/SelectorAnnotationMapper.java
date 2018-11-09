package io.micronaut.spring.boot.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class SelectorAnnotationMapper implements NamedAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.boot.actuate.endpoint.annotation.Selector";
    }

    @Override
    public List<AnnotationValue<?>> map(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        return Collections.singletonList(
                AnnotationValue.builder("io.micronaut.management.endpoint.annotation.Selector").build()

        );
    }
}
