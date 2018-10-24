import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

plugins {
    kotlin("jvm") version "1.2.51"
    application
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib"))
    compile("com.fasterxml.jackson.core", "jackson-databind", "2.9.7")
    compile("org.apache.httpcomponents", "httpclient", "4.5.6")
    compile("org.apache.httpcomponents", "httpcore", "4.4.10")
    testCompile("org.mockito", "mockito-core", "2.23.0")
    testCompile("org.mockito", "mockito-junit-jupiter", "2.23.0")
    testCompile("com.xebialabs.restito", "restito", "0.9.3")
    testCompile("org.junit.jupiter", "junit-jupiter-api", "5.3.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "PostCounterKt"
}