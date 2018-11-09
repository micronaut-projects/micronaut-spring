package io.micronaut.spring.boot.annotation.condition

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest
@Property(name = "some.prop", value = "good")
class ConditionalOnSpec extends Specification {

    @Inject
    ApplicationContext applicationContext

    void "test conditional on bean"() {
        expect:
        !applicationContext.containsBean(ConditionalOnBeanComponent)
        applicationContext.containsBean(ConditionalOnBeanComponent2)
    }

    void "test conditional on missing bean"() {
        expect:
        applicationContext.containsBean(ConditionalOnMissingBeanComponent)
        !applicationContext.containsBean(ConditionalOnMissingBeanComponent2)
    }

     void "test conditional on property"() {
         expect:
         applicationContext.containsBean(ConditionalOnPropertyBean)
         !applicationContext.containsBean(ConditionalOnPropertyBean2)
         !applicationContext.containsBean(ConditionalOnPropertyBean3)
     }

    void "test conditional on web app"() {
        expect:
        applicationContext.containsBean(ConditionalOnNotWebApplicationComponent)
        !applicationContext.containsBean(ConditionalOnWebApplicationComponent)
    }

    void "test conditional on single candidate"() {
        expect:
        applicationContext.containsBean(ConditionalOnSingleCandidateComponent)
        !applicationContext.containsBean(ConditionalOnSingleCandidateComponent2)
    }
}
