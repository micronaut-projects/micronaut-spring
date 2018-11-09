package io.micronaut.spring.context.aop;

import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.Internal;
import io.micronaut.inject.ExecutableMethod;
import io.micronaut.spring.context.factory.MicronautBeanFactory;

import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link SpringConfigurationAdvice}.
 *
 * @author graemerocher
 * @since 1.0
 */
@Singleton
@Internal
public class SpringConfigurationInterceptor implements MethodInterceptor<Object, Object> {

    private final Map<ExecutableMethod, Object> computedSingletons = new ConcurrentHashMap<>();

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        final AnnotationMetadata annotationMetadata = context.getAnnotationMetadata();
        final boolean isSingleton = MicronautBeanFactory.isSingleton(annotationMetadata);
        if (isSingleton) {
            return computedSingletons.computeIfAbsent(context.getExecutableMethod(), executableMethod -> context.proceed());

        }
        return context.proceed();
    }

}
