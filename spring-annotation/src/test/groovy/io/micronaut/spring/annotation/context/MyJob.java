package io.micronaut.spring.annotation.context;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MyJob {

    private boolean executed;

    @Scheduled(fixedDelay = 100)
    void executeMe() {
        this.executed = true;
    }

    public boolean isExecuted() {
        return executed;
    }
}
