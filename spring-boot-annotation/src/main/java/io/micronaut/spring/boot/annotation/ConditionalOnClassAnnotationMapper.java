package io.micronaut.spring.boot.annotation;

public class ConditionalOnClassAnnotationMapper extends ConditionalOnBeanAnnotationMapper {

    @Override
    public String getName() {
        return "org.springframework.boot.autoconfigure.condition.ConditionalOnClass";
    }

    @Override
    protected String typesMemberName() {
        return "names";
    }

    @Override
    protected String requiresMethodName() {
        return "classes";
    }
}
