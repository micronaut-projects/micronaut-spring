/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.spring.web;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.spring.web.bind.ModelRequestArgumentBinder;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.ViewsFilter;
import io.micronaut.views.ViewsRenderer;
import org.reactivestreams.Publisher;
import org.springframework.ui.Model;

import java.util.Optional;

/**
 * Makes it possible to use Spring MVC views API.
 *
 * @author graemerocher
 * @since 1.0
 */
@Filter("/**")
@Requires(classes = {ModelAndView.class, Model.class})
@Requires(beans = ViewsRenderer.class)
public class ModelAndViewServerFilter implements HttpServerFilter {

    private final ViewsFilter viewsFilter;

    /**
     * Default constructor.
     * @param viewsFilter The views filter
     */
    public ModelAndViewServerFilter(ViewsFilter viewsFilter) {
        this.viewsFilter = viewsFilter;
    }

    @Override
    public int getOrder() {
        return viewsFilter.getOrder() + 10;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
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

                final Optional<Object> modelMap = request.getAttribute(ModelRequestArgumentBinder.ATTRIBUTE);

                if (modelMap.isPresent()) {
                    final String view = body.toString();
                    Object o = modelMap.get();
                    if (o instanceof Model) {
                        final Model model = (Model) o;
                        final MutableHttpResponse<Object> res = (MutableHttpResponse<Object>) mutableHttpResponse;
                        res.body(new ModelAndView(
                                view,
                                model
                        ));
                        return res;
                    }
                }
            }


            return mutableHttpResponse;
        });
    }
}
