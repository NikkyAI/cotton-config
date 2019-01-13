import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Versions.kotlin
    id("org.jetbrains.kotlin.kapt") version "1.3.11"
    id("com.github.johnrengelman.shadow") version Versions.shadowJar
    application
}

group = "moe.nikky"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
        maven(url = "http://repo.elytradev.com/") {
            name = "jankson"
        }
        maven(url = "https://maven.fabricmc.net") {
            name = "fabric"
        }
    }
}


dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(group = "blue.endless", name = "jankson", version = "1.0.0-9")

    implementation(group = "com.google.code.gson", name = "gson", version = "2.8.5")

    implementation(group = "org.apache.logging.log4j", name = "log4j-core", version = "2.8.1")

    implementation(project("annotations"))

    implementation(project("annotationProcessor"))
    kapt(project("annotationProcessor"))
//    annotationProcessor(project("annotationProcessor"))

    implementation(group = "io.github.microutils", name = "kotlin-logging", version = Versions.kotlinLogging)
    implementation(group = "ch.qos.logback", name = "logback-classic", version = Versions.logbackClassic)

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = Versions.junit)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = Versions.junit)
}

sourceSets {
    main {
        resources.srcDir("build/generated/source/cotton-config")
    }
}

configurations {
//    annotationProcessor.configure {
//        extendsFrom(configurations.compileOnly.get())
//    }
//    kapt.configure {
//        extendsFrom(configurations.compileOnly.get())
//    }
}

application {
    mainClassName = Constants.mainClassName
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.getByName<ShadowJar>("shadowJar") {
    exclude(
        "META-INF/MANIFEST.MF",
        "META-INF/*.SF",
        "META-INF/*.DSA",
        "META-INF/*.RSA"
    )
}

// or

//val application = extensions.getByType<JavaApplication>()
//
//tasks.getByName<Jar>("jar") {
//    manifest {
//        attributes(
////             Class-Path seems to not be required when everything is shaded in anyways
////            "Class-Path" to configurations.compileClasspath.get().resolve().joinToString(" ") { it.name },
//            "Main-Class" to Constants.mainClassName
//        )
//    }
//    from(configurations.compileClasspath.get().resolve().map { entry -> zipTree(entry) }) {
//        exclude("META-INF/MANIFEST.MF")
//        exclude("META-INF/*.SF")
//        exclude("META-INF/*.DSA")
//        exclude("META-INF/*.RSA")
//    }
//}