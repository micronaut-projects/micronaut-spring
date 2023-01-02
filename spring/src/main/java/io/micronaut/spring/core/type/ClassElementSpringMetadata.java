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

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

import io.micronaut.core.annotation.Internal;
import io.micronaut.inject.ast.ClassElement;
import io.micronaut.inject.ast.ElementQuery;
import io.micronaut.inject.ast.MethodElement;
import io.micronaut.spring.core.annotation.MicronautMergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

/**
 *
 * Implementation of {@link AnnotationMetadata} that backs onto a Micronaut {@link ClassElement}.
 *
 * @author graemerocher
 * @since 4.3.0
 */
@Internal
public final class ClassElementSpringMetadata implements AnnotationMetadata {
    private final ClassElement classElement;

    public ClassElementSpringMetadata(ClassElement classElement) {
        this.classElement = classElement;
    }

    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        return classElement.getEnclosedElements(
            ElementQuery.ALL_METHODS.annotated(ann -> ann.hasAnnotation(annotationName))
        ).stream().map(MethodMetadataImpl::new)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<MethodMetadata> getDeclaredMethods() {
        return classElement.getEnclosedElements(
                ElementQuery.ALL_METHODS.onlyDeclared()
            ).stream().map(MethodMetadataImpl::new)
            .collect(Collectors.toSet());
    }

    @Override
    public MergedAnnotations getAnnotations() {
        return new MicronautMergedAnnotations(classElement.getAnnotationMetadata());
    }

    @Override
    public String getClassName() {
        return classElement.getName();
    }

    @Override
    public boolean isInterface() {
        return classElement.isInterface();
    }

    @Override
    public boolean isAnnotation() {
        return classElement.isAssignable(Annotation.class);
    }

    @Override
    public boolean isAbstract() {
        return classElement.isAbstract();
    }

    @Override
    public boolean isFinal() {
        return classElement.isFinal();
    }

    @Override
    public boolean isIndependent() {
        return !classElement.getEnclosingType().isPresent();
    }

    @Override
    public String getEnclosingClassName() {
        return classElement.getEnclosingType().map(ClassElement::getName).orElse(null);
    }

    @Override
    public String getSuperClassName() {
        return classElement.getSuperType().map(ClassElement::getName).orElse(null);
    }

    @Override
    public String[] getInterfaceNames() {
        return classElement.getInterfaces().stream().map(ClassElement::getName).toArray(String[]::new);
    }

    @Override
    public String[] getMemberClassNames() {
        return classElement.getEnclosedElements(ElementQuery.ALL_INNER_CLASSES)
            .stream().map(ClassElement::getName).toArray(String[]::new);
    }

    private final class MethodMetadataImpl implements MethodMetadata {
        private final MethodElement methodElement;

        private MethodMetadataImpl(MethodElement methodElement) {
            this.methodElement = methodElement;
        }

        @Override
        public String getMethodName() {
            return methodElement.getName();
        }

        @Override
        public String getDeclaringClassName() {
            return methodElement.getDeclaringType().getName();
        }

        @Override
        public String getReturnTypeName() {
            return methodElement.getGenericReturnType().getName();
        }

        @Override
        public boolean isAbstract() {
            return methodElement.isAbstract();
        }

        @Override
        public boolean isStatic() {
            return methodElement.isStatic();
        }

        @Override
        public boolean isFinal() {
            return methodElement.isFinal();
        }

        @Override
        public boolean isOverridable() {
            return !isAbstract() && !methodElement.isPrivate();
        }

        @Override
        public MergedAnnotations getAnnotations() {
            return new MicronautMergedAnnotations(methodElement.getAnnotationMetadata());
        }
    }
}
