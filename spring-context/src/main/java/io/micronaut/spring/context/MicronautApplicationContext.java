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
package io.micronaut.spring.context;

import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.annotation.Secondary;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.reflect.GenericTypeUtils;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.spring.beans.MicronautContextInternal;
import io.micronaut.spring.context.env.MicronautEnvironment;
import io.micronaut.spring.context.event.MicronautApplicationEventPublisher;
import io.micronaut.spring.context.factory.MicronautBeanFactory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of Spring's {@link ApplicationContext} interface that delegates to Micronaut.
 *
 * <p>This can either be created manually via {@link #MicronautApplicationContext(ApplicationContextBuilder)} or looked up as a
 * bean when running from Micronaut. The {@link ApplicationContextAware} interface is supported.</p>
 *
 * @author graemerocher
 * @since 1.0
 */
@Singleton
@Secondary
public class MicronautApplicationContext implements ManagedApplicationContext, ConfigurableApplicationContext, MicronautContextInternal {

    private final io.micronaut.context.ApplicationContext micronautContext;
    private ConfigurableEnvironment environment;
    private MicronautBeanFactory beanFactory;
    private MessageSource messageSource;
    private ApplicationEventPublisher eventPublisher;
    private long startupDate;
    private String id = ObjectUtils.identityToString(this);
    private ApplicationContext parent;
    private ApplicationStartup applicationStartup;

    /**
     * Default constructor.
     * @param micronautContext The micronaut context to delegate to
     * @param environment The environment
     * @param beanFactory The bean factory
     * @param eventPublisher The event publisher
     * @param messageSource The message source
     */
    @Inject
    public MicronautApplicationContext(
            io.micronaut.context.ApplicationContext micronautContext,
            ConfigurableEnvironment environment,
            MicronautBeanFactory beanFactory,
            ApplicationEventPublisher eventPublisher,
            @Nullable MessageSource messageSource) {
        this.micronautContext = micronautContext;
        this.environment = environment;
        this.beanFactory = beanFactory;
        this.messageSource = messageSource;
        this.eventPublisher = eventPublisher;
        this.startupDate = System.currentTimeMillis();
    }

    /**
     * Default constructor.
     */
    public MicronautApplicationContext() {
        this(io.micronaut.context.ApplicationContext.builder());
    }

    /**
     * Customization constructor.
     * @param contextBuilder The context builder
     */
    public MicronautApplicationContext(ApplicationContextBuilder contextBuilder) {
        this.micronautContext = contextBuilder.build();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getApplicationName() {
        return micronautContext.getProperty("micronaut.application.name", String.class).orElse(io.micronaut.context.env.Environment.DEFAULT_NAME);
    }

    @Override
    public String getDisplayName() {
        return getApplicationName();
    }

    @Override
    public long getStartupDate() {
        return startupDate;
    }

    @Override
    public ApplicationContext getParent() {
        return parent;
    }

    @Override
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return beanFactory;
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        if (parent != null) {
            return parent.getAutowireCapableBeanFactory();
        } else {
            return null;
        }
    }

    @Override
    public boolean containsLocalBean(String name) {
        return beanFactory.containsLocalBean(name);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanFactory.containsBeanDefinition(beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return beanFactory.getBeanDefinitionCount();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanFactory.getBeanDefinitionNames();
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
        return beanFactory.getBeanProvider(requiredType, allowEagerInit);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType, boolean allowEagerInit) {
        return beanFactory.getBeanProvider(requiredType, allowEagerInit);
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type) {
        return beanFactory.getBeanNamesForType(type);
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        return beanFactory.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        return beanFactory.getBeanNamesForType(type);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return beanFactory.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return beanFactory.getBeansOfType(type);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        return beanFactory.getBeansOfType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        return beanFactory.getBeanNamesForAnnotation(annotationType);
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        return beanFactory.getBeansWithAnnotation(annotationType);
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
        return beanFactory.findAnnotationOnBean(beanName, annotationType);
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return beanFactory.findAnnotationOnBean(beanName, annotationType, allowFactoryBeanInit);
    }

    @Override
    public <A extends Annotation> Set<A> findAllAnnotationsOnBean(String beanName, Class<A> annotationType, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return beanFactory.findAllAnnotationsOnBean(beanName, annotationType, allowFactoryBeanInit);
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return beanFactory.getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return beanFactory.getBean(name, requiredType);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return beanFactory.getBean(name, args);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return beanFactory.getBean(requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return beanFactory.getBean(requiredType, args);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        return beanFactory.getBeanProvider(requiredType);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        return beanFactory.getBeanProvider(requiredType);
    }

    @Override
    public boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.isSingleton(name);
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.isPrototype(name);
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return beanFactory.isTypeMatch(name, typeToMatch);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return beanFactory.isTypeMatch(name, typeToMatch);
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getType(name);
    }

    @Override
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return beanFactory.getType(name, allowFactoryBeanInit);
    }

    @Override
    public String[] getAliases(String name) {
        return beanFactory.getAliases(name);
    }

    @Override
    public void publishEvent(Object event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        if (messageSource != null) {
            return messageSource.getMessage(code, args, defaultMessage, locale);
        }
        return null;
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        if (messageSource != null) {
            return messageSource.getMessage(code, args, locale);
        }
        return null;
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        if (messageSource != null) {
            return messageSource.getMessage(resolvable, locale);
        }
        return null;

    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setParent(ApplicationContext parent) {
        this.parent = parent;
        if (parent != null) {
            this.beanFactory.setParentBeanFactory(parent.getAutowireCapableBeanFactory());
        }
    }

    @Override
    public void setEnvironment(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public ConfigurableEnvironment getEnvironment() {
        return environment;
    }

    @Override
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        this.applicationStartup = applicationStartup;
    }

    @Override
    public ApplicationStartup getApplicationStartup() {
        return applicationStartup;
    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        beanFactory.getBeanContext().registerSingleton(postProcessor);
    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        beanFactory.getBeanContext().registerSingleton(listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        beanFactory.getBeanContext().destroyBean(listener);
    }

    @Override
    public void setClassLoader(ClassLoader classLoader) {
        // no-op, unsupported
    }

    @Override
    public void addProtocolResolver(ProtocolResolver resolver) {
        beanFactory.getBeanContext().registerSingleton(resolver);
    }

    @Override
    public void refresh() throws BeansException, IllegalStateException {
        stop();
        start();
    }

    @Override
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        if (environment instanceof MicronautEnvironment) {
            return ((MicronautEnvironment) environment).getEnvironment().getResources(locationPattern).toArray(Resource[]::new);
        }
        return new Resource[0];
    }

    @Override
    public Resource getResource(String location) {
        if (environment instanceof MicronautEnvironment) {
            return ((MicronautEnvironment) environment).getEnvironment().getResource(location).map(UrlResource::new).orElse(null);
        }
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        if (environment instanceof MicronautEnvironment) {
            return ((MicronautEnvironment) environment).getEnvironment().getClassLoader();
        }
        return null;
    }

    @Override
    public void close() {
        stop();
    }

    @Override
    public boolean isActive() {
        return isRunning();
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return beanFactory;
    }

    @Override
    public void start() {
        if (!isRunning()) {
            if (!micronautContext.isRunning()) {
                micronautContext.start();
            }
            this.beanFactory = micronautContext.getBean(MicronautBeanFactory.class);
            this.environment = micronautContext.getBean(MicronautEnvironment.class);
            this.eventPublisher = micronautContext.getBean(MicronautApplicationEventPublisher.class);
            this.messageSource = micronautContext.findBean(MessageSource.class).orElse(null);
            this.startupDate = System.currentTimeMillis();
        }
    }

    @Override
    public void stop() {
        if (isRunning() && micronautContext.isRunning()) {
            micronautContext.stop();
        }
    }

    @Override
    public boolean isRunning() {
        return micronautContext.isRunning();
    }

    /**
     * Method executed on startup.
     * @param startupEvent The startup event.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @EventListener
    protected void onStartup(StartupEvent startupEvent) {
        Collection<SmartInitializingSingleton> smartSingletons =
            micronautContext.getBeansOfType(SmartInitializingSingleton.class);
        for (SmartInitializingSingleton smartSingleton : smartSingletons) {
            smartSingleton.afterSingletonsInstantiated();
        }
        Collection<BeanDefinition<ApplicationListener>> beanDefinitions = micronautContext.getBeanDefinitions(ApplicationListener.class);
        for (BeanDefinition<ApplicationListener> beanDefinition : beanDefinitions) {
            Class<?> t = GenericTypeUtils.resolveInterfaceTypeArgument(beanDefinition.getBeanType(), ApplicationListener.class).orElse(null);
            if (t != null && t.equals(ContextRefreshedEvent.class)) {
                ApplicationListener applicationListener = micronautContext.getBean(beanDefinition);
                applicationListener.onApplicationEvent(new ContextRefreshedEvent(this));
            }
        }
    }
}
