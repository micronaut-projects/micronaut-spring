plugins {
    id("io.micronaut.build.internal.spring-base")
    id("java-library")
    id("groovy")
}

dependencies {
    testAnnotationProcessor(projects.micronautSpringAnnotation)
    testAnnotationProcessor(mn.micronaut.inject.java)

    testCompileOnly(mn.micronaut.inject.groovy)

    testImplementation(projects.micronautSpring)
    testImplementation(mnTest.micronaut.test.spock)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
