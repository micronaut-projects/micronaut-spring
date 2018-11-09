package io.micronaut.spring.boot.annotation;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.AnnotationClassValue;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConditionalOnSingleCandidateAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final AnnotationClassValue annotationClassValue = annotation.getValue(AnnotationClassValue.class).orElseGet(() ->
                annotation.get("type", String.class).map(AnnotationClassValue::new).orElse(null)
        );
        if (annotationClassValue != null) {

            return Arrays.asList(
                    AnnotationValue.builder(Requires.class)
                            .member("condition", new AnnotationClassValue<>(
                                    "io.micronaut.spring.boot.condition.RequiresSingleCandidateCondition"
                            ))
                            .build(),
                    AnnotationValue.builder("io.micronaut.spring.boot.condition.RequiresSingleCandidate")
                            .member("value", annotationClassValue)
                            .build()
            );
        }
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate";
    }
}
