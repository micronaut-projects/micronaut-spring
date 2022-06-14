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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.micronaut.core.annotation.AnnotationClassValue;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.AnnotationUtil;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.ast.ClassElement;
import io.micronaut.inject.ast.ElementQuery;
import io.micronaut.inject.ast.MethodElement;
import io.micronaut.inject.ast.beans.BeanElementBuilder;
import io.micronaut.inject.ast.beans.BeanMethodElement;
import io.micronaut.inject.visitor.TypeElementVisitor;
import io.micronaut.inject.visitor.VisitorContext;

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
                    AnnotationClassValue<?> acv = av.annotationClassValue(AnnotationMetadata.VALUE_MEMBER).orElse(null);
                    String className = acv.getName();
                    context.getClassElement(className).ifPresent((typeToImport) -> {
                        if (typeToImport.isAssignable(ImportSelector.class)) {
                            // handle import selector
                        } else if (typeToImport.isAssignable(ImportBeanDefinitionRegistrar.class)) {
                            // handle import registrar
                        } else if (typeToImport.hasAnnotation(Configuration.class)) {
                            // handle configuration class
                            handleConfigurationImport(element, typeToImport, context);
                        } else if (typeToImport.hasStereotype(Component.class)) {
                            // handle component
                        } 
                    });
                }
            }
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
        BeanElementBuilder beanBuilder = originatingElement.addAssociatedBean(typeToImport);
        ElementQuery<MethodElement> instanceMethods = ElementQuery.ALL_METHODS
            .onlyInstance()
            .filter(m -> !m.hasParameters());
        ElementQuery<MethodElement> beanMethods = instanceMethods.annotated(ann -> ann.hasDeclaredAnnotation(Bean.class));
        beanBuilder.produceBeans(beanMethods, (childBuilder) -> {
            MethodElement me = (MethodElement) childBuilder.getProducingElement();
            String scopeName = me.stringValue(Scope.class).orElse(null);
            if (scopeName != null) {
                // TODO: handle scopes
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
}
