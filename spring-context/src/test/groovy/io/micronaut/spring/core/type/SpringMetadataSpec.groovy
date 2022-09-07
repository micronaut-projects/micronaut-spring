package io.micronaut.spring.core.type

import io.micronaut.annotation.processing.test.AbstractTypeElementSpec
import io.micronaut.context.annotation.Bean
import io.micronaut.core.util.StringUtils
import io.micronaut.inject.ast.ClassElement
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.stereotype.Indexed
import spock.lang.Unroll

import java.lang.annotation.Annotation

import static io.micronaut.core.util.StringUtils.EMPTY_STRING_ARRAY

class SpringMetadataSpec extends AbstractTypeElementSpec {
    @Unroll
    void "test class element metadata for keywords #keywords"() {
        when:
        def classElement = buildClassElement("""
package test;

import org.springframework.stereotype.Component;

@Component
$keywords Test {

}
""")
        ClassElementSpringMetadata metadata = new ClassElementSpringMetadata(classElement)

        then:
        metadata.isAbstract() == isAbstract
        metadata.isInterface() == isInterface
        metadata.isFinal() == isFinal
        metadata.isAnnotation() == isAnnotation
        metadata.className == 'test.Test'
        metadata.annotations.isDirectlyPresent(Component)
        !metadata.annotations.isDirectlyPresent(Indexed)
        !metadata.annotations.isDirectlyPresent(Configuration)
        !metadata.annotations.isPresent(Configuration)
        metadata.annotations.isPresent(Component)
        metadata.annotations.isPresent(Indexed)
        metadata.annotations.get(Component).isDirectlyPresent()
        metadata.annotations.get(Component).isPresent()
        metadata.getEnclosingClassName() == null
        metadata.getInterfaceNames() == interfaces
        metadata.superClassName == null

        where:
        keywords         | isFinal | isAbstract | isInterface | isAnnotation | interfaces
        'class'          | false   | false      | false       | false        | EMPTY_STRING_ARRAY
        'interface'      | false   | true       | true        | false        | EMPTY_STRING_ARRAY
        'abstract class' | false   | true       | false       | false        | EMPTY_STRING_ARRAY
        'final class'    | true    | false      | false       | false        | EMPTY_STRING_ARRAY
        '@interface'     | false   | true       | false       | true         | [Annotation.name] as String[]
    }

    @Unroll
    void "test class element metadata for keywords #keywords"() {
        when:
        def classElement = buildBeanDefinition('test.Test', """
package test;

import org.springframework.stereotype.Component;

@Component
$keywords Test {

}
""")
        BeanDefinitionSpringMetadata metadata = new BeanDefinitionSpringMetadata(classElement)

        then:
        metadata.isAbstract() == isAbstract
        metadata.isInterface() == isInterface
        metadata.isFinal() == isFinal
        metadata.isAnnotation() == isAnnotation
        metadata.className == 'test.Test'
        metadata.annotations.isDirectlyPresent(Component)
        !metadata.annotations.isDirectlyPresent(Indexed)
        !metadata.annotations.isDirectlyPresent(Configuration)
        !metadata.annotations.isPresent(Configuration)
        metadata.annotations.isPresent(Component)
        metadata.annotations.isPresent(Indexed)
        metadata.annotations.get(Component).isDirectlyPresent()
        metadata.annotations.get(Component).isPresent()
        metadata.getEnclosingClassName() == null
        metadata.getInterfaceNames() == interfaces
        metadata.superClassName == null

        where:
        keywords         | isFinal | isAbstract | isInterface | isAnnotation | interfaces
        'class'          | false   | false      | false       | false        | EMPTY_STRING_ARRAY
        'abstract class' | false   | true       | false       | false        | EMPTY_STRING_ARRAY
        'final class'    | true    | false      | false       | false        | EMPTY_STRING_ARRAY
    }
}
