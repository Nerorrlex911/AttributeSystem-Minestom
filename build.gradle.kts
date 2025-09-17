plugins {
    kotlin("jvm") version "2.1.10"
}

group = "com.github.zimablue.attrsystem"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("net.minestom:minestom:2025.08.29-1.21.8")
    compileOnly(fileTree("libs"))
    compileOnly("com.ezylang:EvalEx:3.5.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(22)
}