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
