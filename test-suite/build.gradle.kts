plugins {
    id("io.micronaut.build.internal.spring-base")
    id("java-library")
    id("groovy")
}

dependencies {
    testAnnotationProcessor(projects.springAnnotation)
    testAnnotationProcessor(mn.micronaut.inject.java)
    testCompileOnly(mn.micronaut.inject.groovy)
    testImplementation(projects.spring)
    testImplementation(mnTest.micronaut.test.spock)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
