package io.micronaut.spring.annotation.context;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Repository
@Transactional(
        readOnly = true,
        isolation = Isolation.READ_COMMITTED,
        propagation = Propagation.REQUIRES_NEW
)
public class MyTransactionalService {

    public List<String> someMethod() {
        return Collections.emptyList();
    }
}
