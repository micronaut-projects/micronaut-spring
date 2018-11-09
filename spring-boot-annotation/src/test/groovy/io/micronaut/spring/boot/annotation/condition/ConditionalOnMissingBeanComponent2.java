package io.micronaut.spring.boot.annotation.condition;

import io.micronaut.spring.boot.annotation.MyConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(MyConfigurationProperties.class)
public class ConditionalOnMissingBeanComponent2 {
}
