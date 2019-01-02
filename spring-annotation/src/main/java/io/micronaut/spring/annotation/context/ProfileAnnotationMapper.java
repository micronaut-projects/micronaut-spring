package io.micronaut.spring.annotation.context;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ProfileAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        Optional<String[]> value = annotation.getValue(String[].class);
        return value.<List<AnnotationValue<?>>>map(strings -> Collections.singletonList(
                AnnotationValue.builder(Requires.class)
                        .member("env", strings).build()
        )).orElse(Collections.emptyList());
    }

    @Override
    public String getName() {
        return "org.springframework.context.annotation.Profile";
    }
}
