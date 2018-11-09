package io.micronaut.spring.boot.annotation;

public class DeleteOperationAnnotationMapper extends ReadOperationAnnotationMapper {
    @Override
    protected String operationName() {
        return "Delete";
    }
}
