// HarmonyOS build configuration.
//
// ohos cannot be added as a target to the normal build: the KuiklyUI artifacts
// that carry ohosArm64 are published against Kotlin 2.0.21-KBA-010, a Tencent
// distribution, while the main build runs stock Kotlin 2.1.21. KuiklyUI itself
// solves this with parallel build files per Kotlin version, and this mirrors
// that — the ordinary build is untouched, and ohos is driven explicitly:
//
//     ./gradlew -c settings.ohos.gradle.kts :gearui-kit:compileKotlinOhosArm64

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/") }
        maven { url = uri("https://mirrors.tencent.com/nexus/repository/gradle-plugins/") }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/") }
    }
}

rootProject.name = "GearUI-Kit"

val ohosBuildFile = "build.ohos.gradle.kts"
rootProject.buildFileName = ohosBuildFile

include(":gearui-kit")
project(":gearui-kit").buildFileName = ohosBuildFile

include(":sample")
project(":sample").buildFileName = ohosBuildFile
