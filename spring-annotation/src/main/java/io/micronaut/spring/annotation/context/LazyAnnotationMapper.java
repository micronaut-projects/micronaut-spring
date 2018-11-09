package io.micronaut.spring.annotation.context;

import io.micronaut.context.annotation.Context;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * Makes {@code @Lazy(false)} become a {@link Context} scoped bean.
 *
 * @author graemerocher
 * @since 1.0
 */
public class LazyAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.context.annotation.Lazy";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final boolean lazy = annotation.getValue(Boolean.class).orElse(true);
        if (!lazy) {
            return Collections.singletonList(
                    AnnotationValue.builder(Context.class).build()
            );
        }
        return Collections.emptyList();
    }
}
