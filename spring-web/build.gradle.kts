plugins {
    id("io.micronaut.build.internal.spring-module")
}

dependencies {

    compileOnly(mn.micronaut.http.server.netty)
    compileOnly(mnViews.micronaut.views.core)

    api(platform(libs.boms.spring))
    api(projects.micronautSpringContext)
    api(libs.spring.web)
    api(mn.reactor)

    implementation(mn.micronaut.http)

    testAnnotationProcessor(mn.micronaut.inject.java)
    testAnnotationProcessor(projects.micronautSpringAnnotation)
    testAnnotationProcessor(projects.micronautSpringWebAnnotation)

    testImplementation(mn.micronaut.inject.java)
    testImplementation(mnValidation.micronaut.validation)
    testImplementation(mn.micronaut.jackson.databind)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)

    testRuntimeOnly(mnViews.micronaut.views.thymeleaf)
    testRuntimeOnly(libs.spring.boot.starter.thymeleaf)
}
