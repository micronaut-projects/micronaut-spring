plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(libs.sonatype.scan)
    implementation(libs.micronaut.shared.settings)
}
