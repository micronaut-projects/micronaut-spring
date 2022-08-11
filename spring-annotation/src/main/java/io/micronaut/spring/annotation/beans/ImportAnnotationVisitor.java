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

import io.micronaut.core.annotation.Internal;
import jakarta.inject.Singleton;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.context.annotation.Scope;

import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.core.annotation.AnnotationClassValue;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.reflect.InstantiationUtils;
import io.micronaut.core.reflect.exception.InstantiationException;
import io.micronaut.core.util.ArrayUtils;
import io.micronaut.inject.ast.ClassElement;
import io.micronaut.inject.ast.Element;
import io.micronaut.inject.ast.ElementQuery;
import io.micronaut.inject.ast.MethodElement;
import io.micronaut.inject.ast.beans.BeanElementBuilder;
import io.micronaut.inject.ast.beans.BeanMethodElement;
import io.micronaut.inject.visitor.TypeElementVisitor;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.runtime.http.scope.RequestScope;
import io.micronaut.spring.beans.ImportedBy;
import io.micronaut.spring.core.type.ClassElementSpringMetadata;

/**
 * Handles the import importDeclaration allowing importing of additional Spring beans into a Micronaut
 application.
 *
 * @author graemerocher
 * @since 4.2.0
 */
@Internal
public final class ImportAnnotationVisitor implements TypeElementVisitor<Object, Object> {

    private static final String IMPORT_ANNOTATION = "io.micronaut.spring.beans.SpringImport";

    @Override
    public void visitClass(ClassElement element, VisitorContext context) {
        Class annType = element.getAnnotationType(IMPORT_ANNOTATION).orElse(null);
        if (annType != null && element.hasStereotype(annType)) {
            List<AnnotationValue<? extends Annotation>> values = element.getAnnotationValuesByType(annType);
            if (!values.isEmpty()) {
                for (AnnotationValue<?> av : values) {
                    AnnotationClassValue<?>[] acv = av.annotationClassValues(AnnotationMetadata.VALUE_MEMBER);
                    for (AnnotationClassValue<?> a : acv) {
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
            handleImportRegistrar(originatingElement, typeToImport, context);
        } else if (typeToImport.hasAnnotation(Configuration.class)) {
            // handle configuration class
            handleConfigurationImport(originatingElement, typeToImport, context);
        } else {
            // handle component
            BeanElementBuilder beanElementBuilder = originatingElement.addAssociatedBean(typeToImport);
            beanElementBuilder.inject();
            handleScopesAndQualifiers(
                originatingElement,
                beanElementBuilder,
                typeToImport
            );
        }
    }

    private void handleImportRegistrar(ClassElement originatingElement, ClassElement typeToImport, VisitorContext context) {
        // add the registrar as a bean
        originatingElement
            .addAssociatedBean(typeToImport)
            .scope(AnnotationValue.builder(Singleton.class).build())
            .annotate(ImportedBy.class, builder -> builder.member(
                AnnotationMetadata.VALUE_MEMBER,
                new AnnotationClassValue<>(originatingElement.getName())
        ));
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
            .onlyInstance();
        ElementQuery<MethodElement> beanMethods = instanceMethods.annotated(ann -> ann.hasDeclaredAnnotation(Bean.class));
        handleScopesAndQualifiers(originatingElement, beanBuilder, typeToImport);
        beanBuilder.produceBeans(beanMethods, childBuilder -> {
            MethodElement me = (MethodElement) childBuilder.getProducingElement();
            handleScopesAndQualifiers(originatingElement, childBuilder, typeToImport);
            AnnotationValue<Bean> av = me.getAnnotation(Bean.class);
            if (av != null) {
                childBuilder.annotate(av);
            }
            AnnotationValue<Role> r = me.getAnnotation(Role.class);
            if (r != null) {
                childBuilder.annotate(r);
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

    private void handleScopesAndQualifiers(Element originatingElement, BeanElementBuilder beanBuilder, ClassElement typeToImport) {
        // store the name of the type that performs the import
        beanBuilder.annotate(ImportedBy.class, builder -> builder.member(
            AnnotationMetadata.VALUE_MEMBER,
            new AnnotationClassValue<>(originatingElement.getName())
        ));
        String scopeName = typeToImport.stringValue(Scope.class).orElse(null);
        if (typeToImport.hasAnnotation(Primary.class)) {
            beanBuilder.annotate(io.micronaut.context.annotation.Primary.class);
        }
        if (scopeName != null) {
            switch (scopeName) {
                case "prototype":
                    beanBuilder.annotate(Prototype.class);
                break;
                case "request":
                    beanBuilder.annotate(RequestScope.class);
                break;
                default:
                    handleDefaultScope(beanBuilder, typeToImport);
            }
        } else {
            handleDefaultScope(beanBuilder, typeToImport);
        }
    }

    private void handleDefaultScope(BeanElementBuilder childBuilder, Element element) {
        if (element.hasAnnotation(Lazy.class)) {
            boolean lazy = element.booleanValue(Lazy.class).orElse(true);
            if (lazy) {
                childBuilder.annotate(Singleton.class);
            } else {
                childBuilder.annotate(Context.class);
            }
        } else {
            childBuilder.annotate(Context.class);
        }
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
                context.warn("Spring ImportSelector [" + importSelectorElement.getName() + "] found in @Import declaration on element [" + originatingElement.getName() + "] was ignored. Ensure that the type is present on the annotation processor classpath. Note that only simple ImportSelector implementations that do no implement Aware interfaces (which cannot run at build time) are supported.", originatingElement);
            }
        } catch (InstantiationException e) {
            context.fail("Spring ImportSelector [" + importSelectorElement.getName() + "] found in @Import declaration on element [" + originatingElement.getName() + "] must be placed on the annotation processor classpath: " + e.getMessage(), originatingElement);
        } catch (Exception e) {
            context.fail("Spring ImportSelector [" + importSelectorElement.getName() + "] found in @Import declaration on element [" + originatingElement.getName() + "] failed to import: " + e.getMessage(), originatingElement);
        }
    }
}
