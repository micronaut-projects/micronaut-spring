/*
 * Copyright 2017-2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.spring.tx.test;

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.test.annotation.TransactionMode;
import io.micronaut.test.context.TestContext;
import io.micronaut.test.context.TestExecutionListener;
import io.micronaut.test.extensions.AbstractMicronautExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Integrates Spring's transaction management if it is available.
 *
 * @author graemerocher
 * @since 1.0
 */
@Requires(classes = {PlatformTransactionManager.class, AbstractMicronautExtension.class})
@EachBean(PlatformTransactionManager.class)
@Requires(property = AbstractMicronautExtension.TEST_TRANSACTIONAL, value = StringUtils.TRUE, defaultValue = StringUtils.TRUE)
public class SpringTransactionTestExecutionListener implements TestExecutionListener {

    private final PlatformTransactionManager transactionManager;
    private TransactionStatus tx;
    private final AtomicInteger counter = new AtomicInteger();
    private final AtomicInteger setupCounter = new AtomicInteger();
    private final boolean rollback;
    private final TransactionMode transactionMode;

    /**
     * @param transactionManager Spring's {@code PlatformTransactionManager}
     * @param rollback           {@code true} if the transaction should be rollback
     * @param transactionMode    {@code TransactionMode} to use for each test
     */
    public SpringTransactionTestExecutionListener(
            PlatformTransactionManager transactionManager,
            @Property(name = AbstractMicronautExtension.TEST_ROLLBACK, defaultValue = "true") boolean rollback,
            @Property(name = AbstractMicronautExtension.TEST_TRANSACTION_MODE, defaultValue = "SEPARATE_TRANSACTIONS") TransactionMode transactionMode) {

        this.transactionManager = transactionManager;
        this.rollback = rollback;
        this.transactionMode = transactionMode;
    }

    @Override
    public void beforeSetupTest(TestContext testContext) {
        beforeTestExecution(testContext);
    }

    @Override
    public void afterSetupTest(TestContext testContext) {
        if (transactionMode.equals(TransactionMode.SINGLE_TRANSACTION)) {
            setupCounter.getAndIncrement();
        } else {
            afterTestExecution(false);
        }
    }

    @Override
    public void beforeCleanupTest(TestContext testContext) throws Exception {
        beforeTestExecution(testContext);
    }

    @Override
    public void afterCleanupTest(TestContext testContext) throws Exception {
        afterTestExecution(false);
    }

    @Override
    public void afterTestExecution(TestContext testContext) {
        if (transactionMode.equals(TransactionMode.SINGLE_TRANSACTION)) {
            counter.addAndGet(-setupCounter.getAndSet(0));
        }
        afterTestExecution(this.rollback);
    }

    @Override
    public void beforeTestExecution(TestContext testContext) {
        if (counter.getAndIncrement() == 0) {
            tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        }
    }

    private void afterTestExecution(boolean rollback) {
        if (counter.decrementAndGet() == 0) {
            if (rollback) {
                transactionManager.rollback(tx);
            } else {
                transactionManager.commit(tx);
            }
        }
    }
}

