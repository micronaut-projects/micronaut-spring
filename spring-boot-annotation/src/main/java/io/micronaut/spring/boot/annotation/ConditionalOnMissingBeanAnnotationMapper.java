package io.micronaut.spring.boot.annotation;

public class ConditionalOnMissingBeanAnnotationMapper extends ConditionalOnBeanAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean";
    }

    @Override
    protected String requiresMethodName() {
        return "missingBeans";
    }
}
