package io.micronaut.spring.annotation.context;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Maps {@code @Qualifier} to {@link Named}
 *
 * @author graemerocher
 * @since 1.0
 */
public class QualifierAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.beans.factory.annotation.Qualifier";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final Optional<String> value = annotation.getValue(String.class);
        if (value.isPresent()) {
            return Collections.singletonList(
                    AnnotationValue.builder(Named.class)
                            .value(value.get())
                            .build()
            );
        }
        return Collections.emptyList();
    }
}
