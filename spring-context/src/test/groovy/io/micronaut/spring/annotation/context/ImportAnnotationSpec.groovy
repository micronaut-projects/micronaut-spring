package io.micronaut.spring.annotation.context

import io.micronaut.annotation.processing.test.AbstractTypeElementSpec
import io.micronaut.inject.BeanDefinitionReference
import io.micronaut.spring.beans.ObjectProviderBeanDefinition
import io.micronaut.spring.beans.SpringImport
import org.springframework.aop.framework.Advised
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.ImportSelector
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.env.Environment
import org.springframework.core.type.AnnotationMetadata
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor
import org.springframework.stereotype.Component

/**
 *
 * @author graemerocher
 */
class ImportAnnotationSpec extends AbstractTypeElementSpec {

    void "test @EnableAsync"() {
        given:
        def context = buildContext("enabeasync.Application",'''
package enableasync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import io.micronaut.spring.annotation.context.*;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

@EnableAsync
class Application {
}

@Component
class Job {
    @Async
    public void doWork() {
        System.out.println(Thread.currentThread().getName());
    }
}
''', true)
        def job = getBean(context, 'enableasync.Job')

        expect:
        context.containsBean(AsyncAnnotationBeanPostProcessor)
        context.getBean(AsyncAnnotationBeanPostProcessor)
        job != null
        job instanceof Advised

        cleanup:
        context.close()
    }

    void "test build time import selector"() {
        given:
        def context = buildContext('''
package importselector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.stereotype.Component;
import io.micronaut.spring.annotation.context.*;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

@Component
@Speed(fast = true)
class Car {
    @Autowired
    Engine engine;
}
''')
        def car = getBean(context, 'importselector.Car')

        expect:
        car.engine.getClass().name.contains("Fast")

        cleanup:
        context.close()
    }

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

@ImportOne
@ImportTwo
@Component
class Foo {
    @Autowired
    One one;
    @Autowired
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

    @Override
    List<BeanDefinitionReference<?>> getBuiltInBeanReferences() {
        return super.getBuiltInBeanReferences() + [new ObjectProviderBeanDefinition()]
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

interface Engine {}

class FastEngine implements Engine {}

class SlowEngine implements Engine {}

class MySelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        MergedAnnotation<Speed> mergedAnnotation = importingClassMetadata.getAnnotations().get(Speed.class);
        if(mergedAnnotation.getBoolean("fast")) {
            return new String[] { FastEngine.class.getName() };
        } else {
            return new String[] { SlowEngine.class.getName() };
        }
    }
}

@Import(MySelector.class)
@interface Speed {
    boolean fast();
}
