package io.micronaut.spring.web.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.http.annotation.Controller;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.context.ComponentAnnotationMapper;
import io.micronaut.validation.Validated;

import java.lang.annotation.Annotation;
import java.util.List;

public class RestControllerAnnotationMapper extends ComponentAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.RestController";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final List<AnnotationValue<?>> annotationValues = super.mapInternal(annotation, visitorContext);
        annotationValues.add(AnnotationValue.builder(Controller.class).build());
        annotationValues.add(AnnotationValue.builder(Validated.class).build());
        return annotationValues;
    }
}
