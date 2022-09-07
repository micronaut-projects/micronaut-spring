package io.micronaut.spring.annotation.context

import io.micronaut.annotation.processing.test.AbstractTypeElementSpec
import io.micronaut.inject.BeanDefinitionReference
import io.micronaut.spring.beans.ObjectProviderBeanDefinition
import io.micronaut.spring.beans.SpringImport
import org.springframework.aop.framework.Advised
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.ImportSelector
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.env.Environment
import org.springframework.core.type.AnnotationMetadata
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
import org.springframework.scheduling.annotation.SchedulingConfiguration
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration
import org.springframework.transaction.event.TransactionalEventListenerFactory
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor
import org.springframework.transaction.interceptor.TransactionAttributeSource
import org.springframework.transaction.interceptor.TransactionInterceptor
import spock.util.concurrent.PollingConditions

/**
 *
 * @author graemerocher
 */
class ImportAnnotationSpec extends AbstractTypeElementSpec {
    void "test @EnableTransactionManagement"() {
        given:
        def context = buildContext("enabeasync.Application",'''
package enableasync;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Component;
import io.micronaut.spring.annotation.context.*;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@EnableTransactionManagement
class Application {
}

@Component
class Job {
    @Transactional
    public boolean doWork() {
        return TransactionAspectSupport.currentTransactionStatus().isNewTransaction();
    }
}

@Configuration
class DataFactory {
    @Bean
    TransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    DataSource dataSource() {
        return new DriverManagerDataSource("jdbc:h2:mem:devDb;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE");
    }
}
''', true)
        def job = getBean(context, 'enableasync.Job')

        expect:
        context.containsBean(ProxyTransactionManagementConfiguration)
        context.containsBean(BeanFactoryTransactionAttributeSourceAdvisor)
        context.containsBean(TransactionAttributeSource)
        context.containsBean(TransactionInterceptor)
//        context.containsBean(TransactionalEventListenerFactory) needs static method support
        context.getBean(TransactionInterceptor)
        job != null
        job instanceof Advised
        job.doWork()

        cleanup:
        context.close()
    }

    void "test @EnabledScheduling"() {
        given:
        def context = buildContext("enabeasync.Application",'''
package enableasync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import io.micronaut.spring.annotation.context.*;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

@EnableScheduling
class Application {
}

@Component
class Job {
    public boolean executed = false;
    @Scheduled(fixedDelay = 100)
    public void doWork() {
        executed = true;
        System.out.println(Thread.currentThread().getName());
    }
}
''', true)
        def job = getBean(context, 'enableasync.Job')
        def job2 = getBean(context, 'enableasync.Job')
        PollingConditions conditions = new PollingConditions()
        expect:
        job.is(job2)
        context.containsBean(SchedulingConfiguration)
        context.getBean(ScheduledAnnotationBeanPostProcessor)
        context.getBean(ScheduledAnnotationBeanPostProcessor).is(context.getBean(ScheduledAnnotationBeanPostProcessor))
        job != null
        conditions.eventually {
            job.executed
        }


        cleanup:
        context.close()
    }

    void "test @EnableAsync"() {
        given:
        def context = buildContext("enabeasync.Application",'''
package enableasync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import io.micronaut.spring.annotation.context.*;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

@EnableAsync
class Application {
}

@Component
class Job {
    @Async
    public void doWork() {
        System.out.println(Thread.currentThread().getName());
    }
}
''', true)
        def job = getBean(context, 'enableasync.Job')

        expect:
        context.containsBean(AsyncAnnotationBeanPostProcessor)
        context.getBean(AsyncAnnotationBeanPostProcessor)
        job != null
        job instanceof Advised

        cleanup:
        context.close()
    }

    void "test build time import selector"() {
        given:
        def context = buildContext('''
package importselector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.stereotype.Component;
import io.micronaut.spring.annotation.context.*;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

@Component
@Speed(fast = true)
class Car {
    @Autowired
    Engine engine;
}
''')
        def car = getBean(context, 'importselector.Car')

        expect:
        car.engine.getClass().name.contains("Fast")

        cleanup:
        context.close()
    }

    void "test import annotation repetition"() {
        given:
        def context = buildContext('''
package importtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import io.micronaut.spring.annotation.context.*;

@ImportOne
@ImportTwo
@Component
class Foo {
    @Autowired
    One one;
    @Autowired
    Two two;

}

@Import({OneConfiguration.class, BeanA.class})
@interface ImportOne {}

@Import(TwoConfiguration.class)
@interface ImportTwo {}


''')
        when:
        def fooDefinition = getBeanDefinition(context, 'importtest.Foo')
        def oneDefinition = getBeanDefinition(context, One.name)
        def twoDefinition = getBeanDefinition(context, Two.name)
        def foo = getBean(context, 'importtest.Foo')

        then:
        foo
        foo.one
        foo.two
        foo.two.initCalled
        !foo.two.destroyCalled
        context != null
        fooDefinition != null
        fooDefinition.getAnnotationMetadata().getAnnotationValuesByType(SpringImport).size() == 2

        when:
        context.close()

        then:
        foo.two.destroyCalled
    }

    @Override
    List<BeanDefinitionReference<?>> getBuiltInBeanReferences() {
        return super.getBuiltInBeanReferences() + [new ObjectProviderBeanDefinition()]
    }
}

@Configuration
class OneConfiguration {
    @Autowired
    BeanA beanA;

    @Bean
    One one() {
        assert beanA != null
        return new One();
    }
}

@Configuration
class TwoConfiguration {
    @Bean(initMethod="init", destroyMethod="destroy")
    Two two() {
        return new Two();
    }
}

class One {}
class Two {
    boolean initCalled = false
    boolean destroyCalled = false
    void init() {
        initCalled = true
    }

    void destroy() {
        destroyCalled = true
    }
}


@Component
class BeanA {}

interface Engine {}

class FastEngine implements Engine {}

class SlowEngine implements Engine {}

class MySelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        MergedAnnotation<Speed> mergedAnnotation = importingClassMetadata.getAnnotations().get(Speed.class);
        if(mergedAnnotation.getBoolean("fast")) {
            return new String[] { FastEngine.class.getName() };
        } else {
            return new String[] { SlowEngine.class.getName() };
        }
    }
}

@Import(MySelector.class)
@interface Speed {
    boolean fast();
}
