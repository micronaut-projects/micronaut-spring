package io.micronaut.spring.boot.annotation

import io.micronaut.annotation.processing.test.AbstractTypeElementSpec
import io.micronaut.inject.BeanDefinitionReference
import io.micronaut.spring.beans.ObjectProviderBeanDefinition

class EntityScanSpec extends AbstractTypeElementSpec {
    void "test @EntityScan"() {
        given:
        def context = buildContext("entityscan.Application",'''
package entityscan;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
@SpringBootApplication
@EntityScan("entityscan")
class Application {
}

''', true)

        expect:
        context != null
    }

    @Override
    List<BeanDefinitionReference<?>> getBuiltInBeanReferences() {
        return super.getBuiltInBeanReferences() + [new ObjectProviderBeanDefinition()]
    }
}
