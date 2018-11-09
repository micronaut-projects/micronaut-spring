package io.micronaut.spring.web.annotation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.core.util.ArrayUtils;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.annotation.*;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestMappingAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.web.bind.annotation.RequestMapping";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        List<AnnotationValue<?>> annotations = new ArrayList<>();

        final String path = computePath(annotation);
        final Optional<HttpMethod> method = annotation.get("method", HttpMethod.class);

        annotations.add(newBuilder(method.orElse(null), annotation).value(path).build());

        final String[] consumes = annotation.get("consumes", String[].class).orElse(null);
        final String[] produces = annotation.get("produces", String[].class).orElse(null);

        if (ArrayUtils.isNotEmpty(consumes)) {
            annotations.add(AnnotationValue.builder(Consumes.class).member("value", consumes).build());
        }
        if (ArrayUtils.isNotEmpty(produces)) {
            annotations.add(AnnotationValue.builder(Produces.class).member("value", produces).build());
        }

        if (isHttpMethodMapping(method.orElse(null))) {
            annotations.add(AnnotationValue.builder(HttpMethodMapping.class).value(path).build());
        }
        return annotations;
    }

    protected boolean isHttpMethodMapping(@Nullable HttpMethod method) {
        return method != null;
    }

    protected AnnotationValueBuilder<?> newBuilder(@Nullable HttpMethod httpMethod, AnnotationValue<Annotation> annotation) {

        if (httpMethod != null) {
            switch (httpMethod) {
                case TRACE:
                    return AnnotationValue.builder(Trace.class);
                case DELETE:
                    return AnnotationValue.builder(Delete.class);
                case GET:
                    return AnnotationValue.builder(Get.class);
                case HEAD:
                    return AnnotationValue.builder(Head.class);
                case POST:
                    return AnnotationValue.builder(Post.class);
                case PUT:
                    return AnnotationValue.builder(Put.class);
                case PATCH:
                    return AnnotationValue.builder(Patch.class);
                case OPTIONS:
                    return AnnotationValue.builder(Options.class);
                default:
                    return AnnotationValue.builder("io.micronaut.http.annotation.UriMapping");
            }
        } else {
            return AnnotationValue.builder("io.micronaut.http.annotation.UriMapping");
        }
    }

    private String computePath(AnnotationValue<Annotation> annotation) {
        return annotation.getValue(String.class).orElseGet(() -> annotation.get("path", String.class).orElse("/"));
    }
}
