package io.micronaut.spring.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.Lifecycle;

import java.io.Closeable;

/**
 * An interface that is more limited in scope than {@link org.springframework.context.ConfigurableApplicationContext} and
 * provides lifecycle management.
 *
 * @author graemerocher
 * @since 1.0
 */
public interface ManagedApplicationContext extends ApplicationContext, Lifecycle, Closeable {
}
