package io.micronaut.spring.web.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.core.bind.annotation.Bindable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.annotation.Body;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class RequestBodyAnnotationMapping extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.RequestBody";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        List<AnnotationValue<?>> mappedAnnotations = new ArrayList<>();
        final boolean required = annotation.get("required", boolean.class).orElse(true);
        final AnnotationValueBuilder<?> builder = AnnotationValue.builder(Body.class);
        final AnnotationValueBuilder<Bindable> bindableBuilder = AnnotationValue.builder(Bindable.class);
        mappedAnnotations.add(builder.build());
        mappedAnnotations.add(bindableBuilder.build());
        if (!required) {
            mappedAnnotations.add(AnnotationValue.builder(Nullable.class).build());
        }
        return mappedAnnotations;
    }
}
