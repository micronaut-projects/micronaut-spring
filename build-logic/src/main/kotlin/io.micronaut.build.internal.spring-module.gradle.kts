plugins {
    id("io.micronaut.build.internal.spring-base")
    id("io.micronaut.build.internal.module")
}
val libs = versionCatalogs.named("libs")

dependencies {
    api(platform(libs.findLibrary("boms-spring").get()))
    testImplementation(platform(libs.findLibrary("boms-spring").get()))
    //compileOnly 'com.google.code.findbugs:jsr305' // for "warning: unknown enum constant When.MAYBE"
    //testCompileOnly 'com.google.code.findbugs:jsr305' // for "warning: unknown enum constant When.MAYBE"
}
