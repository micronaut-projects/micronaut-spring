package io.micronaut.spring.annotation.context;

import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service("myname")
public class MyNamedService {


    private ContextStartedEvent lastEvent;

    @EventListener
    public void onStartup(ContextStartedEvent startedEvent) {
        this.lastEvent = startedEvent;
    }

    public ContextStartedEvent getLastEvent() {
        return lastEvent;
    }
}
