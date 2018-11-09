package io.micronaut.spring.annotation.context;

import io.micronaut.context.annotation.Primary;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * Maps {@code @Primary} to {@link Primary}.
 *
 * @author graemerocher
 * @since 1.0
 *
 */
public class PrimaryAnnotationMapper extends AbstractSpringAnnotationMapper  {
    @Override
    public String getName() {
        return "org.springframework.context.annotation.Primary";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        return Collections.singletonList(
                AnnotationValue.builder(Primary.class).build()
        );
    }
}
