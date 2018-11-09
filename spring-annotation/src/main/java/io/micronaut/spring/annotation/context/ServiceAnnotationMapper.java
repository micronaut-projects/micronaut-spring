package io.micronaut.spring.annotation.context;

/**
 * Maps {@code @Service} the same was as {@link ComponentAnnotationMapper}
 *
 * @author graemerocher
 * @since 1.0
 */
public class ServiceAnnotationMapper extends ComponentAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.stereotype.Service";
    }
}
