pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
rootProject.name = "cotton-config"

include("annotationProcessor")
include("annotations")
include("loader")

