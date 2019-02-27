package io.micronaut.spring.boot.annotation.condition;

import io.micronaut.core.convert.TypeConverter;
import io.micronaut.http.bind.binders.RequestArgumentBinder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnSingleCandidate(RequestArgumentBinder.class)
public class ConditionalOnSingleCandidateComponent2 {
}
