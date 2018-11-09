package io.micronaut.spring.annotation.scheduling;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.scheduling.annotation.Async;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * Maps {@code @Async} to {@link Async}.
 *
 * @author graemerocher
 * @since 1.0
 */
public class AsyncAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.scheduling.annotation.Async";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final AnnotationValueBuilder<Async> builder = AnnotationValue.builder(Async.class);
        annotation.getValue(String.class).ifPresent(builder::value);
        return Collections.singletonList(builder.build());
    }
}
