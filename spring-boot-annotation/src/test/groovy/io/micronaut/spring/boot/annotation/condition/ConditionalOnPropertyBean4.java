package io.micronaut.spring.boot.annotation.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "some", name = "prop3", matchIfMissing = true)
public class ConditionalOnPropertyBean4 {
}
