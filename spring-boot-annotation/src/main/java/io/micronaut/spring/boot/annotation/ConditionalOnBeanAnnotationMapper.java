package io.micronaut.spring.boot.annotation;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.AnnotationClassValue;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ConditionalOnBeanAnnotationMapper extends AbstractSpringAnnotationMapper {

    @Override
    public String getName() {
        return "org.springframework.boot.autoconfigure.condition.ConditionalOnBean";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final Optional<AnnotationClassValue[]> classValues = annotation.getValue(AnnotationClassValue[].class);
        if (classValues.isPresent()) {
            final AnnotationClassValue<?>[] types = classValues.get();
            return Collections.singletonList(
                    AnnotationValue.builder(Requires.class)
                            .member(requiresMethodName(), types).build()
            );
        } else {
            final Optional<String[]> types = annotation.get(typesMemberName(), String[].class);
            if (types.isPresent()) {
                final AnnotationClassValue[] classesValues = Arrays.stream(types.get()).map(AnnotationClassValue::new).toArray(AnnotationClassValue[]::new);
                return Collections.singletonList(
                        AnnotationValue.builder(Requires.class)
                                .member(requiresMethodName(), classesValues).build()
                );
            }
        }

        return Collections.emptyList();
    }

    protected String typesMemberName() {
        return "types";
    }

    protected String requiresMethodName() {
        return "beans";
    }
}
