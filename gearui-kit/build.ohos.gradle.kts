// gearui-kit for HarmonyOS. Same sources as the normal build, compiled against
// the ohos-flavoured Kuikly artifacts. The one expect/actual in the library
// (calendar's wall clock) has its ohos actual under src/ohosArm64Main; a new
// expect without an ohosArm64 actual breaks only this build, so add both.
plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

// Version comes from gradle.properties; see the note there.
val kuiklyVersion = "${providers.gradleProperty("KUIKLY_VERSION").get()}-${providers.gradleProperty("KUIKLY_KOTLIN_OHOS").get()}"

kotlin {
    ohosArm64()

    targets.all {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs += listOf(
                    "-opt-in=kotlin.ExperimentalStdlibApi",
                    "-opt-in=kotlinx.cinterop.ExperimentalForeignApi",
                    "-opt-in=kotlin.experimental.ExperimentalNativeApi",
                    // The Compose compiler refuses Kotlin 2.0.21-KBA-010 as an
                    // unrecognised version without this; KuiklyUI passes the
                    // same flag for the same reason.
                    "-P", "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true",
                )
            }
        }
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
        commonMain.dependencies {
            api("com.tencent.kuikly-open:compose:$kuiklyVersion")
        }
    }
}
