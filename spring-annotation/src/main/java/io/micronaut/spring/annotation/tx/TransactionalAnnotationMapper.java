package io.micronaut.spring.annotation.tx;

import io.micronaut.core.annotation.AnnotationClassValue;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.scheduling.annotation.Async;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Maps {@code @Transactional} to {@code io.micronaut.spring.tx.annotation.Transactional}.
 *
 * @author graemerocher
 * @since 1.0
 */
public class TransactionalAnnotationMapper extends AbstractSpringAnnotationMapper {

    @Override
    public String getName() {
        return "org.springframework.transaction.annotation.Transactional";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final AnnotationValueBuilder<?> builder = AnnotationValue.builder("io.micronaut.spring.tx.annotation.Transactional");
        annotation.getValue(String.class).ifPresent(s -> {
            builder.value(s);
            builder.member("transactionManager", s);
        });

        Stream.of("propagation", "isolation", "transactionManager")
                .forEach(member -> annotation.get(member, String.class).ifPresent(s -> builder.member(member, s)));
        Stream.of("rollbackForClassName", "noRollbackForClassName")
                .forEach(member -> annotation.get(member, String[].class).ifPresent(s -> builder.member(member, s)));
        Stream.of("rollbackFor", "noRollbackFor")
                .forEach(member -> annotation.get(member, AnnotationClassValue[].class).ifPresent(classValues -> {
                    String[] names = new String[classValues.length];
                    for (int i = 0; i < classValues.length; i++) {
                        AnnotationClassValue classValue = classValues[i];
                        names[i] = classValue.getName();
                    }
                    builder.member(member, names);
                }));
        annotation.get("timeout", Integer.class).ifPresent(integer -> builder.member("timeout", integer));
        annotation.get("readOnly", Boolean.class).ifPresent(bool -> builder.member("readOnly", bool));

        return Collections.singletonList(builder.build());
    }
}
