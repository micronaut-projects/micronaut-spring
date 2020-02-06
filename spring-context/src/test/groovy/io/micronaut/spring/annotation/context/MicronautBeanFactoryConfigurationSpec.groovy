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
package io.micronaut.spring.annotation.context

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.ApplicationContext
import io.micronaut.spring.context.MicronautApplicationContext
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import spock.lang.Specification

class MicronautBeanFactoryConfigurationSpec extends Specification {

    void "test exclude bean types"() {
        given:
        MicronautApplicationContext applicationContext = new MicronautApplicationContext(
            ApplicationContext.build()
                .properties("micronaut.spring.context.bean-excludes": [ObjectMapper])
        )
        applicationContext.start()

        when:
        applicationContext.getBean(ObjectMapper)

        then:
        thrown(NoSuchBeanDefinitionException)

        and:
        !applicationContext.getBeanNamesForType(ObjectMapper)
        !applicationContext.getBeansOfType(ObjectMapper)
    }

    void "test exclude bean types - no excludes"() {
        given:
        MicronautApplicationContext applicationContext = new MicronautApplicationContext(
        )
        applicationContext.start()

        expect:
        applicationContext.getBean(ObjectMapper)
        applicationContext.getBeanNamesForType(ObjectMapper)
    }
}
