package io.micronaut.spring.core.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.micronaut.core.annotation.AnnotationClassValue;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.reflect.ClassUtils;
import io.micronaut.core.util.CollectionUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.MergedAnnotation;

/**
 * Implementation of {@link MergedAnnotation} that backs onto a Micronaut {@link AnnotationValue}.
 * @param <A> The annotation type
 */
public class MergedAnnotationValue<A extends Annotation> implements MergedAnnotation<A> {
    private final AnnotationMetadata annotationMetadata;
    private final AnnotationValue<A> value;

    public MergedAnnotationValue(AnnotationMetadata annotationMetadata, AnnotationValue<A> value) {
        this.annotationMetadata = annotationMetadata;
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<A> getType() {
        return (Class<A>) annotationMetadata.getAnnotationType(value.getAnnotationName())
                    .orElseGet(() -> ClassUtils.forName(value.getAnnotationName(), getClass().getClassLoader()).orElseThrow(() -> new IllegalStateException("Annotation class not present")));
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public boolean isDirectlyPresent() {
        return annotationMetadata.hasDeclaredAnnotation(value.getAnnotationName());
    }

    @Override
    public boolean isMetaPresent() {
        return annotationMetadata.hasStereotype(value.getAnnotationName()) && !annotationMetadata.hasAnnotation(value.getAnnotationName());
    }

    @Override
    public int getDistance() {
        return isMetaPresent() ? 1 : 0;
    }

    @Override
    public int getAggregateIndex() {
        return 0;
    }

    @Override
    public Object getSource() {
        return null;
    }

    @Override
    public MergedAnnotation<?> getMetaSource() {
        if (isMetaPresent()) {
            String metaAnnotationName = annotationMetadata.getAnnotationNameByStereotype(value.getAnnotationName()).orElse(null);
            if (metaAnnotationName != null) {
                AnnotationValue<Annotation> av = annotationMetadata.getAnnotation(metaAnnotationName);
                if (av != null) {
                    return new MergedAnnotationValue<>(annotationMetadata, av);
                }
            }
        }
        return null;
    }

    @Override
    public MergedAnnotation<?> getRoot() {
        if (isMetaPresent()) {
            String metaAnnotationName = annotationMetadata.getAnnotationNameByStereotype(value.getAnnotationName()).orElse(null);
            while (metaAnnotationName != null) {
                String next = annotationMetadata.getAnnotationNameByStereotype(metaAnnotationName).orElse(null);
                if (next == null) {
                    break;
                } else {
                    metaAnnotationName = next;
                }
            }
            if (metaAnnotationName != null) {
                AnnotationValue<Annotation> av = annotationMetadata.getAnnotation(metaAnnotationName);
                if (av != null) {
                    return new MergedAnnotationValue<>(annotationMetadata, av);
                }
            }
        }
        return this;
    }

    @Override
    public List<Class<? extends Annotation>> getMetaTypes() {
        if (isMetaPresent()) {
            List<Class<? extends Annotation>> metaTypes = new ArrayList<>();
            List<Class<? extends Annotation>> annotationTypes = annotationMetadata.getAnnotationTypesByStereotype(value.getAnnotationName());
            metaTypes.add(getType());
            metaTypes.addAll(annotationTypes);
            return Collections.unmodifiableList(metaTypes);
        }
        return Collections.singletonList(getType());
    }

    @Override
    public boolean hasNonDefaultValue(String attributeName) {
        return value.contains(attributeName);
    }

    @Override
    public boolean hasDefaultValue(String attributeName) throws NoSuchElementException {
        return !value.contains(attributeName) && annotationMetadata.getDefaultValue(value.getAnnotationName(), attributeName, Object.class).isPresent();
    }

    @Override
    public byte getByte(String attributeName) throws NoSuchElementException {
        return value.byteValue(attributeName).orElseGet(() ->
            value.get(attributeName, Byte.class)
                .orElseThrow(missingHandler(attributeName))
        );
    }

    private Supplier<NoSuchElementException> missingHandler(String attributeName) {
        return () -> new NoSuchElementException("Annotation [" + value.getAnnotationName() + "] attribute [" + attributeName + "] does not exist");
    }

    @Override
    public byte[] getByteArray(String attributeName) throws NoSuchElementException {
        return value.byteValues(attributeName);
    }

    @Override
    public boolean getBoolean(String attributeName) throws NoSuchElementException {
        return value.booleanValue(attributeName).orElseGet(() ->
            value.get(attributeName, Boolean.class)
                .orElseThrow(missingHandler(attributeName))
        );
    }

    @Override
    public boolean[] getBooleanArray(String attributeName) throws NoSuchElementException {
        return value.booleanValues(attributeName);
    }

    @Override
    public char getChar(String attributeName) throws NoSuchElementException {
        return value.charValue(attributeName).orElseGet(() ->
            value.get(attributeName, Character.class)
                .orElseThrow(missingHandler(attributeName))
        );
    }

    @Override
    public char[] getCharArray(String attributeName) throws NoSuchElementException {
        return value.charValues(attributeName);
    }

    @Override
    public short getShort(String attributeName) throws NoSuchElementException {
        return value.shortValue(attributeName).orElseGet(() ->
            value.get(attributeName, Short.class)
                .orElseThrow(missingHandler(attributeName))
        );
    }

    @Override
    public short[] getShortArray(String attributeName) throws NoSuchElementException {
        return value.shortValues(attributeName);
    }

    @Override
    public int getInt(String attributeName) throws NoSuchElementException {
        return value.intValue(attributeName).orElseGet(() ->
            value.get(attributeName, Integer.class)
                .orElseThrow(missingHandler(attributeName))
        );
    }

    @Override
    public int[] getIntArray(String attributeName) throws NoSuchElementException {
        return value.intValues(attributeName);
    }

    @Override
    public long getLong(String attributeName) throws NoSuchElementException {
        return value.longValue(attributeName).orElseGet(() ->
            value.get(attributeName, Long.class)
                .orElseThrow(missingHandler(attributeName))
        );
    }

    @Override
    public long[] getLongArray(String attributeName) throws NoSuchElementException {
        return value.longValues(attributeName);
    }

    @Override
    public double getDouble(String attributeName) throws NoSuchElementException {
        return value.doubleValue(attributeName).orElseGet(() ->
            value.get(attributeName, Double.class)
                .orElseThrow(missingHandler(attributeName))
        );
    }

    @Override
    public double[] getDoubleArray(String attributeName) throws NoSuchElementException {
        return value.doubleValues(attributeName);
    }

    @Override
    public float getFloat(String attributeName) throws NoSuchElementException {
        return value.floatValue(attributeName).orElseGet(() ->
            value.get(attributeName, Float.class)
                .orElseThrow(missingHandler(attributeName))
        );
    }

    @Override
    public float[] getFloatArray(String attributeName) throws NoSuchElementException {
        return value.floatValues(attributeName);
    }

    @Override
    public String getString(String attributeName) throws NoSuchElementException {
        return value.stringValue(attributeName).orElseGet(() ->
            value.get(attributeName, String.class)
                .orElseThrow(missingHandler(attributeName))
        );
    }

    @Override
    public String[] getStringArray(String attributeName) throws NoSuchElementException {
        return value.stringValues(attributeName);
    }

    @Override
    public Class<?> getClass(String attributeName) throws NoSuchElementException {
        return value.classValue(attributeName).orElseGet(() ->
            value.get(attributeName, Class.class)
                .orElseThrow(missingHandler(attributeName))
        );
    }

    @Override
    public Class<?>[] getClassArray(String attributeName) throws NoSuchElementException {
        return value.classValues(attributeName);
    }

    @Override
    public <E extends Enum<E>> E getEnum(String attributeName, Class<E> type) throws NoSuchElementException {
        return value.enumValue(attributeName, type).orElseGet(() ->
            value.get(attributeName, type)
                .orElseThrow(missingHandler(attributeName))
        );
    }

    @Override
    public <E extends Enum<E>> E[] getEnumArray(String attributeName, Class<E> type) throws NoSuchElementException {
        return value.enumValues(attributeName, type);
    }

    @Override
    public <T extends Annotation> MergedAnnotation<T> getAnnotation(String attributeName, Class<T> type) throws NoSuchElementException {
        return value.getAnnotation(attributeName, type)
            .map(av -> new MergedAnnotationValue<>(annotationMetadata, av))
            .orElseThrow(missingHandler(attributeName));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Annotation> MergedAnnotation<T>[] getAnnotationArray(String attributeName, Class<T> type) throws NoSuchElementException {
        return value.getAnnotations(attributeName, type)
            .stream().map(av -> new MergedAnnotationValue<>(annotationMetadata, av))
            .toArray(MergedAnnotation[]::new);
    }

    @Override
    public Optional<Object> getValue(String attributeName) {
        return value.get(attributeName, Object.class);
    }

    @Override
    public <T> Optional<T> getValue(String attributeName, Class<T> type) {
        return value.get(attributeName, type);
    }

    @Override
    public Optional<Object> getDefaultValue(String attributeName) {
        return annotationMetadata.getDefaultValue(value.getAnnotationName(), attributeName, Object.class);
    }

    @Override
    public <T> Optional<T> getDefaultValue(String attributeName, Class<T> type) {
        return annotationMetadata.getDefaultValue(value.getAnnotationName(), attributeName, type);
    }

    @Override
    public MergedAnnotation<A> filterDefaultValues() {
        return new MergedAnnotationValue<A>(
            annotationMetadata,
            // copy to remove default values
            new AnnotationValue<>(value.getAnnotationName(), value.getValues())
        ) {
            @Override
            public boolean hasDefaultValue(String attributeName) throws NoSuchElementException {
                return false;
            }

            @Override
            public Optional<Object> getDefaultValue(String attributeName) {
                return Optional.empty();
            }

            @Override
            public <T> Optional<T> getDefaultValue(String attributeName, Class<T> type) {
                return Optional.empty();
            }
        };
    }

    @Override
    public MergedAnnotation<A> filterAttributes(Predicate<String> predicate) {
        return null;
    }

    @Override
    public MergedAnnotation<A> withNonMergedAttributes() {
        return this;
    }

    @Override
    public AnnotationAttributes asAnnotationAttributes(Adapt... adaptations) {
        return asMap(mergedAnnotation -> new AnnotationAttributes(), adaptations);
    }

    @SuppressWarnings("Convert2Diamond")
    @Override
    public Map<String, Object> asMap(Adapt... adaptations) {
        return asMap(mergedAnnotation -> new LinkedHashMap<String, Object>(), adaptations);
    }

    @Override
    public <T extends Map<String, Object>> T asMap(Function<MergedAnnotation<?>, T> factory, Adapt... adaptations) {
        return asMap(factory, value, adaptations);
    }

    private <T extends Map<String, Object>> T asMap(Function<MergedAnnotation<?>, T> factory, AnnotationValue<?> thisValue, Adapt[] adaptations) {
        Map<CharSequence, Object> values = thisValue.getValues();
        T newMap = factory.apply(this);
        Set<Adapt> adapts = CollectionUtils.setOf(adaptations);
        values.forEach((attribute, value) -> {
            if (value instanceof AnnotationClassValue) {
                AnnotationClassValue<?> acv = (AnnotationClassValue<?>) value;
                if (adapts.contains(Adapt.CLASS_TO_STRING)) {
                    value = acv.getName();
                } else {
                    value = acv.getType().orElse(null);
                }
            } else if (value instanceof AnnotationValue) {
                AnnotationValue<?> av = (AnnotationValue<?>) value;
                if (adapts.contains(Adapt.ANNOTATION_TO_MAP)) {
                    value = asMap(factory, av, adaptations);
                } else {
                    // TODO: synthesize annotation from value?
                }
            }
            newMap.put(attribute.toString(), value);
        });
        return newMap;
    }

    @Override
    public A synthesize() throws NoSuchElementException {
        return annotationMetadata.synthesize(getType());
    }

    @Override
    public Optional<A> synthesize(Predicate<? super MergedAnnotation<A>> condition) throws NoSuchElementException {
        if (condition != null && condition.test(this)) {
            return Optional.ofNullable(synthesize());
        }
        return Optional.empty();
    }
}
