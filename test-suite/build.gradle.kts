plugins {
    groovy
    id("io.micronaut.build.internal.java-base")

}

dependencies {
    testAnnotationProcessor(projects.micronautSpringAnnotation)
    testAnnotationProcessor(mn.micronaut.inject.java)

    testCompileOnly(mn.micronaut.inject.groovy)

    testImplementation(projects.micronautSpring)
    testImplementation(mnTest.micronaut.test.spock)
}

tasks.test {
    useJUnitPlatform()
}
