package io.micronaut.spring.context.factory;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.BeanContext;
import io.micronaut.context.BeanRegistration;
import io.micronaut.context.LifeCycle;
import io.micronaut.context.annotation.*;
import io.micronaut.context.exceptions.NoSuchBeanException;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.naming.NameResolver;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.BeanDefinitionReference;
import io.micronaut.inject.DisposableBeanDefinition;
import io.micronaut.inject.ParametrizedBeanFactory;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.spring.context.aware.SpringAwareListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringValueResolver;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Scope;
import javax.inject.Singleton;
import java.beans.PropertyEditor;
import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.AccessControlContext;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Implementation of the {@link ListableBeanFactory} interface for Micronaut.
 *
 * @author graemerocher
 * @since 1.0
 */
@Singleton
@Primary
@Internal
public class MicronautBeanFactory extends DefaultListableBeanFactory implements ListableBeanFactory, AutowireCapableBeanFactory, HierarchicalBeanFactory, ConfigurableListableBeanFactory {

    private final BeanContext beanContext;
    private final Map<String, BeanDefinitionReference<?>> beanDefinitionMap = new LinkedHashMap<>(200);
    // only used for by name lookups
    private final Map<String, BeanDefinitionReference<?>> beanDefinitionsByName = new LinkedHashMap<>(200);
    private final SpringAwareListener springAwareListener;
    private final ConcurrentHashMap<String, Object> namedSingletons = new ConcurrentHashMap<>();
    private ClassLoader tempClassLoader;
    private ClassLoader beanClassLoader;

    public MicronautBeanFactory(BeanContext beanContext, SpringAwareListener awareListener) {
        this.beanContext = beanContext;
        this.springAwareListener = awareListener;
        final Collection<BeanDefinitionReference<?>> references = beanContext.getBeanDefinitionReferences();

        for (BeanDefinitionReference<?> reference : references) {
            final BeanDefinition<?> definition = reference.load(beanContext);
            if (definition instanceof ParametrizedBeanFactory) {
                // Spring doesn't have a similar concept. Consider these internal / non-public beans.
                continue;
            }
            if (definition.isEnabled(beanContext)) {
                if (definition.isIterable()) {
                    Collection<? extends BeanDefinition<?>> beanDefinitions = beanContext.getBeanDefinitions(definition.getBeanType());
                    for (BeanDefinition<?> beanDefinition : beanDefinitions) {
                        String beanName = computeBeanName(beanDefinition);
                        beanDefinitionMap.put(beanName, reference);
                    }
                } else {
                    String beanName = computeBeanName(definition);
                    beanDefinitionMap.put(beanName, reference);
                }

                if (definition.isAnnotationPresent(Named.class)) {
                    // explicit handling of named beans
                    final Optional<String> v = definition.getValue(Named.class, String.class);
                    v.ifPresent(s -> beanDefinitionsByName.put(s, reference));

                }
            }
        }
    }

    public static boolean isSingleton(AnnotationMetadata annotationMetadata) {
        if (annotationMetadata.isAnnotationPresent(EachProperty.class) || annotationMetadata.isAnnotationPresent(EachBean.class)) {
            return true;
        }
        final Optional<Class<? extends Annotation>> scope = annotationMetadata.getDeclaredAnnotationTypeByStereotype(Scope.class);
        // is singleton logic
        return (scope.isPresent() && scope.get() == Singleton.class) || annotationMetadata.getValue(DefaultScope.class, Singleton.class).isPresent();
    }

    private String computeBeanName(BeanDefinition<?> definition) {
        String name;
        if (definition instanceof NameResolver) {
            name = ((NameResolver) definition).resolveName().orElse(Primary.class.getSimpleName());
        } else {
            name = definition.getValue(Named.class, String.class).orElseGet(() ->
                    definition.getAnnotationTypeByStereotype(Qualifier.class).map(Class::getSimpleName).orElse(definition.getClass().getSimpleName())
            );
        }
        return definition.getBeanType().getName() + "(" + name + ")";
    }

    @Override
    public @Nonnull
    Object getBean(@Nonnull String name) throws BeansException {
        if (namedSingletons.containsKey(name)) {
            return namedSingletons.get(name);
        } else {
            final Class<?> type = getType(name);
            BeanDefinitionReference<?> reference = null;
            if (type != null) {
                reference = beanDefinitionMap.get(name);
            } else {
                reference = beanDefinitionsByName.get(name);
            }

            if (reference != null) {

                AnnotationMetadata annotationMetadata = reference.getAnnotationMetadata();
                Optional<Class<? extends Annotation>> q = annotationMetadata.getAnnotationTypeByStereotype(Qualifier.class);
                if (q.isPresent()) {
                    try {
                        return beanContext.getBean(type, Qualifiers.byAnnotation(annotationMetadata, q.get()));
                    } catch (NoSuchBeanException e) {
                        throw new NoSuchBeanDefinitionException(type, e.getMessage());
                    } catch (Exception e) {
                        throw new BeanCreationException(name, e.getMessage(), e);
                    }
                } else {
                    return getBean(type);
                }
            }
            throw new NoSuchBeanDefinitionException(name);
        }
    }

    @Override
    public @Nonnull
    <T> T getBean(@Nonnull String name, @Nonnull Class<T> requiredType) throws BeansException {
        try {
            return beanContext.getBean(requiredType, Qualifiers.byName(name));
        } catch (NoSuchBeanException e) {
            throw new NoSuchBeanDefinitionException(requiredType, e.getMessage());
        } catch (Exception e) {
            throw new BeanCreationException(name, e.getMessage(), e);
        }
    }

    @Override
    public @Nonnull
    Object getBean(@Nonnull String name, @Nonnull Object... args) throws BeansException {
        final Class<?> type = getType(name);
        if (type != null) {
            return beanContext.createBean(type, args);
        }
        throw new NoSuchBeanDefinitionException(name);
    }

    @Override
    public @Nonnull
    <T> T getBean(@Nonnull Class<T> requiredType) throws BeansException {
        // unfortunate hack
        try {
            return beanContext.getBean(requiredType);
        } catch (NoSuchBeanException e) {
            throw new NoSuchBeanDefinitionException(requiredType, e.getMessage());
        }
    }

    @Override
    public @Nonnull
    <T> T getBean(@Nonnull Class<T> requiredType, @Nonnull Object... args) throws BeansException {
        try {
            return beanContext.createBean(requiredType, args);
        } catch (NoSuchBeanException e) {
            throw new NoSuchBeanDefinitionException(requiredType, e.getMessage());
        }
    }

    @Override
    public @Nonnull
    <T> ObjectProvider<T> getBeanProvider(@Nonnull Class<T> requiredType) {
        return new ObjectProvider<T>() {
            @Override
            public T getObject(Object... args) throws BeansException {
                return beanContext.createBean(requiredType, args);
            }

            @Override
            public T getIfAvailable() throws BeansException {
                if (beanContext.containsBean(requiredType)) {
                    return beanContext.getBean(requiredType);
                }
                return null;
            }

            @Override
            public T getIfUnique() throws BeansException {
                final Collection<T> beansOfType = beanContext.getBeansOfType(requiredType);
                if (beansOfType.size() == 1) {
                    return beansOfType.stream().findFirst().orElse(null);
                }
                return null;
            }

            @Override
            public T getObject() throws BeansException {
                return beanContext.getBean(requiredType);
            }
        };
    }

    @Override
    public @Nonnull
    <T> ObjectProvider<T> getBeanProvider(@Nonnull ResolvableType requiredType) {
        final Class<T> resolved = (Class<T>) requiredType.resolve();
        return getBeanProvider(resolved);
    }

    @Override
    public boolean containsBean(@Nonnull String name) {
        return namedSingletons.containsKey(name) || beanDefinitionMap.containsKey(name);
    }

    @Override
    public boolean isSingleton(@Nonnull String name) throws NoSuchBeanDefinitionException {
        if (namedSingletons.containsKey(name)) {
            return true;
        } else {
            final BeanDefinitionReference<?> definition = beanDefinitionMap.get(name);
            if (definition != null) {
                return isSingleton(definition);
            }
            return false;
        }
    }

    protected boolean isSingleton(BeanDefinitionReference<?> definition) {
        final AnnotationMetadata annotationMetadata = definition.getAnnotationMetadata();
        return isSingleton(annotationMetadata);
    }

    @Override
    public boolean isPrototype(@Nonnull String name) throws NoSuchBeanDefinitionException {
        if (namedSingletons.containsKey(name)) {
            return false;
        }

        final BeanDefinitionReference<?> definition = beanDefinitionMap.get(name);
        if (definition != null) {
            final AnnotationMetadata annotationMetadata = definition.getAnnotationMetadata();
            if (annotationMetadata.hasDeclaredStereotype(Prototype.class)) {
                return true;
            } else {
                final boolean hasScope = annotationMetadata.getAnnotationNamesByStereotype(Scope.class).isEmpty();
                return !hasScope;
            }
        }
        return false;
    }

    @Override
    public boolean isTypeMatch(@Nonnull String name, @Nonnull ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        final Class<?> resolved = typeToMatch.resolve();
        if (resolved != null) {
            return isTypeMatch(name, resolved);
        }
        return false;
    }

    @Override
    public boolean isTypeMatch(@Nonnull String name, @Nonnull Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        final Class<?> beanType = getType(name);
        if (beanType != null) {
            return typeToMatch.isAssignableFrom(beanType);
        }
        return false;
    }

    @Override
    public Class<?> getType(@Nonnull String name) throws NoSuchBeanDefinitionException {
        Object o = namedSingletons.get(name);
        if (o != null) {
            return o.getClass();
        }

        final BeanDefinitionReference<?> definition = beanDefinitionMap.get(name);
        if (definition != null) {
            return definition.getBeanType();
        }
        return null;
    }

    @Override
    public boolean containsBeanDefinition(@Nonnull String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return beanDefinitionMap.size();
    }

    @Override
    public @Nonnull
    String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    @Override
    public @Nonnull
    String[] getBeanNamesForType(@Nonnull ResolvableType type) {
        final Class<?> resolved = type.resolve();
        if (resolved != null) {
            return getBeanNamesForType(resolved);
        }
        return StringUtils.EMPTY_STRING_ARRAY;
    }

    @Override
    public @Nonnull
    String[] getBeanNamesForType(Class<?> type) {
        return getBeanNamesForType(type, true, true);
    }

    @Override
    public @Nonnull
    String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        final Collection<? extends BeanDefinition<?>> beanDefinitions = beanContext.getBeanDefinitions(type);
        return beansToNames(beanDefinitions);
    }

    @Override
    public @Nonnull
    <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return getBeansOfType(type, true, true);
    }

    @Override
    public @Nonnull
    <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        if (type == null) {
            return Collections.emptyMap();
        }
        final Collection<T> beansOfType = beanContext.getBeansOfType(type);
        Map<String, T> beans = new HashMap<>(beansOfType.size());
        for (T bean : beansOfType) {
            final Optional<BeanRegistration<T>> reg = beanContext.findBeanRegistration(bean);
            reg.ifPresent(registration -> beans.put(registration.getBeanDefinition().getClass().getName(), bean));
        }
        return beans;
    }

    @Override
    public @Nonnull
    String[] getBeanNamesForAnnotation(@Nonnull Class<? extends Annotation> annotationType) {
        final Collection<BeanDefinition<?>> beanDefinitions = beanContext.getBeanDefinitions(Qualifiers.byStereotype(annotationType));
        return beansToNames(beanDefinitions);
    }

    @Override
    public @Nonnull
    Map<String, Object> getBeansWithAnnotation(@Nonnull Class<? extends Annotation> annotationType) throws BeansException {
        if (annotationType == null) {
            return Collections.emptyMap();
        }
        final Collection<BeanDefinition<?>> definitions = beanContext.getBeanDefinitions(Qualifiers.byStereotype(annotationType));
        Map<String, Object> beans = new HashMap<>(definitions.size());
        for (BeanDefinition<?> definition : definitions) {
            beans.put(computeBeanName(definition), beanContext.getBean(definition.getBeanType(), Qualifiers.byStereotype(annotationType)));
        }
        return beans;
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(@Nonnull String beanName, @Nonnull Class<A> annotationType) throws NoSuchBeanDefinitionException {
        final BeanDefinitionReference<?> ref = beanDefinitionMap.get(beanName);
        if (ref != null) {
            return ref.getAnnotationMetadata().synthesize(annotationType);
        }
        return null;
    }

    private String[] beansToNames(Collection<? extends BeanDefinition<?>> beanDefinitions) {
        return beanDefinitions.stream()
                .filter(bd -> !(bd instanceof ParametrizedBeanFactory))
                .map(this::computeBeanName).toArray(String[]::new);
    }

    @Override
    public @Nonnull
    <T> T createBean(@Nonnull Class<T> beanClass) throws BeansException {
        return beanContext.createBean(beanClass);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void autowireBean(@Nonnull Object existingBean) throws BeansException {
        if (existingBean != null) {
            beanContext.inject(existingBean);
        }
    }

    @Override
    public Object configureBean(Object existingBean, String beanName) throws BeansException {
        final Object injected = beanContext.inject(existingBean);
        return initializeBean(injected, beanName);
    }

    @Override
    public Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        return createBean(beanClass);
    }

    @Override
    public Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        return beanContext.getBean(beanClass);
    }

    @Override
    public void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws BeansException {
        autowireBean(existingBean);
    }

    @Override
    public void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException {
        autowireBean(existingBean);
    }

    @Override
    public Object initializeBean(Object existingBean, String beanName) throws BeansException {
        return springAwareListener.onBeanCreated(existingBean);
    }


    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        return existingBean;
    }

    @Override
    public void destroyBean(Object existingBean) {
        if (existingBean instanceof Closeable) {
            try {
                ((Closeable) existingBean).close();
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invocation of destroy method failed for bean [" + existingBean + "]: " + e.getMessage(), e);
                }
            }
        } else if (existingBean instanceof DisposableBean) {
            try {
                ((DisposableBean) existingBean).destroy();
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invocation of destroy method failed for bean [" + existingBean + "]: " + e.getMessage(), e);
                }
            }
        } else if (existingBean instanceof AutoCloseable) {
            try {
                ((AutoCloseable) existingBean).close();
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invocation of destroy method failed for bean [" + existingBean + "]: " + e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException {
        throw new UnsupportedOperationException("Method resolveNamedBean(..) is not supported by this implementation");
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName) throws BeansException {
        throw new UnsupportedOperationException("Method resolveDependency(..) is not supported by this implementation");
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName, Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException {
        throw new UnsupportedOperationException("Method resolveDependency(..) is not supported by this implementation");
    }

    @Override
    public boolean containsLocalBean(String name) {
        return namedSingletons.containsKey(name) || containsBean(name);
    }

    public BeanContext getBeanContext() {
        return beanContext;
    }

    @Override
    public void registerResolvableDependency(Class<?> dependencyType, Object autowiredValue) {
        beanContext.registerSingleton((Class) dependencyType, autowiredValue);
    }

    @Override
    public boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public org.springframework.beans.factory.config.BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        final BeanDefinitionReference<?> reference = beanDefinitionMap.get(beanName);
        if (reference != null) {
            final BeanDefinition<?> def = reference.load(beanContext);
            if (def.isEnabled(beanContext)) {
                final GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
                genericBeanDefinition.setBeanClass(def.getBeanType());
                return genericBeanDefinition;
            }
        }
        throw new NoSuchBeanDefinitionException(beanName);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T doGetBean(String name, Class<T> requiredType, Object[] args, boolean typeCheckOnly) throws BeansException {
        BeanDefinitionReference<?> reference = beanDefinitionMap.get(name);
        if (reference == null && requiredType == null) {
            // by name, with no type lookups
            reference = beanDefinitionsByName.get(name);
        }

        if (reference != null) {
            final BeanDefinition<?> definition = reference.load(beanContext);
            if (definition.isEnabled(beanContext)) {
                if (requiredType == null) {
                    requiredType = (Class<T>) definition.getBeanType();
                }

                io.micronaut.context.Qualifier<T> q = (io.micronaut.context.Qualifier<T>) definition.getValue(Named.class, String.class)
                        .map((String n) -> {
                            if (Primary.class.getName().equals(n)) {
                                return n;
                            }
                            return Qualifiers.byName(n);
                        })
                        .orElseGet(() ->
                                {
                                    if (definition.hasDeclaredStereotype(Primary.class)) {
                                        return null;
                                    }
                                    final String annotationName = definition.getAnnotationNameByStereotype(Qualifier.class).orElse(null);
                                    if (annotationName != null) {
                                        return Qualifiers.byAnnotation(definition, annotationName);
                                    }
                                    return null;
                                }
                        );
                if (q != null) {
                    return beanContext.getBean(requiredType, q);
                } else {
                    return beanContext.getBean(requiredType);
                }
            }
        }
        throw new NoSuchBeanDefinitionException(name);
    }

    @Override
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        return getSingleton(beanName);
    }

    @Override
    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        return getSingleton(beanName);
    }

    @Override
    public Iterator<String> getBeanNamesIterator() {
        return Arrays.asList(getBeanDefinitionNames()).iterator();
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }

    @Override
    public void setTempClassLoader(ClassLoader tempClassLoader) {
        this.tempClassLoader = tempClassLoader;
    }

    @Override
    public ClassLoader getTempClassLoader() {
        return tempClassLoader;
    }

    @Override
    public boolean hasEmbeddedValueResolver() {
        return beanContext instanceof ApplicationContext;
    }

    @Override
    public String resolveEmbeddedValue(String value) {
        if (hasEmbeddedValueResolver()) {
            ApplicationContext ctx = (ApplicationContext) beanContext;
            return ctx.resolveRequiredPlaceholders(value);
        }
        return null;
    }

    @Override
    public boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public void destroyBean(String beanName, Object beanInstance) {
        final BeanDefinitionReference<?> reference = beanDefinitionMap.get(beanName);
        if (reference != null) {
            final BeanDefinition<?> ref = reference.load(beanContext);
            if (ref instanceof DisposableBeanDefinition) {
                ((DisposableBeanDefinition) ref).dispose(beanContext, beanInstance);
            }
        }
    }

    @Override
    public void destroyScopedBean(String beanName) {
        // no-op / managed by Micronaut
    }

    @Override
    public void destroySingletons() {
        for (Object value : namedSingletons.values()) {
            destroyBean(value);
        }
        namedSingletons.clear();
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        final Class type = singletonObject.getClass();
        beanContext.registerSingleton(
                type,
                singletonObject,
                Qualifiers.byName(beanName)
        );
        namedSingletons.put(beanName, singletonObject);
    }

    @Override
    public Object getSingleton(String beanName) {
        if (isSingleton(beanName)) {
            return getBean(beanName);
        }
        throw new NoSuchBeanDefinitionException(beanName);
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return isSingleton(beanName);
    }

    @Override
    public String[] getSingletonNames() {
        final Stream<String> names = beanDefinitionMap.entrySet().stream().filter(entry ->
                isSingleton(entry.getValue())
        ).map(Map.Entry::getKey);
        return names.toArray(String[]::new);
    }

    @Override
    public int getSingletonCount() {
        return getSingletonNames().length;
    }

    @Override
    public void registerBeanDefinition(String beanName, org.springframework.beans.factory.config.BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        if (beanDefinition instanceof org.springframework.beans.factory.support.AbstractBeanDefinition) {
            org.springframework.beans.factory.support.AbstractBeanDefinition abstractBeanDefinition = (org.springframework.beans.factory.support.AbstractBeanDefinition) beanDefinition;
            final Supplier<?> instanceSupplier = abstractBeanDefinition.getInstanceSupplier();
            if (instanceSupplier != null) {
                registerSingleton(beanName, instanceSupplier.get());
            }
        }
    }
}
