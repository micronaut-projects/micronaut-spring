package io.micronaut.spring.annotation.context

import io.micronaut.annotation.processing.test.AbstractTypeElementSpec
import io.micronaut.cache.annotation.CachePut
import io.micronaut.cache.annotation.Cacheable
import io.micronaut.inject.BeanDefinition
import org.springframework.cache.annotation.CacheEvict

class CacheAnnotationMappingSpec extends AbstractTypeElementSpec {


    void "test cacheable mapping"() {
        given:
        BeanDefinition definition = buildBeanDefinition(
            "test.MyBean",
                """

package test;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.*;
@Service("myname")
class MyBean {

    @Cacheable("test")
    String testCache() {
        return null;
    }
    
    @CacheEvict("test")
    String testCacheEvict() {
        return null;
    }
    
    @CachePut("test")
    String testCachePut() {
        return null;
    }
}

"""
        )

        expect:
        definition.executableMethods.size() == 3
        definition.executableMethods[0].isAnnotationPresent(Cacheable)
        definition.executableMethods[0].getValue(Cacheable, String).get() == 'test'
        definition.executableMethods[1].isAnnotationPresent(CacheEvict)
        definition.executableMethods[1].getValue(CacheEvict, String).get() == 'test'
        definition.executableMethods[2].isAnnotationPresent(CachePut)
        definition.executableMethods[2].getValue(CachePut, String).get() == 'test'
    }
}
