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
package io.micronaut.spring.beans.aware;

import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.spring.beans.ImportedBy;
import io.micronaut.spring.core.type.BeanDefinitionSpringMetadata;
import jakarta.inject.Singleton;
import org.springframework.context.annotation.ImportAware;

@Singleton
public class ImportAwareListener implements BeanCreatedEventListener<ImportAware> {
    @Override
    public ImportAware onCreated(BeanCreatedEvent<ImportAware> event) {
        ImportAware importAware = event.getBean();
        BeanDefinition<ImportAware> beanDefinition = event.getBeanDefinition();
        Class<?> importedBy = beanDefinition.getAnnotationMetadata().classValue(ImportedBy.class).orElse(null);
        if (importedBy != null) {
            event.getSource().findBeanDefinition(importedBy).ifPresent(importedDef ->
                importAware.setImportMetadata(new BeanDefinitionSpringMetadata(importedDef))
            );
        }
        return importAware;
    }
}
