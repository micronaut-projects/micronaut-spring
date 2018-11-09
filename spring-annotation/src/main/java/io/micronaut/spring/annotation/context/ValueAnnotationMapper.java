package io.micronaut.spring.annotation.context;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Maps {@code @Value} to {@link Value}
 *
 * @author graemerocher
 * @since 1.0
 */
public class ValueAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.beans.factory.annotation.Value";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final Optional<String> value = annotation.getValue(String.class);
        if (value.isPresent()) {
            return Collections.singletonList(
                    AnnotationValue.builder(Value.class)
                            .value(value.get())
                            .build()
            );
        }
        return Collections.emptyList();
    }
}
