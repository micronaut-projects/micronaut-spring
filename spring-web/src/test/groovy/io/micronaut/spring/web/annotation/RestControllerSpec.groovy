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
package io.micronaut.spring.web.annotation

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class RestControllerSpec extends Specification {

    @Inject
    GreetingClient greetingClient

    void "test request controller"() {
        expect:
        greetingClient.home().contains("Welcome to Micronaut for Spring")
        greetingClient.greet("Fred").content == 'Hello, Fred!'
        greetingClient.nestedGreet("Fred").content == 'Hello Nested, Fred!'
        greetingClient.greet(null).content == 'Hello, World!'
        greetingClient.greetByPost(new Greeting(1, "Fred")).content == 'Hello, Fred!'
        greetingClient.greetWithStatus("Fred").status() == HttpStatus.CREATED
    }

    void "test delete and response entity"() {
        when:
        HttpResponse<?> response = greetingClient.deletePost()

        then:
        response.status() == HttpStatus.NO_CONTENT
        response.header("Foo") == "Bar"
    }

    void "test request controller validation"() {

        when:
        greetingClient.greet("123").content == 'Hello, Fred!'

        then:
        def e = thrown(HttpClientResponseException)
        e.response.getBody(Map).get()._embedded.errors[0].message.contains('name: must match "\\D+"')
    }

    void "test ServerHttpRequest argument"() {

        when:
        Greeting greeting = greetingClient.requestTest(new Greeting(1, "Fred"))

        then:
        greeting != null
    }
}
