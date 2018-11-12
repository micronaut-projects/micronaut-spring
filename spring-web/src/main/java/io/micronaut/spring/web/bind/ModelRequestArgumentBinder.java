package io.micronaut.spring.web.bind;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import javax.inject.Singleton;
import java.util.Optional;

/**
 * Binds the Spring {@link Model} argument for controllers.
 *
 * @author graemerocher
 * @since 1.0
 */
@Requires(classes = Model.class)
@Singleton
@Internal
public class ModelRequestArgumentBinder implements TypedRequestArgumentBinder<Model> {

    /**
     * The name of the request attribute to use.
     */
    public static final String ATTRIBUTE = "io.micronaut.spring.MODEL";

    @Override
    public BindingResult<Model> bind(ArgumentConversionContext<Model> context, HttpRequest<?> source) {
        final Optional<Model> attribute = source.getAttribute(ATTRIBUTE, Model.class);
        if (!attribute.isPresent()) {
            final ConcurrentModel concurrentModel = new ConcurrentModel();
            source.setAttribute(ATTRIBUTE, concurrentModel);
            return () -> Optional.of(concurrentModel);
        }
        return () -> attribute;
    }

    @Override
    public Argument<Model> argumentType() {
        return Argument.of(Model.class);
    }
}
