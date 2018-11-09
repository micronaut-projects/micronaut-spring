package io.micronaut.spring.boot.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.http.annotation.Produces;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ReadOperationAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final String[] produces = annotation.get("produces", String[].class).orElse(null);
        final AnnotationValue<?> readOp = AnnotationValue.builder("io.micronaut.management.endpoint.annotation." + operationName()).build();
        List<AnnotationValue<?>> annotationValues = new ArrayList<>(2);

        annotationValues.add(readOp);
        if (produces != null) {
            final AnnotationValue<Produces> producesAnn = AnnotationValue.builder(Produces.class).member("value", produces).build();
            annotationValues.add(producesAnn);
        }
        return annotationValues;
    }

    @Override
    public String getName() {
        return "org.springframework.boot.actuate.endpoint.annotation." + operationName() + "Operation";
    }

    protected String operationName() {
        return "Read";
    }
}
