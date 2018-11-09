package io.micronaut.spring.boot

import io.micronaut.context.ApplicationContext
import io.micronaut.context.ApplicationContextBuilder
import io.micronaut.core.io.socket.SocketUtils
import io.micronaut.spring.context.MicronautApplicationContext
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.ConfigurableApplicationContext
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

        cleanup:
        springContext.close()

    }

    @SpringBootApplication
    static class Application extends SpringBootServletInitializer{}
}
