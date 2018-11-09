package io.micronaut.spring.boot.condition;

import io.micronaut.context.BeanContext;
import io.micronaut.context.condition.Condition;
import io.micronaut.context.condition.ConditionContext;

public class RequiresSingleCandidateCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context) {
        final Class<?> type = context.getComponent().findAnnotation(RequiresSingleCandidate.class).flatMap(ann -> ann.getValue(Class.class)).orElse(null);
        if (type != null) {
            final BeanContext beanContext = context.getBeanContext();
            return beanContext.findBeanDefinition(type).isPresent();
        }
        return true;
    }
}
