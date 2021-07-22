/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.spring.context.event;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.context.event.ShutdownEvent;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.spring.context.MicronautApplicationContext;
import jakarta.inject.Singleton;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;

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

    /**
     * Default constructor.
     * @param applicationContext The app context
     * @param eventPublisher The event publisher
     */
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
