package io.micronaut.spring.web.annotation;

import io.micronaut.core.annotation.AnnotationClassValue;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.http.annotation.Error;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class ExceptionHandlerAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final AnnotationValueBuilder<Error> builder = AnnotationValue.builder(Error.class);
        annotation.getValue(AnnotationClassValue.class).ifPresent(annotationClassValue -> builder.member("value", annotationClassValue));
        return Collections.singletonList(
                builder
                    .build()
        );
    }

    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.ExceptionHandler";
    }
}
