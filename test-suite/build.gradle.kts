plugins {
    id("io.micronaut.build.internal.spring-base")
    `java-library`
    groovy
}

dependencies {
    testAnnotationProcessor(projects.micronautSpringAnnotation)
    testAnnotationProcessor(mn.micronaut.inject.java)

    testCompileOnly(mn.micronaut.inject.groovy)

    testImplementation(projects.micronautSpring)
    testImplementation(mnTest.micronaut.test.spock)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
