plugins {
    kotlin("jvm") version "2.1.10"
}

group = "com.github.zimablue.attrsystem"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("com.github.zimablue.devoutserver:DevoutServer:1.0-SNAPSHOT")
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

tasks.jar {
    doLast {
        val destinationDir = file("F:\\Code\\MyCode\\MineStom\\Devout\\DevoutServerTest\\plugins") // 替换为目标路径
        copy {
            from(archiveFile)
            into(destinationDir)
        }
        println("Jar file copied to $destinationDir")
    }
}