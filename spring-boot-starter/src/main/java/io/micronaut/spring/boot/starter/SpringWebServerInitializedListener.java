/*
 * Copyright 2017-2022 original authors
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
package io.micronaut.spring.boot.starter;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.io.socket.SocketUtils;
import io.micronaut.discovery.ServiceInstance;
import io.micronaut.discovery.event.ServiceReadyEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;

/**
 * Transforms Spring Boot {@link WebServerInitializedEvent} events to Micronaut {@link ServiceReadyEvent}.
 *
 * @since 4.4.0
 */
@Internal
public class SpringWebServerInitializedListener implements ApplicationListener<WebServerInitializedEvent> {

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {

        String id   = event.getApplicationContext().getId();
        String host = event.getApplicationContext().getEnvironment().getProperty("server.address", SocketUtils.LOCALHOST);
        int port    = event.getWebServer().getPort();

        ApplicationContext context = event.getApplicationContext().getBean("micronautApplicationContext", ApplicationContext.class);

        ServiceInstance serviceInstance = ServiceInstance.of(id, host, port);

        context.getEventPublisher(ServiceReadyEvent.class)
                .publishEvent(new ServiceReadyEvent(serviceInstance));
    }
}
