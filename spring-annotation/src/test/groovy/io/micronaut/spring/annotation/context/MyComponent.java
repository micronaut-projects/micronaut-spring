package io.micronaut.spring.annotation.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MyComponent implements BeanFactoryAware, EnvironmentAware, InitializingBean {

    @Value("${foo.bar}")
    String value;

    @Autowired
    MyNamedService myNamedService;
    private ApplicationEvent lastEvent;
    BeanFactory beanFactory;
    Environment environment;
    boolean initialized;


    @EventListener
    public void receiveEvent(ApplicationEvent event) {
        this.lastEvent = event;
    }

    public ApplicationEvent getLastEvent() {
        return lastEvent;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (beanFactory == null) {
            throw new IllegalStateException("Bean factory must be set");
        }
        if (environment == null) {
            throw new IllegalStateException("Enviroment must be set");
        }

        this.initialized = true;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
