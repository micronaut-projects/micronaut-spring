package io.micronaut.spring.boot.annotation.condition;

import io.micronaut.core.convert.TypeConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnSingleCandidate(TypeConverter.class)
public class ConditionalOnSingleCandidateComponent2 {
}
