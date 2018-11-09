package io.micronaut.spring.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.version.VersionUtils;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public abstract class AbstractSpringAnnotationMapper implements NamedAnnotationMapper {
    @Override
    public final List<AnnotationValue<?>> map(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        if (annotation == null || visitorContext == null) {
            return Collections.emptyList();
        }

        if (VersionUtils.isAtLeastMicronautVersion("1.0.1")) {
            return mapInternal(annotation, visitorContext);
        } else {
            visitorContext.info("Annotation mapper [" + getClass().getName() + "] requires Micronaut 1.0.1 or above. Please upgrade to continue.", null);
            return Collections.emptyList();
        }
    }

    protected abstract List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext);
}
