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

import io.micronaut.annotation.processing.test.AbstractTypeElementSpec
import io.micronaut.cache.annotation.CachePut
import io.micronaut.cache.annotation.Cacheable
import io.micronaut.context.ApplicationContext
import io.micronaut.inject.BeanDefinition
import org.springframework.cache.annotation.CacheEvict

class CacheAnnotationMappingSpec extends AbstractTypeElementSpec {

    void "test cacheable mapping"() {
        given:
        ApplicationContext applicationContext = buildContext(
            "test.MyBean",
                """

package test;

import io.micronaut.context.annotation.Executable;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.*;

@Service("myname")
@Executable
class MyBean {

    @Cacheable("test")
    String testCache() {
        return null;
    }
    
    @CacheEvict("test")
    public String testCacheEvict() {
        return null;
    }
    
    @CachePut("test")
    String testCachePut() {
        return null;
    }
}

"""
        )
        def definition = applicationContext
                .getBeanDefinition(applicationContext.getClassLoader().loadClass('test.MyBean'))

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
