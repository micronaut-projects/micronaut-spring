pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    includeBuild("build-logic")
}

plugins {
    id("io.micronaut.build.shared.settings") version "8.0.0-RC1"
}

rootProject.name = "spring-parent"

include(
    "spring",
    "spring-annotation",
    "spring-bom",
    "spring-boot-annotation",
    "spring-boot",
    "spring-boot-starter",
    "spring-web-annotation",
    "spring-web",
    "spring-context",
    "test-suite",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

micronautBuild {
    useStandardizedProjectNames = true
    importMicronautCatalog()
    importMicronautCatalog("micronaut-cache")
    importMicronautCatalog("micronaut-views")
    importMicronautCatalog("micronaut-validation")
    importMicronautCatalog("micronaut-servlet")
    importMicronautCatalog("micronaut-sql")
}
