package io.micronaut.spring.context.factory;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.util.ArgumentUtils;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Configuration for the Micronaut bean factory.
 *
 * @author graemerocher
 * @since 1.0
 */
@ConfigurationProperties(MicronautBeanFactoryConfiguration.PREFIX)
public class MicronautBeanFactoryConfiguration {
    public static final String PREFIX = "micronaut.spring.context";

    private List<Class<?>> beanExcludes = Collections.emptyList();

    /**
     * The bean types to exclude from being exposed by Spring's {@link org.springframework.beans.factory.BeanFactory} interface
     * @return The bean types
     */
    public @Nonnull List<Class<?>> getBeanExcludes() {
        return beanExcludes;
    }

    /**
     * The bean types to exclude from being exposed by Spring's {@link org.springframework.beans.factory.BeanFactory} interface
     * @param beanExcludes The bean types
     */
    public void setBeanExcludes(@Nonnull List<Class<?>> beanExcludes) {
        ArgumentUtils.requireNonNull("beanExcludes", beanExcludes);
        this.beanExcludes = beanExcludes;
    }
}
