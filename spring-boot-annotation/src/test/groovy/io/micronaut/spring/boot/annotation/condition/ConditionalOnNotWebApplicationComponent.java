package io.micronaut.spring.boot.annotation.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnNotWebApplication
public class ConditionalOnNotWebApplicationComponent {
}
