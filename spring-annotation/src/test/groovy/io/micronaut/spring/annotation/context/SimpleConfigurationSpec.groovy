package io.micronaut.spring.annotation.context

import io.micronaut.aop.Intercepted
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.spring.tx.annotation.Transactional
import io.micronaut.test.annotation.MicronautTest
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.context.ApplicationEvent
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.transaction.annotation.Propagation
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import javax.inject.Inject

@MicronautTest
@Property(name = "foo.bar", value = "myvalue")
@Property(name = "some.int", value = "10")
class SimpleConfigurationSpec extends Specification {

    @Inject
    ApplicationContext applicationContext

    @Inject
    ListableBeanFactory beanFactory

    @Inject
    Environment env

    @Inject
    MyComponent myComponent

    void "test configuration maps beans"() {

        expect:
        applicationContext.getBean(MyConfiguration).myBean() == applicationContext.getBean(MyConfiguration).myBean()
        applicationContext.getBean(MyConfiguration.MyBean) == applicationContext.getBean(MyConfiguration).myBean()
        applicationContext.getBean(MyConfiguration.MyBean).name == 'default'
        applicationContext.getBean(MyConfiguration.MyBean, Qualifiers.byName("another")).name == 'another'
        applicationContext.getBeansOfType(MyConfiguration.MyBean).size() == 2
        applicationContext.getBean(MyComponent) == applicationContext.getBean(MyComponent)
        applicationContext.getBean(MyComponent).value == "myvalue"
        applicationContext.getBean(MyComponent).myNamedService != null
        applicationContext.getBean(MyNamedService) == applicationContext.getBean(MyComponent).myNamedService
        applicationContext.getBean(MyNamedService).lastEvent
        applicationContext.getBean(MyNamedService, Qualifiers.byName("myname"))
        applicationContext.getBean(MyTransactionalService.class) instanceof Intercepted
        applicationContext.getBeanDefinition(MyTransactionalService).executableMethods.size() == 1
        applicationContext.getBeanDefinition(MyTransactionalService).executableMethods[0].getAnnotation(Transactional)
        applicationContext.getBeanDefinition(MyTransactionalService).executableMethods[0].synthesize(Transactional).readOnly()
        applicationContext.getBeanDefinition(MyTransactionalService).executableMethods[0].synthesize(Transactional).propagation() == Propagation.REQUIRES_NEW
    }

    void "test aware and lifecyle interfaces"() {
        expect:
        myComponent.environment
        myComponent.beanFactory
        myComponent.initialized
    }

    void "test the environment"() {
        expect:
        env.getActiveProfiles().size() > 0
        env.getActiveProfiles().contains io.micronaut.context.env.Environment.TEST
        env.getProperty("foo.bar") == 'myvalue'
        env.getRequiredProperty("foo.bar") == 'myvalue'
        env.getProperty("some.int", Integer) == 10
        env.getRequiredProperty("some.int", Integer) == 10
        env.resolveRequiredPlaceholders("hello \${foo.bar}") == 'hello myvalue'
    }

    void "test the bean factory"() {
        expect:
        beanFactory.getBean(MyConfiguration) == applicationContext.getBean(MyConfiguration)
        beanFactory.getBeansOfType(MyConfiguration.MyBean).size() == 2
    }

    void "test jobs are executed"() {
        given:
        PollingConditions conditions = new PollingConditions(timeout: 3)

        expect:
        conditions.eventually {
            applicationContext.getBean(MyJob).executed
        }
    }

     void "test that events are published"() {
         when:
         applicationContext.publishEvent(new ApplicationEvent(this) {
             @Override
             Object getSource() {
                 return super.getSource()
             }
         })

         then:
         applicationContext.getBean(MyComponent).lastEvent != null
     }

    void "test start spring"() {
        given:
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext()
        applicationContext.environment.propertySources.addFirst(new MapPropertySource("test", [
                'foo.bar':'myvalue'
        ]))
        applicationContext.scan("io.micronaut.spring.annotation.context")
        applicationContext.refresh()

        expect:
        applicationContext.getBean(MyConfiguration.MyBean).name == 'default'
        applicationContext.getBean("another", MyConfiguration.MyBean).name == 'another'
        applicationContext.getBeansOfType(MyConfiguration.MyBean).size() == 2
        applicationContext.getBean(MyComponent) == applicationContext.getBean(MyComponent)
        applicationContext.getBean(MyComponent).value == "myvalue"
        applicationContext.getBean(MyComponent).myNamedService != null
        applicationContext.getBean(MyNamedService) == applicationContext.getBean(MyComponent).myNamedService
        applicationContext.getBean("myname", MyNamedService)

        cleanup:
        applicationContext.close()
    }
}
