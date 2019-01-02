package io.micronaut.spring.annotation.context

import io.micronaut.annotation.processing.test.AbstractTypeElementSpec
import io.micronaut.cache.annotation.CachePut
import io.micronaut.cache.annotation.Cacheable
import io.micronaut.context.annotation.Requires
import io.micronaut.inject.BeanDefinition
import org.springframework.cache.annotation.CacheEvict
import spock.lang.Specification

import javax.inject.Singleton

class ProfileAnnotationMapperSpec extends AbstractTypeElementSpec  {


    void "test profile mapping"() {
        given:
        BeanDefinition definition = buildBeanDefinition(
                "test.MyBean",
                """

package test;

import org.springframework.context.annotation.*;
import org.springframework.stereotype.Service;

@Service("myname")
@Profile("test")
class MyBean {

}

"""
        )

        expect:
        definition.isAnnotationPresent(Singleton)
        definition.isAnnotationPresent(Requires)
        definition.synthesize(Requires).env() == ['test'] as String[]
    }

}
