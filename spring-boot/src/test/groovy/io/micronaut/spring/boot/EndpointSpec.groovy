package io.micronaut.spring.boot

import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest
import some.other.pkg.FeaturesClient
import some.other.pkg.FeaturesEndpoint
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest
@Property(name = "endpoints.features.sensitive", value = "false")
class EndpointSpec extends Specification {

    @Inject
    FeaturesClient client

    void "test endpoint"() {

        when:
        def features = client.features()

        then:
        features
        features.default.enabled

        when:
        def status = client.saveFeature("stuff", new FeaturesEndpoint.Feature())
        features = client.features()


        then:
        status
        features.size() == 2
        features.stuff.enabled == false
        client.features("stuff") != null

        when:
        client.deleteFeature("stuff")
        features = client.features()

        then:
        features.size() == 1
    }
}
