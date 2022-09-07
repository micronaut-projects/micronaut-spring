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
package io.micronaut.spring.context.factory;

import java.util.List;

import io.micronaut.context.BeanRegistration;
import io.micronaut.context.annotation.Context;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Order;
import io.micronaut.core.order.Ordered;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.spring.beans.ImportedBy;
import io.micronaut.spring.core.type.BeanDefinitionSpringMetadata;
import jakarta.annotation.PostConstruct;
import org.springframework.aop.framework.autoproxy.InfrastructureAdvisorAutoProxyCreator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;

/**
 * Runs any configured ImportBeanDefinitionRegistrar instances.
 *
 * @since 4.3.0
 * @author graemerocher
 */
@Context
@Order(Ordered.HIGHEST_PRECEDENCE)
@Introspected(classes = InfrastructureAdvisorAutoProxyCreator.class)
@Internal
final class ImportBeanRegistrarProcessor {
    private final List<BeanRegistration<ImportBeanDefinitionRegistrar>> registrars;
    private final MicronautBeanFactory beanFactory;

    public ImportBeanRegistrarProcessor(
        List<BeanRegistration<ImportBeanDefinitionRegistrar>> registrars, MicronautBeanFactory beanFactory) {
        this.registrars = registrars;
        this.beanFactory = beanFactory;
    }

    @PostConstruct
    void runRegistrars() {
        for (BeanRegistration<ImportBeanDefinitionRegistrar> registrar : registrars) {
            BeanDefinition<ImportBeanDefinitionRegistrar> beanDefinition = registrar.getBeanDefinition();
            Class<?> importingType = beanDefinition.classValue(ImportedBy.class).orElse(null);
            BeanDefinitionSpringMetadata springMetadata;
            if (importingType != null) {
                springMetadata = new BeanDefinitionSpringMetadata(beanFactory.getBeanContext().getBeanDefinition(importingType));
            } else {
                springMetadata = new BeanDefinitionSpringMetadata(beanDefinition);
            }
            registrar.bean().registerBeanDefinitions(
                springMetadata,
                beanFactory,
                AnnotationBeanNameGenerator.INSTANCE
            );
        }
    }
}
