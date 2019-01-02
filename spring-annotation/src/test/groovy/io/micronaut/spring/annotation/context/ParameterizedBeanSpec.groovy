package io.micronaut.spring.annotation.context

import io.micronaut.context.annotation.Parameter
import io.micronaut.spring.context.MicronautApplicationContext
import org.springframework.context.ApplicationContext
import spock.lang.Specification

import javax.inject.Singleton

class ParameterizedBeanSpec extends Specification {

    void "test that parameterized beans are not exposed as beans to Spring"() {
        given:
        ApplicationContext ctx = new MicronautApplicationContext()
        ctx.start()


        expect:
        !ctx.getBeanNamesForType(MyBean)

        cleanup:
        ctx.close()
    }


    @Singleton
    static class MyBean {
        final String param

        MyBean(@Parameter String param) {
            this.param = param
        }
    }
}
