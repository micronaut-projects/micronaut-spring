/*
 * Copyright 2017-2022 original authors
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
package io.micronaut.spring.beans;

import java.util.Arrays;
import java.util.stream.Stream;

import io.micronaut.context.BeanContext;
import io.micronaut.context.BeanResolutionContext;
import io.micronaut.context.DefaultBeanContext;
import io.micronaut.context.Qualifier;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.reflect.InstantiationUtils;
import io.micronaut.core.type.Argument;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.provider.AbstractProviderDefinition;
import io.micronaut.inject.qualifiers.AnyQualifier;
import io.micronaut.inject.qualifiers.Qualifiers;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.OrderComparator;

/**
 * Implements support for Spring's ObjectProvider interface.
 * @author graemerocher
 * @since 4.3.0
 */
public final class ObjectProviderBeanDefinition extends AbstractProviderDefinition<ObjectProvider<Object>> {
    @Override
    public boolean isEnabled(BeanContext context, BeanResolutionContext resolutionContext) {
        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Class<ObjectProvider<Object>> getBeanType() {
        return (Class) ObjectProvider.class;
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    protected ObjectProvider<Object> buildProvider(
        @NonNull BeanResolutionContext resolutionContext,
        @NonNull BeanContext context,
        @NonNull Argument<Object> argument,
        @Nullable Qualifier<Object> qualifier,
        boolean singleton) {
        return new MicronautObjectProvider(qualifier, context, resolutionContext, argument);
    }

    @Override
    protected boolean isAllowEmptyProviders(BeanContext context) {
        return true;
    }

    private final class MicronautObjectProvider implements ObjectProvider<Object>, MicronautContextInternal {
        private final Qualifier<Object> finalQualifier;
        private final Qualifier<Object> qualifier;
        private final BeanContext context;
        private final BeanResolutionContext resolutionContext;
        private final Argument<Object> argument;

        public MicronautObjectProvider(Qualifier<Object> qualifier, BeanContext context, BeanResolutionContext resolutionContext, Argument<Object> argument) {
            this.qualifier = qualifier;
            this.context = context;
            this.resolutionContext = resolutionContext;
            this.argument = argument;
            finalQualifier = qualifier instanceof AnyQualifier ? null : qualifier;
        }

        @Override
        public Stream<Object> stream() {
            return ((DefaultBeanContext) context).streamOfType(resolutionContext.copy(), argument, finalQualifier);
        }

        @Override
        public Stream<Object> orderedStream() {
            return ((DefaultBeanContext) context).streamOfType(resolutionContext.copy(), argument, finalQualifier)
                .sorted(OrderComparator.INSTANCE);
        }

        @Override
        public Object getObject(Object... args) throws BeansException {
            try {
                BeanDefinition<Object> beanDefinition = context.getBeanDefinition(argument, finalQualifier);
                Class<Object> beanType = beanDefinition.getBeanType();
                return InstantiationUtils.instantiate(
                    beanType,
                    Arrays.stream(beanDefinition.getConstructor().getArguments()).map(Argument::getClass).toArray(Class[]::new),
                    args
                );
            } catch (Exception e) {
                throw new BeanCreationException(e.getMessage(), e);
            }
        }

        @Override
        public Object getIfAvailable() throws BeansException {
            if (context.containsBean(argument, finalQualifier)) {
                return getObject();
            }
            return null;
        }

        @Override
        public Object getIfUnique() throws BeansException {
            if (context.getBeanDefinitions(argument, finalQualifier).size() == 1) {
                try {
                    return ((DefaultBeanContext) context).getBean(resolutionContext.copy(), argument, qualify(qualifier));
                } catch (Exception e) {
                    throw new BeanCreationException(e.getMessage(), e);
                }
            }
            return null;
        }

        @Override
        public Object getObject() throws BeansException {
            try {
                return ((DefaultBeanContext) context).getBean(resolutionContext.copy(), argument, finalQualifier);
            } catch (Exception e) {
                throw new BeanCreationException(e.getMessage(), e);
            }
        }

        private Qualifier<Object> qualify(Qualifier<Object> qualifier) {
            if (finalQualifier == null) {
                return qualifier;
            } else if (qualifier == null) {
                return finalQualifier;
            }

            //noinspection unchecked
            return Qualifiers.byQualifiers(finalQualifier, qualifier);
        }

    }
}
