package io.micronaut.spring.annotation.context

import io.micronaut.annotation.processing.test.AbstractTypeElementSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.inject.BeanDefinition
import io.micronaut.spring.context.aop.SpringConfigurationInterceptor

class ConfigurationAnnotationMappingSpec extends AbstractTypeElementSpec {

    void "test configuration mapping"() {
        given:
        ApplicationContext applicationContext = buildContext('test.MyConfiguration', '''
package test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MyConfiguration {

    @Bean
    @Primary
    public MyBean myBean() {
        return new MyBean("default");
    }


    @Bean("another")
    public MyBean anotherBean() {
        return new MyBean("another");
    }
}

class MyBean {
        private final String name;

        MyBean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
''')

        applicationContext.registerSingleton(new SpringConfigurationInterceptor())
        def type = applicationContext.classLoader.loadClass('test.MyBean')
        def config = applicationContext.classLoader.loadClass('test.MyConfiguration')

        expect:
        applicationContext.getBean(type) == applicationContext.getBean(type)
        applicationContext.getBean(config).myBean() == applicationContext.getBean(config).myBean()
    }
}
