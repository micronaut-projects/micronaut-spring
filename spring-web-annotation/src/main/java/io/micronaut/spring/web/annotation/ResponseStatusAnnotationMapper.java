package io.micronaut.spring.web.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Status;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class ResponseStatusAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.ResponseStatus";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        try {
            final String code = annotation.getValue(String.class).orElse(annotation.get("code", String.class).orElse(null));
            final HttpStatus status = HttpStatus.valueOf(code);

            return Collections.singletonList(
                    AnnotationValue.builder(Status.class).value(status).build()
            );
        } catch (IllegalArgumentException e) {
            // ignore
        }
        return Collections.emptyList();
    }
}
