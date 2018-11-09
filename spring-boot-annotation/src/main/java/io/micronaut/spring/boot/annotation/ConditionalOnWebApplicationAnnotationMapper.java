package io.micronaut.spring.boot.annotation;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.AnnotationClassValue;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class ConditionalOnWebApplicationAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        return Collections.singletonList(
                AnnotationValue.builder(Requires.class)
                               .member("beans", new AnnotationClassValue<>(EmbeddedServer.class))
                               .build()
        );
    }

    @Override
    public String getName() {
        return "org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication";
    }
}
