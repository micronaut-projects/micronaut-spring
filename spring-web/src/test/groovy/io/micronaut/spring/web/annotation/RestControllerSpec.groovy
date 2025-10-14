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
package io.micronaut.spring.web.annotation

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.client.multipart.MultipartBody
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
        !response.header("myHeader")

        when:
        var myHeaderValue = "myHeaderValue"
        response = greetingClient.deletePost(myHeaderValue)

        then:
        response.status() == HttpStatus.NO_CONTENT
        response.header("Foo") == "Bar"
        response.header("myHeader") == myHeaderValue
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

    void "test RequestPart argument"() {

        when:
        def json = """{"prop1": "val1", "prop2": 12}"""
        def fileContent = "this is file content"
        def response = greetingClient.multipartRequest(MultipartBody.builder()
                .addPart("json", json)
                .addPart("myFile", "myFileName", fileContent.bytes)
                .build())

        then:
        response == json + '#' + fileContent

        when:
        def notRequiredPart = "this is not required part"
        response = greetingClient.multipartRequest(MultipartBody.builder()
                .addPart("json", json)
                .addPart("notRequiredPart", notRequiredPart)
                .addPart("myFile", "myFileName", fileContent.bytes)
                .build())

        then:
        response == json + '#' + notRequiredPart + '#' + fileContent
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
