/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.spring.boot

import io.micronaut.context.annotation.Property
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import some.other.pkg.FeaturesClient
import some.other.pkg.FeaturesEndpoint
import spock.lang.Specification

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
