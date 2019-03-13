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
