plugins {
    id("io.micronaut.build.internal.spring-base")
    id("io.micronaut.build.internal.module")
    id("org.sonatype.gradle.plugins.scan")
}
val ossIndexUsername = System.getenv("OSS_INDEX_USERNAME") ?: project.properties["ossIndexUsername"] as String?
val ossIndexPassword = System.getenv("OSS_INDEX_PASSWORD") ?: project.properties["ossIndexPassword"] as String?
if (ossIndexUsername != null && ossIndexPassword != null) {
    ossIndexAudit {
        username = ossIndexUsername
        password = ossIndexPassword
    }
}

val libs = versionCatalogs.named("libs")

dependencies {
    api(platform(libs.findLibrary("boms-spring").get()))
    testImplementation(platform(libs.findLibrary("boms-spring").get()))
    //compileOnly 'com.google.code.findbugs:jsr305' // for "warning: unknown enum constant When.MAYBE"
    //testCompileOnly 'com.google.code.findbugs:jsr305' // for "warning: unknown enum constant When.MAYBE"
}
