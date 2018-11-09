package io.micronaut.spring.boot.annotation;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.ConfigurationReader;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.naming.NameUtils;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigurationPropertiesAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.boot.context.properties.ConfigurationProperties";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        String prefix = annotation.get("prefix", String.class).orElseGet(() -> annotation.getValue(String.class).orElse(null));
        if (prefix != null) {
            prefix = NameUtils.hyphenate(prefix, true);
            return Arrays.asList(
                    AnnotationValue.builder(ConfigurationReader.class)
                            .member("prefix", prefix)
                            .build(),
                    AnnotationValue.builder(ConfigurationProperties.class)
                            .value(prefix)
                            .build()
            );
        } else {
            return Collections.emptyList();
        }
    }
}
