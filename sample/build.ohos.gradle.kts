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

// Same generator the normal build uses; shared so the two build files cannot
// drift (the sample's sources reference SampleBuildInfo unconditionally).
apply(from = rootProject.file("gradle/sample-build-info.gradle.kts"))

// Version comes from gradle.properties; see the note there.
val kuiklyVersion = "${providers.gradleProperty("KUIKLY_VERSION").get()}-${providers.gradleProperty("KUIKLY_KOTLIN_OHOS").get()}"

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
        commonMain {
            kotlin.srcDir(tasks.named("generateSampleBuildInfo"))
        }
        commonMain.dependencies {
            implementation(project(":gearui-kit"))
            implementation("com.tencent.kuikly-open:core:$kuiklyVersion")
            implementation("com.tencent.kuikly-open:core-annotations:$kuiklyVersion")
        }
    }
}

dependencies {
    // Generates the ohos page entry from @Page. core-ksp has an
    // OhOsTargetEntryBuilder, so unlike the JS target this is a first-class
    // path rather than something that happens to work.
    add("kspOhosArm64", "com.tencent.kuikly-open:core-ksp:$kuiklyVersion")
}

ksp {
    arg("catchException", "false")
}
