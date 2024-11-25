package io.micronaut.spring.annotation.context;

import io.micronaut.context.annotation.Requires;
import org.springframework.stereotype.Component;

@Component
@Requires(property = "activation.property")
public class MyInterfaceImpl implements MyInterface {
}
