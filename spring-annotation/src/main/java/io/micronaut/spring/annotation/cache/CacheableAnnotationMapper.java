package io.micronaut.spring.annotation.cache;

import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class CacheableAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final String[] cacheNames = annotation.getValue(String[].class).orElseGet(() -> annotation.get("cacheNames", String[].class).orElse(null));

        if (cacheNames != null) {
            final AnnotationValueBuilder<?> builder = buildAnnotation()
                    .member("value", cacheNames)
                    .member("cacheNames", cacheNames);


            return Collections.singletonList(builder.build());
        }

        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "org.springframework.cache.annotation.Cacheable";
    }

    protected AnnotationValueBuilder<? extends Annotation> buildAnnotation() {
        return AnnotationValue.builder(Cacheable.class);
    }
}
