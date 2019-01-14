import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.kapt")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(group = "blue.endless", name = "jankson", version = "1.0.0-9")
    implementation(project(":annotations"))
    implementation(project(":loader"))

//    implementation(group = "com.sun", name= "tools", version= "1.7.0.13")
    implementation(files("${System.getenv("JAVA_HOME")}/lib/tools.jar"))
    println(System.getenv("JAVA_HOME"))

    compileOnly("com.google.auto.service:auto-service:1.0-rc4")
    kapt("com.google.auto.service:auto-service:1.0-rc4")
}

//configurations {
//    annotationProcessor.configure {
//        extendsFrom(configurations.implementation.get())
//    }
//}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
