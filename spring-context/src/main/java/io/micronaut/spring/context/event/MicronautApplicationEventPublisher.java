package io.micronaut.spring.context.event;

import io.micronaut.context.annotation.Primary;
import io.micronaut.core.annotation.Internal;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import javax.inject.Singleton;

/**
 * Implementation of the {@link ApplicationEventPublisher} interface for Micronaut.
 *
 * @author graemerocher
 */
@Singleton
@Primary
@Internal
public class MicronautApplicationEventPublisher implements ApplicationEventPublisher {

    private final io.micronaut.context.event.ApplicationEventPublisher eventPublisher;

    public MicronautApplicationEventPublisher(io.micronaut.context.event.ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publishEvent(Object event) {
        eventPublisher.publishEvent(event);
    }
}
