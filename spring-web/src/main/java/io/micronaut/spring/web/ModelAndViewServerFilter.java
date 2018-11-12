package io.micronaut.spring.web;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.OncePerRequestHttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.spring.web.bind.ModelRequestArgumentBinder;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.ViewsFilter;
import io.micronaut.views.ViewsRenderer;
import org.reactivestreams.Publisher;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import java.util.Optional;

@Filter("/**")
@Requires(classes = {ModelAndView.class, Model.class})
@Requires(beans = ViewsRenderer.class)
public class ModelAndViewServerFilter extends OncePerRequestHttpServerFilter {

    private final ViewsFilter viewsFilter;

    public ModelAndViewServerFilter(ViewsFilter viewsFilter) {
        this.viewsFilter = viewsFilter;
    }

    @Override
    public int getOrder() {
        return viewsFilter.getOrder() + 10;
    }

    @Override
    protected Publisher<MutableHttpResponse<?>> doFilterOnce(HttpRequest<?> request, ServerFilterChain chain) {
        final Publisher<MutableHttpResponse<?>> responsePublisher = chain.proceed(request);
        return Publishers.map(responsePublisher, mutableHttpResponse -> {
            final Optional<Model> attribute = request.getAttribute(ModelRequestArgumentBinder.ATTRIBUTE, Model.class);
            final Object body = mutableHttpResponse.body();
            final boolean isCharSeq = body instanceof CharSequence;
            if (isCharSeq) {

                if (attribute.isPresent()) {
                    final String view = body.toString();
                    final Model model = attribute.get();
                    final MutableHttpResponse<Object> res = (MutableHttpResponse<Object>) mutableHttpResponse;
                    res.body(new ModelAndView(
                            view,
                            model.asMap()
                    ));
                    return res;
                }

                final Optional<ModelMap> modelMap = request.getAttribute(ModelRequestArgumentBinder.ATTRIBUTE, ModelMap.class);

                if (modelMap.isPresent()) {
                    final String view = body.toString();
                    final ModelMap model = modelMap.get();
                    final MutableHttpResponse<Object> res = (MutableHttpResponse<Object>) mutableHttpResponse;
                    res.body(new ModelAndView(
                            view,
                            model
                    ));
                    return res;
                }
            }


            return mutableHttpResponse;
        });
    }
}
