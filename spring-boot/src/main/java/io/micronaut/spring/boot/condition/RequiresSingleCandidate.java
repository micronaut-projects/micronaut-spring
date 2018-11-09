package io.micronaut.spring.boot.condition;

import io.micronaut.context.annotation.Requires;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Requires
public @interface RequiresSingleCandidate {

    Class<?> value();
}
