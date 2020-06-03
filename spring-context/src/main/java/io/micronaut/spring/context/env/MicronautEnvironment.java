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
package io.micronaut.spring.context.env;

import io.micronaut.context.annotation.Primary;
import io.micronaut.context.env.EnvironmentPropertySource;
import io.micronaut.context.env.SystemPropertiesPropertySource;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.util.StringUtils;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.env.*;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.*;

/**
 * Implementation of the {@link Environment} interface for Micronaut.
 *
 * @author graemerocher
 * @since 1.0
 */
@Singleton
@Primary
@Internal
public class MicronautEnvironment implements ConfigurableEnvironment {
    private final io.micronaut.context.env.Environment environment;
    private String[] requiredProperties;
    private ConfigurableConversionService conversionService;

    /**
     * Default constructor.
     * @param environment The target environment
     */
    public MicronautEnvironment(io.micronaut.context.env.Environment environment) {
        this.environment = environment;
    }

    @Override
    public String[] getActiveProfiles() {
        return environment.getActiveNames().toArray(new String[0]);
    }

    @Override
    public String[] getDefaultProfiles() {
        return StringUtils.EMPTY_STRING_ARRAY;
    }

    @Override
    public boolean acceptsProfiles(@NonNull String... profiles) {
        Assert.notNull(profiles, "Profiles must not be null");
        return Arrays.stream(profiles).anyMatch(p -> environment.getActiveNames().contains(p));
    }

    @Override
    public boolean acceptsProfiles(@Nonnull Profiles profiles) {
        Assert.notNull(profiles, "Profiles must not be null");
        return profiles.matches(this::isProfileActive);
    }

    private boolean isProfileActive(String profile) {
        Set<String> currentActiveProfiles = environment.getActiveNames();
        return currentActiveProfiles.contains(profile);
    }

    @Override
    public boolean containsProperty(String key) {
        return environment.containsProperty(key) || environment.containsProperties(key);
    }

    @Override
    public @Nullable String getProperty(@Nonnull String key) {
        Assert.notNull(key, "Key must not be null");
        return environment.getProperty(key, String.class).orElse(null);
    }

    @Override
    public @Nonnull String getProperty(@Nonnull String key, @Nonnull String defaultValue) {
        Assert.notNull(key, "Key must not be null");
        return environment.getProperty(key, String.class).orElse(defaultValue);
    }

    @Override
    public @Nullable <T> T getProperty(@Nonnull String key, @Nonnull Class<T> targetType) {
        Assert.notNull(key, "Key must not be null");
        Assert.notNull(targetType, "Target type must not be null");
        return environment.getProperty(key, targetType).orElse(null);

    }

    @Override
    public <T> T getProperty(@Nonnull String key, @Nonnull Class<T> targetType, @Nonnull T defaultValue) {
        Assert.notNull(key, "Key must not be null");
        Assert.notNull(targetType, "Target type must not be null");
        return environment.getProperty(key, targetType).orElse(defaultValue);
    }

    @Override
    public String getRequiredProperty(@Nonnull String key) throws IllegalStateException {
        Assert.notNull(key, "Key must not be null");

        return environment.getProperty(key, String.class).orElseThrow(() -> new IllegalStateException("Property with key [" + key + "] not present"));
    }

    @Override
    public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
        Assert.notNull(key, "Key must not be null");
        Assert.notNull(targetType, "Target type must not be null");

        return environment.getProperty(key, targetType).orElseThrow(() -> new IllegalStateException("Property with key [" + key + "] not present"));
    }

    @Override
    public String resolvePlaceholders(@Nonnull String text) {
        return environment.getPlaceholderResolver().resolvePlaceholders(text).orElse(text);
    }

    @Override
    public String resolveRequiredPlaceholders(@Nonnull String text) throws IllegalArgumentException {
        return environment.getPlaceholderResolver().resolveRequiredPlaceholders(text);
    }

    /**
     * The target environment.
     * @return The environment
     */
    public @Nonnull io.micronaut.context.env.Environment getEnvironment() {
        return environment;
    }

    @Override
    public void setActiveProfiles(String... profiles) {
        throw new UnsupportedOperationException("Method setActiveProfiles not supported");
    }

    @Override
    public void addActiveProfile(String profile) {
        throw new UnsupportedOperationException("Method addActiveProfile not supported");
    }

    @Override
    public void setDefaultProfiles(String... profiles) {
        throw new UnsupportedOperationException("Method setDefaultProfiles not supported");
    }

    @Override
    public MutablePropertySources getPropertySources() {
        return new MutablePropertySources();
    }

    @Override
    public Map<String, Object> getSystemProperties() {
        final Collection<io.micronaut.context.env.PropertySource> propertySources = environment.getPropertySources();
        final Optional<io.micronaut.context.env.PropertySource> opt = propertySources.stream()
                .filter(ps -> ps instanceof SystemPropertiesPropertySource)
                .findFirst();
        if (opt.isPresent()) {
            SystemPropertiesPropertySource ps = (SystemPropertiesPropertySource) opt.get();
            return ps.asMap();
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> getSystemEnvironment() {
        final Collection<io.micronaut.context.env.PropertySource> propertySources = environment.getPropertySources();
        final Optional<io.micronaut.context.env.PropertySource> opt = propertySources.stream()
                .filter(ps -> ps instanceof EnvironmentPropertySource)
                .findFirst();
        if (opt.isPresent()) {
            EnvironmentPropertySource ps = (EnvironmentPropertySource) opt.get();
            return ps.asMap();
        }
        return Collections.emptyMap();
    }

    @Override
    public void merge(ConfigurableEnvironment parent) {
        throw new UnsupportedOperationException("Method parent not supported");
    }

    @Override
    public ConfigurableConversionService getConversionService() {
        return conversionService;
    }

    @Override
    public void setConversionService(ConfigurableConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public void setPlaceholderPrefix(String placeholderPrefix) {
        throw new UnsupportedOperationException("Method setPlaceholderPrefix not supported");
    }

    @Override
    public void setPlaceholderSuffix(String placeholderSuffix) {
        throw new UnsupportedOperationException("Method setPlaceholderSuffix not supported");
    }

    @Override
    public void setValueSeparator(String valueSeparator) {
        throw new UnsupportedOperationException("Method setValueSeparator not supported");
    }

    @Override
    public void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders) {
        throw new UnsupportedOperationException("Method setIgnoreUnresolvableNestedPlaceholders not supported");
    }

    @Override
    public void setRequiredProperties(String... requiredProperties) {
        this.requiredProperties = requiredProperties;
    }

    @Override
    public void validateRequiredProperties() throws MissingRequiredPropertiesException {
        if (requiredProperties != null) {
            Set<String> missingProps = new HashSet<>();
            for (String requiredProperty : requiredProperties) {
                if (!environment.containsProperty(requiredProperty)) {
                    missingProps.add(requiredProperty);
                }
            }

            if (!missingProps.isEmpty()) {
                throw new MissingRequiredPropertiesException() {
                    @Override
                    public Set<String> getMissingRequiredProperties() {
                        return missingProps;
                    }
                };

            }
        }
    }
}
