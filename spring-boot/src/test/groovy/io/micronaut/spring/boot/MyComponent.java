package io.micronaut.spring.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import some.other.pkg.MyOtherComponent;

@Component
public class MyComponent {
    @Autowired
    MyOtherComponent myOtherComponent;
}
