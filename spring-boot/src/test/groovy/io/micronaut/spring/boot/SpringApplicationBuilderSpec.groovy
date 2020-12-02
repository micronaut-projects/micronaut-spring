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

import io.micronaut.context.ApplicationContext
import io.micronaut.core.io.socket.SocketUtils
import io.micronaut.spring.context.MicronautApplicationContext
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.ParentContextApplicationContextInitializer
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component
import some.other.pkg.FeaturesClient
import some.other.pkg.MyOtherComponent
import some.other.pkg.TestClient
import spock.lang.Specification

class SpringApplicationBuilderSpec extends Specification{

    void "test with Spring application builder"() {
        when:
        SpringApplicationBuilder builder = new SpringApplicationBuilder()
        def port = SocketUtils.findAvailableTcpPort()
        def props = ["server.port": port,
                     'spring.test.port': port]
        def context = new MicronautApplicationContext(ApplicationContext.build().properties(
                props
        ))
        context.start()
        builder.web(WebApplicationType.SERVLET)
        builder.parent(context)
        builder.sources(Application)
        builder.properties(
                props
        )
        def application = builder.build()
        ConfigurableApplicationContext springContext = application.run()

        then:
        springContext != null
        springContext.getBean(MyComponent).myOtherComponent
        springContext.getBean(MyComponent).myOtherComponent == springContext.getBean(MyOtherComponent)
        springContext.getBean(FeaturesClient)
        springContext.getBean(TestClient).hello() == 'good'
        springContext.getBean(MyRunner).executed
        
        cleanup:
        springContext.close()

    }

    void "test with Spring application builder with bootstrap context"() {
        when:
        SpringApplicationBuilder builder = new SpringApplicationBuilder()
        def port = SocketUtils.findAvailableTcpPort()
        def props = ["server.port": port,
                     'spring.test.port': port]
        def context = new MicronautApplicationContext(ApplicationContext.build().properties(
                props
        ))
        context.start()
        builder.web(WebApplicationType.SERVLET)
        builder.parent(context)
        builder.sources(Application)
        builder.properties(
                props
        )
        def application = builder.build()
        ConfigurableApplicationContext springContext = application.run()

        final ConfigurableApplicationContext bootstrapAppContext = new GenericApplicationContext()
        bootstrapAppContext.refresh()

        new ParentContextApplicationContextInitializer(bootstrapAppContext)
                .initialize(context)

        bootstrapAppContext.beanFactory.registerSingleton("mySingleton", new MySingleton())

        then:
        springContext != null
        springContext.getBean(MyComponent).myOtherComponent
        springContext.getBean(MyComponent).myOtherComponent == springContext.getBean(MyOtherComponent)
        springContext.getBean(FeaturesClient)
        springContext.getBean(TestClient).hello() == 'good'
        springContext.getBean(MyRunner).executed
        springContext.getBean(MySingleton)
        springContext.beanFactory.parentBeanFactory == context.beanFactory
        context.beanFactory.parentBeanFactory == bootstrapAppContext.beanFactory

        cleanup:
        springContext.close()

    }

    @Component
    static class MyRunner implements ApplicationRunner {
        boolean executed = false
        @Override
        void run(ApplicationArguments args) throws Exception {
            executed = args != null
        }
    }
    @SpringBootApplication
    static class Application extends SpringBootServletInitializer{}

    class MySingleton {

    }
}
