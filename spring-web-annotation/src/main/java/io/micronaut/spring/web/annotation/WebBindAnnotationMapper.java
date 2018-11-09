package io.micronaut.spring.web.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.core.bind.annotation.Bindable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public abstract class WebBindAnnotationMapper<T extends Annotation> extends AbstractSpringAnnotationMapper {

    abstract Class<T> annotationType();

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        List<AnnotationValue<?>> mappedAnnotations = new ArrayList<>();

        final String name = annotation.getValue(String.class).orElseGet(() -> annotation.get("name", String.class).orElse(null));
        final String defaultValue = annotation.get("defaultValue", String.class).orElse(null);
        final boolean required = annotation.get("required", boolean.class).orElse(true);

        final AnnotationValueBuilder<?> builder = AnnotationValue.builder(annotationType());
        final AnnotationValueBuilder<Bindable> bindableBuilder = AnnotationValue.builder(Bindable.class);
        if (StringUtils.isNotEmpty(name)) {
            builder.value(name);
            bindableBuilder.value(name);
        }
        if (StringUtils.isNotEmpty(defaultValue)) {
            builder.member("defaultValue", name);
            bindableBuilder.member("defaultValue", defaultValue);
        }

        mappedAnnotations.add(builder.build());
        mappedAnnotations.add(bindableBuilder.build());
        if (!required) {
            mappedAnnotations.add(AnnotationValue.builder(Nullable.class).build());
        }

        return mappedAnnotations;
    }

}
