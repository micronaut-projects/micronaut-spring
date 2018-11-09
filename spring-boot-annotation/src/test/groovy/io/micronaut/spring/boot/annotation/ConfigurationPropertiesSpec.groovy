package io.micronaut.spring.boot.annotation

import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest
@Property(name='foo.bar.name', value = "myname")
class ConfigurationPropertiesSpec extends Specification {

    @Inject
    MyConfigurationProperties myConfigurationProperties

    void "test configuration properties in Micronaut"() {
        expect:
        myConfigurationProperties.name == 'myname'
    }
}
