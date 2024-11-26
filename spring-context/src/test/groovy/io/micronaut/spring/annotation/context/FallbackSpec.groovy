package io.micronaut.spring.annotation.context

import io.micronaut.context.ApplicationContext
import spock.lang.Specification

class FallbackSpec extends Specification {

    void "test fallback works"() {
        when:
        def context = ApplicationContext.run()

        then:
        context.getBean(MyInterface) instanceof MyFallback
    }
}
