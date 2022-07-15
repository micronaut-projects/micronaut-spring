package io.micronaut.spring.core.annotation;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.AnnotationValue;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotationSelector;
import org.springframework.core.annotation.MergedAnnotations;

public class MicronautMergedAnnotations implements MergedAnnotations {
    private final AnnotationMetadata annotationMetadata;

    public MicronautMergedAnnotations(AnnotationMetadata annotationMetadata) {
        this.annotationMetadata = annotationMetadata;
    }

    @Override
    public <A extends Annotation> boolean isPresent(Class<A> annotationType) {
        return annotationMetadata.hasAnnotation(annotationType);
    }

    @Override
    public boolean isPresent(String annotationType) {
        return annotationMetadata.hasAnnotation(annotationType);
    }

    @Override
    public <A extends Annotation> boolean isDirectlyPresent(Class<A> annotationType) {
        return annotationMetadata.hasDeclaredAnnotation(annotationType);
    }

    @Override
    public boolean isDirectlyPresent(String annotationType) {
        return annotationMetadata.hasDeclaredAnnotation(annotationType);
    }

    @Override
    public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType) {
        AnnotationValue<A> av = annotationMetadata.getAnnotation(annotationType);
        if (av != null) {
            return new MergedAnnotationValue<>(annotationMetadata, av);
        }
        return MergedAnnotation.missing();
    }

    @Override
    public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType, Predicate<? super MergedAnnotation<A>> predicate) {
        AnnotationValue<A> av = annotationMetadata.getAnnotation(annotationType);
        return resolveAnnotationValueToMergedAnnotation(predicate, av);
    }

    @Override
    public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType, Predicate<? super MergedAnnotation<A>> predicate, MergedAnnotationSelector<A> selector) {
        AnnotationValue<A> av = annotationMetadata.getAnnotation(annotationType);
        return resolveAnnotationValueToMergedAnnotation(predicate, av);
    }

    @Override
    public <A extends Annotation> MergedAnnotation<A> get(String annotationType) {
        AnnotationValue<A> av = annotationMetadata.getAnnotation(annotationType);
        if (av != null) {
            return new MergedAnnotationValue<>(annotationMetadata, av);
        }
        return MergedAnnotation.missing();
    }

    @Override
    public <A extends Annotation> MergedAnnotation<A> get(String annotationType, Predicate<? super MergedAnnotation<A>> predicate) {
        AnnotationValue<A> av = annotationMetadata.getAnnotation(annotationType);
        return resolveAnnotationValueToMergedAnnotation(predicate, av);
    }

    private <A extends Annotation> MergedAnnotation<A> resolveAnnotationValueToMergedAnnotation(Predicate<? super MergedAnnotation<A>> predicate, AnnotationValue<A> av) {
        if (av != null) {
            MergedAnnotationValue<A> ma = new MergedAnnotationValue<>(annotationMetadata, av);
            if (predicate != null && !predicate.test(ma)) {
                return MergedAnnotation.missing();
            }
            return ma;
        }
        return MergedAnnotation.missing();
    }

    @Override
    public <A extends Annotation> MergedAnnotation<A> get(String annotationType, Predicate<? super MergedAnnotation<A>> predicate, MergedAnnotationSelector<A> selector) {
        AnnotationValue<A> av = annotationMetadata.getAnnotation(annotationType);
        return resolveAnnotationValueToMergedAnnotation(predicate, av);
    }

    @Override
    public <A extends Annotation> Stream<MergedAnnotation<A>> stream(Class<A> annotationType) {
        MergedAnnotation<A> ma = get(annotationType);
        if (ma != null) {
            return Stream.of(ma);
        }
        return Stream.empty();
    }

    @Override
    public <A extends Annotation> Stream<MergedAnnotation<A>> stream(String annotationType) {
        MergedAnnotation<A> ma = get(annotationType);
        if (ma != null) {
            return Stream.of(ma);
        }
        return Stream.empty();
    }

    @Override
    public Stream<MergedAnnotation<Annotation>> stream() {
        return annotationMetadata.getAnnotationNames()
            .stream().map(annotationMetadata::getAnnotation)
            .filter(Objects::nonNull)
            .map(av -> new MergedAnnotationValue<>(annotationMetadata, av));
    }

    @Override
    public Iterator<MergedAnnotation<Annotation>> iterator() {
        return stream().iterator();
    }
}
