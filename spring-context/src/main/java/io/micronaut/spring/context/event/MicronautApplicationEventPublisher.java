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

import io.micronaut.context.annotation.Primary;
import io.micronaut.core.annotation.Internal;
import io.micronaut.spring.beans.MicronautContextInternal;
import jakarta.inject.Singleton;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Implementation of the {@link ApplicationEventPublisher} interface for Micronaut.
 *
 * @author graemerocher
 */
@Singleton
@Primary
@Internal
public class MicronautApplicationEventPublisher implements ApplicationEventPublisher, MicronautContextInternal {

    private final io.micronaut.context.event.ApplicationEventPublisher eventPublisher;

    /**
     * Default constructor.
     * @param eventPublisher The event publisher to adapt
     */
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
