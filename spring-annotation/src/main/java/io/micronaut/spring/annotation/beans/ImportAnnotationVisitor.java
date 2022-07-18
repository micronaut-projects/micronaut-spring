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
package io.micronaut.spring.annotation.beans;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.micronaut.core.util.ArrayUtils;
import io.micronaut.spring.core.type.ClassElementSpringMetadata;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.micronaut.context.annotation.Prototype;
import io.micronaut.core.annotation.AnnotationClassValue;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.AnnotationUtil;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.reflect.InstantiationUtils;
import io.micronaut.core.reflect.exception.InstantiationException;
import io.micronaut.inject.ast.ClassElement;
import io.micronaut.inject.ast.ElementQuery;
import io.micronaut.inject.ast.MethodElement;
import io.micronaut.inject.ast.beans.BeanElementBuilder;
import io.micronaut.inject.ast.beans.BeanMethodElement;
import io.micronaut.inject.visitor.TypeElementVisitor;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.runtime.http.scope.RequestScope;

/**
 * Handles the import importDeclaration allowing importing of additional Spring beans into a Micronaut
 application.
 *
 * @author graemerocher
 * @since 4.2.0
 */
public class ImportAnnotationVisitor implements TypeElementVisitor<Object, Object>{

    private static final String IMPORT_ANNOTATION = "io.micronaut.spring.beans.SpringImport";

    @Override
    public void visitClass(ClassElement element, VisitorContext context) {
        Class annType = element.getAnnotationType(IMPORT_ANNOTATION).orElse(null);
        if (annType != null) {
            List<AnnotationValue<? extends Annotation>> values = element.getAnnotationValuesByType(annType);
            if (!values.isEmpty()) {
                for (AnnotationValue<?> av : values) {
                    AnnotationClassValue<?>[] acv = av.annotationClassValues(AnnotationMetadata.VALUE_MEMBER);
                    for (int idx = 0; idx < acv.length; idx++) {
                        AnnotationClassValue<?> a = acv[idx];
                        String className = a.getName();
                        context.getClassElement(className).ifPresent(typeToImport -> {
                            handleImport(element, context, typeToImport);
                        });
                    }
                }
            }
        }
    }

    private void handleImport(ClassElement originatingElement, VisitorContext context, ClassElement typeToImport) {
        if (typeToImport.isAssignable(ImportSelector.class)) {
            // handle import selector
            importSelector(originatingElement, typeToImport, context);
        } else if (typeToImport.isAssignable(ImportBeanDefinitionRegistrar.class)) {
            // handle import registrar
        } else if (typeToImport.hasAnnotation(Configuration.class)) {
            // handle configuration class
            handleConfigurationImport(originatingElement, typeToImport, context);
        } else {
            // handle component
            originatingElement.addAssociatedBean(typeToImport).inject();
        }
    }

    @Override
    public VisitorKind getVisitorKind() {
        return VisitorKind.ISOLATING;
    }

    @Override
    public Set<String> getSupportedAnnotationNames() {
        return Collections.singleton(IMPORT_ANNOTATION);
    }

    private void handleConfigurationImport(
        ClassElement originatingElement,
        ClassElement typeToImport,
        VisitorContext context) {
        // TODO: In Micronaut 3.5.2 interception was added so need to proxy these
        BeanElementBuilder beanBuilder = originatingElement
            .addAssociatedBean(typeToImport)
            .inject();
        ElementQuery<MethodElement> instanceMethods = ElementQuery.ALL_METHODS
            .onlyInstance()
            .filter(m -> !m.hasParameters());
        ElementQuery<MethodElement> beanMethods = instanceMethods.annotated(ann -> ann.hasDeclaredAnnotation(Bean.class));
        beanBuilder.produceBeans(beanMethods, (childBuilder) -> {
            MethodElement me = (MethodElement) childBuilder.getProducingElement();
            String scopeName = me.stringValue(Scope.class).orElse(null);
            if (scopeName != null) {
                switch(scopeName) {
                    case "prototype":
                        childBuilder.annotate(Prototype.class);
                    break;
                    case "request":
                        childBuilder.annotate(RequestScope.class);
                    break;
                    default:
                        childBuilder.annotate(AnnotationUtil.SINGLETON);
                }
            } else {
                childBuilder.annotate(AnnotationUtil.SINGLETON);
            }
            String initMethod = me.stringValue(Bean.class, "initMethod").orElse(null);
            String destroyMethod = me.stringValue(Bean.class, "destroyMethod").orElse(null);
            if (initMethod != null) {
                childBuilder.withMethods(
                    instanceMethods.named(n -> n.equals(initMethod)),
                    BeanMethodElement::postConstruct
                );
            }
            if (destroyMethod != null) {
                childBuilder.withMethods(
                    instanceMethods.named(n -> n.equals(destroyMethod)),
                    BeanMethodElement::preDestroy
                );
            }
        });

    }

    private void importSelector(ClassElement originatingElement, ClassElement importSelectorElement, VisitorContext context) {
        try {
            Object selectorObject = null;

            if (!importSelectorElement.isAssignable(BeanClassLoaderAware.class) &&
                !importSelectorElement.isAssignable(BeanFactoryAware.class) &&
                !importSelectorElement.isAssignable(ResourceLoaderAware.class) &&
                !importSelectorElement.isAssignable(EnvironmentAware.class) &&
                !importSelectorElement.isAssignable(DeferredImportSelector.class)) {
                selectorObject = InstantiationUtils.tryInstantiate(importSelectorElement.getName(), getClass().getClassLoader()).orElse(null);
            }
            if (selectorObject instanceof ImportSelector) {
                ImportSelector selector = (ImportSelector) selectorObject;
                String[] importedTypes = selector.selectImports(new ClassElementSpringMetadata(originatingElement));
                if (ArrayUtils.isNotEmpty(importedTypes)) {
                    for (String importedType : importedTypes) {
                        context.getClassElement(importedType).ifPresent(typeToImport -> {
                            handleImport(originatingElement, context, typeToImport);
                        });
                    }
                }
            } else {
                // TODO: defer to runtime
            }
        } catch (InstantiationException e) {
            context.fail("ImportSelector of type [" + importSelectorElement.getName() + "] found in Spring @Import declaration must be placed on the annotation processor classpath: " + e.getMessage(), originatingElement);
        } catch (Exception e) {
            context.fail("ImportSelector of type [" + importSelectorElement.getName() + "] failed to import: " + e.getMessage(), originatingElement);
        }
    }
}
