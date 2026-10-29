plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("com.android.library")
    id("org.jetbrains.compose")
}

// Optional GearUI module: Markdown rendering on KuiklyMarkdown's Compose renderer. Kept out of
// gearui-kit so the core still depends only on the KuiklyUI runtime; apps opt in.
val kuiklyVersion = "${providers.gradleProperty("KUIKLY_VERSION").get()}-${providers.gradleProperty("KUIKLY_KOTLIN").get()}"
val kuiklyMarkdownVersion = "${providers.gradleProperty("KUIKLY_MARKDOWN_VERSION").get()}-${providers.gradleProperty("KUIKLY_KOTLIN").get()}"

kotlin {
    androidTarget {
        compilations.all { kotlinOptions { jvmTarget = "1.8" } }
    }
    js(IR) { browser() }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":gearui-kit"))
            api("com.tencent.kuikly-open:compose:$kuiklyVersion")
            api("com.tencent.kuiklybase:KuiklyMarkdownCompose:$kuiklyMarkdownVersion")
            // KuiklyMarkdown expects the host to provide these.
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
            implementation(compose.runtime)
        }
    }
}

android {
    namespace = "com.gearui.markdown"
    compileSdk = 36
    defaultConfig { minSdk = 21 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
