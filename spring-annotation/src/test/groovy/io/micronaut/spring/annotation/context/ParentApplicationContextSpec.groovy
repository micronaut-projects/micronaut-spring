package io.micronaut.spring.annotation.context

import io.micronaut.context.annotation.Factory
import io.micronaut.spring.context.MicronautApplicationContext
import org.springframework.beans.factory.support.AbstractBeanDefinition
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import spock.lang.Specification

class ParentApplicationContextSpec extends Specification {

    void "test autowire by name beans are able to find beans in parent"() {
        given:
        def parent = new MicronautApplicationContext()
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

        expect:
        child.getBean("child", ChildBean).myParentBean
        child.getBean("child").myParentBean

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
