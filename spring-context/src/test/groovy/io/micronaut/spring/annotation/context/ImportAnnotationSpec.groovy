package io.micronaut.spring.annotation.context

import io.micronaut.annotation.processing.test.AbstractTypeElementSpec
import io.micronaut.spring.beans.SpringImport
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import;
/**
 *
 * @author graemerocher
 */
class ImportAnnotationSpec extends AbstractTypeElementSpec {

    void "test import annotation repetition"() {
        given:
        def context = buildContext('''
package importtest;

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

@Import(OneConfiguration.class)
@interface ImportOne {}

@Import(TwoConfiguration.class)
@interface ImportTwo {}


''')
        def fooDefinition = getBeanDefinition(context, 'importtest.Foo')
        def oneDefinition = getBeanDefinition(context, One.name)
        def twoDefinition = getBeanDefinition(context, Two.name)
        def foo = getBean(context, 'importtest.Foo')

        expect:
        foo
        foo.one
        foo.two
        context != null
        fooDefinition != null
        fooDefinition.getAnnotationMetadata().getAnnotationValuesByType(SpringImport).size() == 2

        cleanup:
        context.close()
    }
}

@Configuration
class OneConfiguration {
    @Bean
    One one() {
        return new One();
    }
}

@Configuration
class TwoConfiguration {
    @Bean
    Two two() {
        return new Two();
    }    
}

class One {}
class Two{}
