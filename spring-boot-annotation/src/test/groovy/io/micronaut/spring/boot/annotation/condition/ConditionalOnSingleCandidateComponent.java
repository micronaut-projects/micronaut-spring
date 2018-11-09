package io.micronaut.spring.boot.annotation.condition;

import io.micronaut.spring.boot.annotation.MyConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnSingleCandidate(MyConfigurationProperties.class)
public class ConditionalOnSingleCandidateComponent {
}
