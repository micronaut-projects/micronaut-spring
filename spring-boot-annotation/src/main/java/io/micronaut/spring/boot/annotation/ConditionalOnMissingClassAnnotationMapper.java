package io.micronaut.spring.boot.annotation;

public class ConditionalOnMissingClassAnnotationMapper extends ConditionalOnClassAnnotationMapper {

    @Override
    public String getName() {
        return "org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass";
    }

    @Override
    protected String requiresMethodName() {
        return "missing";
    }
}
