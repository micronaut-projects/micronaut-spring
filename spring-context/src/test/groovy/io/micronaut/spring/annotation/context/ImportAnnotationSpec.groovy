package io.micronaut.spring.annotation.context

import io.micronaut.annotation.processing.test.AbstractTypeElementSpec
import io.micronaut.spring.beans.SpringImport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

/**
 *
 * @author graemerocher
 */
class ImportAnnotationSpec extends AbstractTypeElementSpec {

    void "test import annotation repetition"() {
        given:
        def context = buildContext('''
package importtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import io.micronaut.spring.annotation.context.*;
import jakarta.inject.*;

@ImportOne
@ImportTwo
@Component
class Foo {
    @Inject
    One one;
    @Inject
    Two two;

}

@Import({OneConfiguration.class, BeanA.class})
@interface ImportOne {}

@Import(TwoConfiguration.class)
@interface ImportTwo {}


''')
        when:
        def fooDefinition = getBeanDefinition(context, 'importtest.Foo')
        def oneDefinition = getBeanDefinition(context, One.name)
        def twoDefinition = getBeanDefinition(context, Two.name)
        def foo = getBean(context, 'importtest.Foo')

        then:
        foo
        foo.one
        foo.two
        foo.two.initCalled
        !foo.two.destroyCalled
        context != null
        fooDefinition != null
        fooDefinition.getAnnotationMetadata().getAnnotationValuesByType(SpringImport).size() == 2

        when:
        context.close()

        then:
        foo.two.destroyCalled
    }

    
}

@Configuration
class OneConfiguration {
    @Autowired
    BeanA beanA;

    @Bean
    One one() {
        assert beanA != null
        return new One();
    }
}

@Configuration
class TwoConfiguration {
    @Bean(initMethod="init", destroyMethod="destroy")
    Two two() {
        return new Two();
    }    
}

class One {}
class Two {
    boolean initCalled = false
    boolean destroyCalled = false
    void init() {
        initCalled = true
    }

    void destroy() {
        destroyCalled = true
    }
}


@Component
class BeanA {}