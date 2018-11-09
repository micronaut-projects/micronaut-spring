package io.micronaut.spring.boot.annotation.condition;

import io.micronaut.spring.boot.annotation.MyConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnClass(MyConfigurationProperties.class)
public class ConditionalOnClassComponent {
}
