// sample for HarmonyOS.
//
// Mirrors the normal sample build, minus the Android and iOS targets: this
// configuration exists only to produce the shared library the ArkTS host
// loads. Sources are the same commonMain — the sample's one expect
// declaration, StatusBarControllerImpl, gets an ohos actual under
// src/ohosArm64Main.
plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
}

kotlin {
    // The host loads this as libshared.so — see EntryAbilityStage.ets, which
    // calls setup("libshared.so"). Renaming the binary means renaming it there
    // too.
    ohosArm64 {
        binaries.sharedLib("shared") {
            freeCompilerArgs += "-Xadd-light-debug=enable"
            linkerOpts += "--build-id=sha1"
        }
    }

    targets.all {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs += listOf(
                    "-opt-in=kotlin.ExperimentalStdlibApi",
                    "-opt-in=kotlinx.cinterop.ExperimentalForeignApi",
                    "-opt-in=kotlin.experimental.ExperimentalNativeApi",
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
            implementation(project(":gearui-kit"))
            implementation("com.tencent.kuikly-open:core:2.25.0-2.0.21-ohos")
            implementation("com.tencent.kuikly-open:core-annotations:2.25.0-2.0.21-ohos")
        }
    }
}

dependencies {
    // Generates the ohos page entry from @Page. core-ksp has an
    // OhOsTargetEntryBuilder, so unlike the JS target this is a first-class
    // path rather than something that happens to work.
    add("kspOhosArm64", "com.tencent.kuikly-open:core-ksp:2.25.0-2.0.21-ohos")
}

ksp {
    arg("catchException", "false")
}
