package io.micronaut.spring.context.event;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.context.event.ShutdownEvent;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.spring.context.MicronautApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;

import javax.inject.Singleton;

/**
 * Adapts Micronaut events to Spring events.
 *
 * @author graemerocher
 * @since 1.0
 */
@Singleton
class ContextEventAdapter implements ApplicationEventListener<Object> {

    private final ApplicationEventPublisher eventPublisher;
    private final MicronautApplicationContext applicationContext;

    ContextEventAdapter(MicronautApplicationContext applicationContext, ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(Object event) {
        if (event instanceof StartupEvent) {
            eventPublisher.publishEvent(new ContextStartedEvent(
                applicationContext
            ));
        } else if (event instanceof ShutdownEvent) {
            eventPublisher.publishEvent(new ContextClosedEvent(
                    applicationContext
            ));
        }
    }
}
