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
package io.micronaut.spring.core.type;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import io.micronaut.core.annotation.Internal;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.spring.core.annotation.MicronautMergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

/**
 * Implementation of Spring's {@link AnnotationMetadata} that backs onto a {@link BeanDefinition}.
 *
 * @author graemerocher
 * @author graemerocher
 * @since 4.3.0
 */
@Internal
public final class BeanDefinitionSpringMetadata implements AnnotationMetadata {
    private final BeanDefinition<?> beanDefinition;

    public BeanDefinitionSpringMetadata(BeanDefinition<?> beanDefinition) {
        this.beanDefinition = beanDefinition;
    }

    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        return Collections.emptySet();
    }

    @Override
    public MergedAnnotations getAnnotations() {
        return new MicronautMergedAnnotations(beanDefinition.getAnnotationMetadata());
    }

    @Override
    public String getClassName() {
        return beanDefinition.getBeanType().getName();
    }

    @Override
    public boolean isInterface() {
        return beanDefinition.getBeanType().isInterface();
    }

    @Override
    public boolean isAnnotation() {
        return beanDefinition.getBeanType().isAnnotation();
    }

    @Override
    public boolean isAbstract() {
        return beanDefinition.isAbstract();
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(beanDefinition.getBeanType().getModifiers());
    }

    @Override
    public boolean isIndependent() {
        return getEnclosingClassName() == null;
    }

    @Override
    public String getEnclosingClassName() {
        Class<?> ec = beanDefinition.getBeanType().getEnclosingClass();
        if (ec != null) {
            return ec.getName();
        }
        return null;
    }

    @Override
    public String getSuperClassName() {
        Class<?> st = beanDefinition.getBeanType().getSuperclass();
        if (st != null && st != Object.class) {
            return st.getName();
        }
        return null;
    }

    @Override
    public String[] getInterfaceNames() {
        Class<?>[] interfaces = beanDefinition.getBeanType().getInterfaces();
        return Arrays.stream(interfaces).map(Class::getName).toArray(String[]::new);
    }

    @Override
    public String[] getMemberClassNames() {
        Class<?>[] classes = beanDefinition.getBeanType().getClasses();
        return Arrays.stream(classes).map(Class::getName).toArray(String[]::new);
    }
}
