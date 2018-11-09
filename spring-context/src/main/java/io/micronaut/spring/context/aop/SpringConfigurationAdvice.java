package io.micronaut.spring.context.aop;

import io.micronaut.aop.Around;
import io.micronaut.context.annotation.Type;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * In order to support the semantics of {@link org.springframework.context.annotation.Configuration} in Spring. This class
 * creates a subclass of each {@link org.springframework.context.annotation.Configuration} so that singletons are honoured if
 * invoked explicitly.
 *
 * @see SpringConfigurationInterceptor
 * @since 1.0
 * @author graemerocher
 */
@SuppressWarnings("WeakerAccess")
@Around
@Type(SpringConfigurationInterceptor.class)
@Documented
@Retention(RUNTIME)
public @interface SpringConfigurationAdvice {
}
