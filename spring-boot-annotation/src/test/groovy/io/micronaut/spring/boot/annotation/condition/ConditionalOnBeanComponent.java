package io.micronaut.spring.boot.annotation.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@ConditionalOnBean(DataSource.class)
public class ConditionalOnBeanComponent {
}
