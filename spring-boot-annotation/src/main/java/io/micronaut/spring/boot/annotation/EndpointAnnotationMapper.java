package io.micronaut.spring.boot.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EndpointAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final Optional<String> id = annotation.get("id", String.class);
        if (id.isPresent()) {
            final AnnotationValueBuilder<?> builder = AnnotationValue.builder("io.micronaut.management.endpoint.annotation.Endpoint");
            final Boolean enableByDefault = annotation.get("enableByDefault", boolean.class).orElse(true);
            builder.value(id.get());
            builder.member("id",id.get());
            builder.member("defaultEnabled", enableByDefault);
            return Collections.singletonList(builder.build());
        }

        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "org.springframework.boot.actuate.endpoint.annotation.Endpoint";
    }
}
