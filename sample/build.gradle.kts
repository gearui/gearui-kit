import org.gradle.api.tasks.Copy

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.compose")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp") version "2.1.21-2.0.1"
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    // iOS targets
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "GearUI-KuiklyUI Sample - GearUI Component Demo"
        homepage = "https://github.com/zoujiaqing/tdesign-kuikly"
        version = "1.0"
        ios.deploymentTarget = "14.0"
        podfile = project.file("iosApp/Podfile")
        framework {
            baseName = "GearUISample"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // GearUI Component Library
                implementation(project(":gearui-kit"))

                // KuiklyUI Core（用于 @Page 注解和 ComposeContainer）
                implementation("com.tencent.kuikly-open:core:2.15.2-2.1.21")
                implementation("com.tencent.kuikly-open:core-annotations:2.15.2-2.1.21")

                // Compose runtime
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
            }
        }

        androidMain.dependencies {
            // KuiklyUI Android 渲染支持
            implementation("com.tencent.kuikly-open:core-render-android:2.15.2-2.1.21")
            implementation("androidx.appcompat:appcompat:1.6.1")
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    namespace = "com.gearui.kit.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gearui.kit.sample"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
    }

    // Configure assets directory for cross-platform assets
    sourceSets {
        named("main") {
            assets.srcDirs("src/commonMain/assets")
        }
    }
}

// KSP 配置
dependencies {
    add("kspAndroid", "com.tencent.kuikly-open:core-ksp:2.15.2-2.1.21")
    add("kspIosX64", "com.tencent.kuikly-open:core-ksp:2.15.2-2.1.21")
    add("kspIosArm64", "com.tencent.kuikly-open:core-ksp:2.15.2-2.1.21")
    add("kspIosSimulatorArm64", "com.tencent.kuikly-open:core-ksp:2.15.2-2.1.21")
}

val syncSharedAssetsToPodResources by tasks.registering(Copy::class) {
    val sharedAssetsDir = project(":gearui-kit").projectDir.resolve("src/commonMain/assets")
    val sampleAssetsDir = projectDir.resolve("src/commonMain/assets")

    from(sharedAssetsDir)
    from(sampleAssetsDir)
    into(layout.buildDirectory.dir("compose/cocoapods/compose-resources"))
}

tasks.matching { it.name == "syncFramework" }.configureEach {
    finalizedBy(syncSharedAssetsToPodResources)
}
