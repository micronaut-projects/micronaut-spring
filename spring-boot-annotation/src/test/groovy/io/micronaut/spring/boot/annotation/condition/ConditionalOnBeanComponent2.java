package io.micronaut.spring.boot.annotation.condition;

import io.micronaut.context.env.Environment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(Environment.class)
public class ConditionalOnBeanComponent2 {
}
