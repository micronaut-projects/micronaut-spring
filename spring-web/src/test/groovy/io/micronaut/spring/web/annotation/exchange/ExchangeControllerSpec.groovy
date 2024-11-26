/*
 * Copyright 2017-2024 original authors
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
package io.micronaut.spring.web.annotation.exchange

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.spring.web.annotation.Greeting
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.validation.ConstraintViolationException
import spock.lang.Specification

@MicronautTest
class ExchangeControllerSpec extends Specification {

    @Inject
    ExchangeGreetingClient greetingClient

    void "test request controller"() {
        expect:
        greetingClient.home().contains("Welcome to Micronaut for Spring")
        greetingClient.greeting("Fred").content == 'Hello, Fred!'
        greetingClient.greetingNested("Fred").content == 'Hello Nested, Fred!'
        greetingClient.greeting(null).content == 'Hello, World!'
        greetingClient.greetingByPost(new Greeting(1, "Fred")).content == 'Hello, Fred!'
        greetingClient.greetingWithStatus("Fred")
    }

    void "test delete and response entity"() {
        when:
        HttpResponse<?> response = greetingClient.deleteGreeting()

        then:
        response.status() == HttpStatus.NO_CONTENT
        response.header("Foo") == "Bar"
        !response.header("myHeader")

        when:
        var myHeaderValue = "myHeaderValue"
        response = greetingClient.deleteGreeting(myHeaderValue)

        then:
        response.status() == HttpStatus.NO_CONTENT
        response.header("Foo") == "Bar"
        response.header("myHeader") == myHeaderValue
    }

    void "test request controller validation"() {

        when:
        greetingClient.greeting("123").content == 'Hello, Fred!'

        then:
        def e = thrown(ConstraintViolationException)
        e.message.contains('greeting.name: must match "\\D+"')
    }

    void "test ServerHttpRequest argument"() {

        when:
        Greeting greeting = greetingClient.requestTest(new Greeting(1, "Fred"))

        then:
        greeting != null
    }

    void "test optional pathVar"() {

        when:
        def responseForNull = greetingClient.withOptVar(null)

        then:
        responseForNull == 'optVar is null!'

        when:
        def response = greetingClient.withOptVar("This is path var")

        then:
        response == 'Hello, This is path var!'
    }
}
