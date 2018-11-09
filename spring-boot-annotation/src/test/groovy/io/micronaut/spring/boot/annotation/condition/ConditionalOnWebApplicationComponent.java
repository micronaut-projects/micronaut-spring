package io.micronaut.spring.boot.annotation.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnWebApplication
public class ConditionalOnWebApplicationComponent {
}
