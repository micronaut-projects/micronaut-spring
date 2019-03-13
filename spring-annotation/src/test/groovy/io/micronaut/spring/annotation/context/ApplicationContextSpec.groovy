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

import io.micronaut.spring.context.ManagedApplicationContext
import io.micronaut.spring.context.MicronautApplicationContext
import org.springframework.beans.factory.BeanFactoryUtils
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Service
import spock.lang.Specification

class ApplicationContextSpec extends Specification {

    void "test register singleton"() {
        given:
        MicronautApplicationContext applicationContext = new MicronautApplicationContext()
        applicationContext.start()

        def singleton = new MySingleton()
        when:
        applicationContext.beanFactory.registerSingleton(
                "foo", singleton
        )


        then:
        applicationContext.getBean("foo").is(singleton)
        applicationContext.getType("foo") == MySingleton
        applicationContext.containsBean("foo")
        applicationContext.beanFactory.containsLocalBean("foo")
    }

    void "test spring application context implementation"() {
        when:
        ManagedApplicationContext context = new MicronautApplicationContext( io.micronaut.context.ApplicationContext.build()
                .properties("foo.bar":'somevalue'))

        then:
        !context.isRunning()

        when:
        context.start()

        then:
        context.isRunning()

        when:
        def names = context.getBeanNamesForType(MyNamedService)

        then:
        names
        context.getBean(names[0]) instanceof MyNamedService
        context.getBean(names[0]) == context.getBean(MyNamedService)
        context.findAnnotationOnBean(names[0], Service)
        context.getBeansWithAnnotation(Service).size() == 2 // the event listener is also there
        context.getBeanProvider(MyNamedService).ifAvailable == context.getBean(MyNamedService)
        context.getBeansOfType(MyNamedService).size() == 1

        when:
        context.stop()

        then:
        !context.isRunning()
    }

    void "test use as parent context"() {
        when:
        ManagedApplicationContext parentContext = new MicronautApplicationContext(
                io.micronaut.context.ApplicationContext.build()
                    .properties("foo.bar":'somevalue')
        )
        parentContext.start()

        ApplicationContext context = new GenericApplicationContext(parentContext)
        context.refresh()

        then:
        context.getBean(MyNamedService)
        context.getBeanProvider(MyNamedService).ifAvailable == context.getBean(MyNamedService)
        BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, MyNamedService).size() == 1

    }

    static class MySingleton {}
}
