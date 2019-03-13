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

import io.micronaut.context.annotation.EachProperty
import io.micronaut.scheduling.executor.ExecutorConfiguration
import io.micronaut.spring.context.MicronautApplicationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import spock.lang.Specification

import javax.inject.Named
import javax.inject.Singleton

class QualifierSpec extends Specification {

    void "test inject with qualifier"() {
        given:
        ApplicationContext ctx = new MicronautApplicationContext(
                io.micronaut.context.ApplicationContext.build()
                    .properties(
                        'foo.bar.one.name':'one',
                        'foo.bar.two.name':'two'
                )
        )
        ctx.start()

        expect:
        ctx.getBean(FooService).foo instanceof Bar1
        ctx.getBeanNamesForType(Foo).size() == 2
        ctx.beanFactory.getSingleton(ctx.getBeanNamesForType(Foo).first())
        ctx.getBeanNamesForType(FooBarProperties).size() == 2
        ctx.beanFactory.getSingleton(ctx.getBeanNamesForType(FooBarProperties).first())
        ctx.beanFactory.getBeanNamesForType(ExecutorConfiguration).size() == 2
        ctx.beanFactory.getSingleton(ctx.beanFactory.getBeanNamesForType(ExecutorConfiguration).first())

        cleanup:
        ctx.close()
    }


    @Singleton
    static class FooService {
        @Autowired
        @Qualifier('bar1')
        Foo foo
    }

    static interface Foo {

    }

    @Singleton
    @Named("bar1")
    static class Bar1 implements Foo {

    }

    @Singleton
    @Named("bar2")
    static class Bar2 implements Foo {

    }

    @EachProperty('foo.bar')
    static class FooBarProperties {
        String name
    }
}
