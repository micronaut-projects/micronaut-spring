package io.micronaut.spring.annotation.context;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps {@code @Autowired} to {@link Inject}
 *
 * @author graemerocher
 * @since 1.0
 */
public class AutowiredAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.beans.factory.annotation.Autowired";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final boolean required = annotation.getValue(Boolean.class).orElse(true);

        List<AnnotationValue<?>> annotations = new ArrayList<>(2);
        annotations.add(AnnotationValue.builder(Inject.class).build());
        if (!required) {
            annotations.add(AnnotationValue.builder(Nullable.class).build());
        }
        return annotations;
    }
}
