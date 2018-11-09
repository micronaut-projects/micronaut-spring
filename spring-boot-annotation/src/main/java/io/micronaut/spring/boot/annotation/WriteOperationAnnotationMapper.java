package io.micronaut.spring.boot.annotation;

public class WriteOperationAnnotationMapper extends ReadOperationAnnotationMapper {
    @Override
    protected String operationName() {
        return "Write";
    }
}
