/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import io.micronaut.spring.context.env.MicronautEnvironment;
import io.micronaut.spring.context.event.MicronautApplicationEventPublisher;
import io.micronaut.spring.context.factory.MicronautBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.*;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.ObjectUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;

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
public class MicronautApplicationContext implements ManagedApplicationContext, ConfigurableApplicationContext {

    private final io.micronaut.context.ApplicationContext micronautContext;
    private ConfigurableEnvironment environment;
    private MicronautBeanFactory beanFactory;
    private MessageSource messageSource;
    private ApplicationEventPublisher eventPublisher;
    private long startupDate;
    private String id = ObjectUtils.identityToString(this);
    private ApplicationContext parent;

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
        this(io.micronaut.context.ApplicationContext.build());
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
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        beanFactory.getBeanContext().registerSingleton(postProcessor);
    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        beanFactory.getBeanContext().registerSingleton(listener);
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
            micronautContext.start();
            this.beanFactory = micronautContext.getBean(MicronautBeanFactory.class);
            this.environment = micronautContext.getBean(MicronautEnvironment.class);
            this.eventPublisher = micronautContext.getBean(MicronautApplicationEventPublisher.class);
            this.messageSource = micronautContext.findBean(MessageSource.class).orElse(null);
            this.startupDate = System.currentTimeMillis();
        }
    }

    @Override
    public void stop() {
        if (isRunning()) {
            micronautContext.stop();
        }
    }

    @Override
    public boolean isRunning() {
        return micronautContext.isRunning();
    }
}
