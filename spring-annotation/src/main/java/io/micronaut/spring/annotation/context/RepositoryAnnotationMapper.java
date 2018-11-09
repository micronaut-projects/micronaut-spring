package io.micronaut.spring.annotation.context;

/**
 * Maps {@code @Repository} the same was as {@link ComponentAnnotationMapper}
 *
 * @author graemerocher
 * @since 1.0
 */
public class RepositoryAnnotationMapper extends ComponentAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.stereotype.Repository";
    }
}
