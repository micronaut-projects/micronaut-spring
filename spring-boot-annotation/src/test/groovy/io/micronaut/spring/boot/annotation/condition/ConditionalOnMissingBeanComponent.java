package io.micronaut.spring.boot.annotation.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@ConditionalOnMissingBean(DataSource.class)
public class ConditionalOnMissingBeanComponent {
}
