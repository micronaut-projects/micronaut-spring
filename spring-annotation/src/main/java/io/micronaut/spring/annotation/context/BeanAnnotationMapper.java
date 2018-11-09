package io.micronaut.spring.annotation.context;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.DefaultScope;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Maps {@code @Bean} to {@link Bean}
 *
 * @author graemerocher
 * @since 1.0
 */
public class BeanAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.context.annotation.Bean";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        List<AnnotationValue<?>> newAnnotations = new ArrayList<>(3);
        final AnnotationValueBuilder<Bean> beanAnn = AnnotationValue.builder(Bean.class);

        final Optional<String> destroyMethod = annotation.get("destroyMethod", String.class);
        destroyMethod.ifPresent(s -> beanAnn.member("preDestroy", s));
        newAnnotations.add(beanAnn.build());
        newAnnotations.add(AnnotationValue.builder(DefaultScope.class)
                .value(Singleton.class)
                .build());
        final String beanName = annotation.getValue(String.class).orElse(annotation.get("name", String.class).orElse(null));

        if (StringUtils.isNotEmpty(beanName)) {
            newAnnotations.add(AnnotationValue.builder(Named.class).value(beanName).build());
        }

        return newAnnotations;
    }
}
