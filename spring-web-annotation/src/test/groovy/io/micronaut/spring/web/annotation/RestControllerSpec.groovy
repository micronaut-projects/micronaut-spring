package io.micronaut.spring.web.annotation

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

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
        e.message.contains('name: must match "\\D+"')
    }

    void "test ServerHttpRequest argument"() {

        when:
        Greeting greeting = greetingClient.requestTest(new Greeting(1, "Fred"))

        then:
        greeting != null
    }
}
