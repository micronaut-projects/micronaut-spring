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
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import spock.lang.Shared
import spock.lang.Specification

class QualifierSpec extends Specification {

    @Shared
    Map<String, Object> config = [
            'foo.bar.one.name':'one',
            'foo.bar.two.name':'two',
    ]

    private ApplicationContext startContext() {
        ApplicationContext ctx = new MicronautApplicationContext(
                io.micronaut.context.ApplicationContext.builder()
                        .properties(config))
        ctx.start()
        ctx
    }

    void "test inject bean with name qualifier"() {
        given:
        ApplicationContext ctx = startContext()

        when:
        List<String> names = ctx.getBeanNamesForType(Foo)

        then:
        names.size() == 2

        and:
        ctx.getBean(FooService).foo instanceof Bar1

        when:
        ctx.beanFactory.getSingleton(names.first())

        then:
        noExceptionThrown()

        cleanup:
        ctx.close()
    }

    void "test inject ExecutorConfiguration"() {
        given:
        ApplicationContext ctx = startContext()

        when:
        List<String> names = ctx.getBeanNamesForType(ExecutorConfiguration)

        then:
        names.size() == 2

        when:
        ctx.beanFactory.getSingleton(names.first())

        then:
        noExceptionThrown()

        cleanup:
        ctx.close()
    }

    void "test inject ConfigurationProperty bean"() {
        given:
        ApplicationContext ctx = startContext()

        when:
        List<String> names = ctx.getBeanNamesForType(FooBarProperties)

        then:
        names.size() == 2

        when:
        ((MicronautApplicationContext) ctx).beanFactory.getSingleton(names.first())

        then:
        noExceptionThrown()

        cleanup:
        ctx.close()
    }

    @Singleton
    static class FooService {
        @Autowired
        @Named("bar1")
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
