package io.micronaut.spring.annotation.context;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.DefaultScope;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Maps {@code @Component} to {@link Bean} with a {@link DefaultScope} of {@link Singleton}
 *
 * @author graemerocher
 * @since 1.0
 */
public class ComponentAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.stereotype.Component";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final Optional<String> beanName = annotation.getValue(String.class);
        List<AnnotationValue<?>> mappedAnnotations = new ArrayList<>(2);
        mappedAnnotations.add(AnnotationValue.builder(Bean.class).build());
        mappedAnnotations.add(AnnotationValue.builder(DefaultScope.class)
                .value(Singleton.class)
                .build());
        beanName.ifPresent(s -> mappedAnnotations.add(AnnotationValue.builder(Named.class).value(s).build()));
        return mappedAnnotations;
    }
}
