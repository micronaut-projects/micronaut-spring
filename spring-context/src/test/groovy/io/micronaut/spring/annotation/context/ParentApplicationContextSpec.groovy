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

import io.micronaut.context.annotation.Factory
import io.micronaut.spring.context.MicronautApplicationContext
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.support.AbstractBeanDefinition
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import spock.lang.Specification

class ParentApplicationContextSpec extends Specification {

    void "test autowire by name beans are able to find beans in parent"() {
        given:
        def parent = new MicronautApplicationContext()

        when:
        parent.start()
        def child = new AnnotationConfigApplicationContext()
        child.setParent(parent)
        def definition = new RootBeanDefinition(ChildBean)
        definition.setAutowireCandidate(true)
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME)
        child.registerBeanDefinition(
                "child",
                definition

        )
        child.refresh()

        then:
        child.getBean("child", ChildBean).myParentBean
        child.getBean("child").myParentBean

        when:"Accessing a bean that is not there"
        child.getBean("notthere")

        then:"A no such bean definition exception is thrown"
        def e = thrown(NoSuchBeanDefinitionException)
        e.message == 'No bean named \'notthere\' available'

        when:"Access via the parent"
        child.parent.getBean("notthere")

        then:"A no such bean definition exception is thrown"
        e = thrown(NoSuchBeanDefinitionException)
        e.message == 'No bean named \'notthere\' available'
    }

    void "test autowire by type beans are able to find beans in parent"() {
        given:
        def parent = new MicronautApplicationContext()
        parent.start()
        def child = new AnnotationConfigApplicationContext()
        child.setParent(parent)
        def definition = new RootBeanDefinition(ChildBean)
        definition.setAutowireCandidate(true)
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
        child.registerBeanDefinition(
                "child",
                definition

        )
        child.refresh()

        expect:
        child.getBean("child", ChildBean).myParentBean
        child.getBean("child").myParentBean
    }

    @Factory
    static class MyParentFactory {
        @Bean("myParentBean")
        MyParentBean myParentBean() {
            new MyParentBean()
        }
    }

    static class ChildBean {
        MyParentBean myParentBean
    }

    static class MyParentBean {

    }
}
