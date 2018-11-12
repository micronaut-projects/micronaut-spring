package io.micronaut.spring.web.bind;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.AnnotatedRequestArgumentBinder;
import org.springframework.web.bind.annotation.RequestAttribute;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
@Requires(classes = RequestAttribute.class)
public class RequestAttributeArgumentBinder implements AnnotatedRequestArgumentBinder<RequestAttribute, Object> {

    @Override
    public Class<RequestAttribute> getAnnotationType() {
        return RequestAttribute.class;
    }

    @Override
    public BindingResult<Object> bind(ArgumentConversionContext<Object> context, HttpRequest<?> source) {
        final AnnotationMetadata annotationMetadata = context.getAnnotationMetadata();
        final boolean required = annotationMetadata.getValue("required", boolean.class).orElse(true);
        final String name = annotationMetadata.getValue(RequestAttribute.class, String.class).orElseGet(() ->
                annotationMetadata.getValue(RequestAttribute.class, "name", String.class).orElse(context.getArgument().getName())
        );

        return new BindingResult<Object>() {
            @Override
            public Optional<Object> getValue() {
                return source.getAttributes().get(name, context);
            }

            @Override
            public boolean isSatisfied() {
                return !required;
            }
        };
    }
}
