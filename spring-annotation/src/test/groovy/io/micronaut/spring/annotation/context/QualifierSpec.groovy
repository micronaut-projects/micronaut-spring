package io.micronaut.spring.annotation.context

import io.micronaut.context.annotation.EachProperty
import io.micronaut.spring.context.MicronautApplicationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import spock.lang.Specification

import javax.inject.Named
import javax.inject.Singleton

class QualifierSpec extends Specification {

    void "test inject with qualifier"() {
        given:
        ApplicationContext ctx = new MicronautApplicationContext(
                io.micronaut.context.ApplicationContext.build()
                    .properties(
                        'foo.bar.one.name':'one',
                        'foo.bar.two.name':'two'
                )
        )
        ctx.start()

        expect:
        ctx.getBean(FooService).foo instanceof Bar1
        ctx.getBeanNamesForType(Foo).size() == 2
        ctx.beanFactory.getSingleton(ctx.getBeanNamesForType(Foo).first())
        ctx.getBeanNamesForType(FooBarProperties).size() == 2
//        ctx.getBean(ctx.getBeanNamesForType(FooBarProperties).first(), FooBarProperties)
        ctx.beanFactory.getSingleton(ctx.getBeanNamesForType(FooBarProperties).first())

        cleanup:
        ctx.close()
    }


    @Singleton
    static class FooService {
        @Autowired
        @Qualifier('bar1')
        Foo foo
    }

    static interface Foo {

    }

    @Singleton
    @Named("bar1")
    static class Bar1 implements Foo {

    }

    @Singleton
    @Named("bar2")
    static class Bar2 implements Foo {

    }

    @EachProperty('foo.bar')
    static class FooBarProperties {
        String name
    }
}
