plugins {
    id("io.micronaut.build.internal.spring-module")
}

dependencies {
    api(projects.micronautSpringContext)
    api(libs.managed.spring.boot)
    compileOnly(libs.spring.boot.autoconfigure)

    testAnnotationProcessor(mn.micronaut.inject.java)
    testAnnotationProcessor(projects.micronautSpringBootAnnotation)
    testAnnotationProcessor(projects.micronautSpringWebAnnotation)

    testImplementation(projects.micronautSpringWeb)
    testImplementation(mn.micronaut.management)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(libs.spring.boot.autoconfigure)
    testImplementation(libs.spring.boot.actuator)
    testImplementation(libs.servlet.api)
    testImplementation(mn.micronaut.jackson.databind)
    testRuntimeOnly(libs.managed.spring.boot.starter.web)
    testRuntimeOnly(libs.spring.boot.starter.tomcat)
    testRuntimeOnly(mn.jakarta.annotation.api)
}
