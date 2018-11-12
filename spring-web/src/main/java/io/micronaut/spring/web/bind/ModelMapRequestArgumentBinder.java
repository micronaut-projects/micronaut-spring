package io.micronaut.spring.web.bind;

import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;
import org.springframework.ui.ModelMap;

import java.util.Optional;

public class ModelMapRequestArgumentBinder implements TypedRequestArgumentBinder<ModelMap> {

    /**
     * The name of the request attribute to use.
     */
    public static final String ATTRIBUTE = "io.micronaut.spring.MODEL_MAP";

    @Override
    public Argument<ModelMap> argumentType() {
        return Argument.of(ModelMap.class);
    }

    @Override
    public BindingResult<ModelMap> bind(ArgumentConversionContext<ModelMap> context, HttpRequest<?> source) {
        final Optional<ModelMap> attribute = source.getAttribute(ATTRIBUTE, ModelMap.class);
        if (!attribute.isPresent()) {
            final ModelMap modelMap = new ModelMap();
            source.setAttribute(ATTRIBUTE, modelMap);
            return () -> Optional.of(modelMap);
        }
        return () -> attribute;
    }
}
