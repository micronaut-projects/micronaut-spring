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
package io.micronaut.spring.tx

import io.micronaut.context.ApplicationContext
import spock.lang.Specification

// simple tests for tx management. More robust tests exist in github.com/micronaut-projects/micronaut-sql
class MockSpringTxSpec extends Specification {

    void "test spring tx management"() {
        given:
        def ctx = ApplicationContext.run()
        TransactionalBean transactionalBean = ctx.getBean(TransactionalBean)

        expect:
        transactionalBean.doSomething() == 'foo'

        cleanup:
        ctx.close()
    }

    void "test meta spring tx management"() {
        given:
        def ctx = ApplicationContext.run()
        MetaTransactionalBean transactionalBean = ctx.getBean(MetaTransactionalBean)

        expect:
        transactionalBean.doSomething() == 'foo'

        cleanup:ctx.close()
    }
}
