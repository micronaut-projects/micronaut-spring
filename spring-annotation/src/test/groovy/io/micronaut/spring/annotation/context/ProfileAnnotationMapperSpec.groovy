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
