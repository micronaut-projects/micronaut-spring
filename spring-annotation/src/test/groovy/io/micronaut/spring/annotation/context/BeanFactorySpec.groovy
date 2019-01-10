package io.micronaut.spring.annotation.context

import io.micronaut.context.env.Environment
import io.micronaut.spring.context.MicronautApplicationContext
import io.micronaut.spring.context.factory.MicronautBeanFactory
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import spock.lang.Specification

class BeanFactorySpec extends Specification {

    void "test register named singleton"() {

        given:
        def context = new MicronautApplicationContext()

        when:
        context.start()
        ConfigurableBeanFactory beanFactory = context.beanFactory

        then:
        beanFactory.beanDefinitionNames.length > 0

        when:
        beanFactory.registerSingleton("foo", new Foo())

        then:
        beanFactory.containsSingleton("foo")
        beanFactory.containsBean("foo")
        beanFactory.isSingleton("foo")
        beanFactory.getBean("foo") instanceof Foo
        beanFactory.getBeanNamesForType(Foo) == ['foo'] as String[]
        beanFactory.getBeansOfType(Foo).size() == 1
        beanFactory.getType("foo") == Foo
        beanFactory.getBean(Environment)
        beanFactory.getSingletonCount() == 1
        beanFactory.getSingletonNames() == ['foo'] as String[]
        beanFactory.getSingleton("foo") instanceof Foo

        when:
        beanFactory.registerAlias("foo", "foo2")

        then:
        beanFactory.getAliases("foo").size() == 1
        beanFactory.getBean("foo2")

        cleanup:
        context.close()

    }

    static class Foo {}
}
