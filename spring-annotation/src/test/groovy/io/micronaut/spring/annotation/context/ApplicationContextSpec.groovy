package io.micronaut.spring.annotation.context

import io.micronaut.spring.context.ManagedApplicationContext
import io.micronaut.spring.context.MicronautApplicationContext
import org.springframework.beans.factory.BeanFactoryUtils
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Service
import spock.lang.Specification

class ApplicationContextSpec extends Specification {

    void "test spring application context implementation"() {
        when:
        ManagedApplicationContext context = new MicronautApplicationContext()

        then:
        !context.isRunning()

        when:
        context.start()

        then:
        context.isRunning()

        when:
        def names = context.getBeanNamesForType(MyNamedService)

        then:
        names
        context.getBean(names[0]) instanceof MyNamedService
        context.getBean(names[0]) == context.getBean(MyNamedService)
        context.findAnnotationOnBean(names[0], Service)
        context.getBeansWithAnnotation(Service).size() == 1
        context.getBeanProvider(MyNamedService).ifAvailable == context.getBean(MyNamedService)
        context.getBeansOfType(MyNamedService).size() == 1

        when:
        context.stop()

        then:
        !context.isRunning()
    }

    void "test use as parent context"() {
        when:
        ManagedApplicationContext parentContext = new MicronautApplicationContext(
                io.micronaut.context.ApplicationContext.build()
                    .properties("foo.bar":'somevalue')
        )
        parentContext.start()

        ApplicationContext context = new GenericApplicationContext(parentContext)
        context.refresh()

        then:
        context.getBean(MyNamedService)
        context.getBeanProvider(MyNamedService).ifAvailable == context.getBean(MyNamedService)
        BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, MyNamedService).size() == 1

    }
}
