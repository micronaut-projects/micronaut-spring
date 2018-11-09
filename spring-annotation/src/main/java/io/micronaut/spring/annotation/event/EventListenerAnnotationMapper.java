package io.micronaut.spring.annotation.event;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * Maps {@code @EventListener} to {@link io.micronaut.runtime.event.annotation.EventListener}
 *
 * @author graemerocher
 * @since 1.0
 */
public class EventListenerAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.context.event.EventListener";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        return Collections.singletonList(
                AnnotationValue.builder("io.micronaut.runtime.event.annotation.EventListener")
                        .build()
        );
    }
}
