plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("com.vanniktech.maven.publish")
}

// Version comes from gradle.properties; see the note there.
val kuiklyVersion = "${providers.gradleProperty("KUIKLY_VERSION").get()}-${providers.gradleProperty("KUIKLY_KOTLIN").get()}"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs += listOf(
                    "-Xjvm-default=all",
                    "-opt-in=kotlin.RequiresOptIn"
                )
            }
        }
    }

    js(IR) {
        browser()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    targets.all {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs += listOf(
                    "-opt-in=kotlin.RequiresOptIn"
                )
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            // GearUI-KuiklyUI depends ONLY on KuiklyUI Runtime
            api("com.tencent.kuikly-open:compose:$kuiklyVersion")
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            // 1.10.0 common metadata requires Kotlin 2.3.20; Kuikly uses 2.1.21.
            implementation("androidx.annotation:annotation:1.9.1")
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain.get())
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    namespace = "com.gearui.kit"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        named("main") {
            assets.srcDirs("src/commonMain/assets")
        }
    }
}

// =============================================================================
// Maven Central publishing via vanniktech.maven.publish
// =============================================================================
// Credentials (env or ~/.gradle/gradle.properties):
//   ORG_GRADLE_PROJECT_mavenCentralUsername    Central Portal user token name
//   ORG_GRADLE_PROJECT_mavenCentralPassword    Central Portal user token secret
//   ORG_GRADLE_PROJECT_signingInMemoryKey      GPG private key, base64 encoded
//   ORG_GRADLE_PROJECT_signingInMemoryKeyPassword
//
// Two things about the key that cost an afternoon:
//
//   Store it base64 encoded on a single line:
//       gpg --export-secret-keys <fpr> | base64 | tr -d '\n'
//   The ASCII-armored form has to survive Java Properties escaping (every
//   newline written as a literal \n), and getting that wrong fails late and
//   unhelpfully with "Could not read PGP secret key".
//
//   Do NOT set signingInMemoryKeyId unless you mean a specific subkey.
//   Gradle's useInMemoryPgpKeys(keyId, ...) searches subkeys only, so passing
//   the master key's short id finds nothing and every signing task dies with
//   "no configured signatory". Omitted, it uses the primary key.
//
// Publish:
//   ./gradlew :gearui-kit:publishToMavenCentral           (manual close + release on Portal)
//   ./gradlew :gearui-kit:publishAndReleaseToMavenCentral (auto-release after upload)
//
// Publish from macOS: the three iOS targets only build there, and on Linux
// they are silently absent from the upload rather than failing it.
val hasSigningKey = !providers
    .gradleProperty("signingInMemoryKey")
    .orElse(providers.environmentVariable("ORG_GRADLE_PROJECT_signingInMemoryKey"))
    .orNull
    .isNullOrBlank()

// vanniktech.maven.publish auto-reads POM_* properties from gradle.properties
// (POM_GROUP_ID, POM_ARTIFACT_ID, POM_VERSION, POM_NAME, POM_DESCRIPTION, POM_URL,
//  POM_LICENSE_*, POM_DEVELOPER_*, POM_SCM_*, POM_ORGANIZATION_*).
// No explicit pom { } block is needed; redundant configuration would duplicate
// license / developer entries in the generated POM.
mavenPublishing {
    publishToMavenCentral(automaticRelease = false)
    // Sign only when a GPG key is configured. publishToMavenLocal must work without one.
    if (hasSigningKey) {
        signAllPublications()
    }
}

// =============================================================================
// iOS Kotlin tests need the real Kuikly host
// =============================================================================
// Kuikly's compose runtime calls into its CocoaPods host: OpenKuiklyIOSRender's
// KuiklyRenderThreadBridge.m implements _com_tencent_kuikly_IsCurrentOnContextThread.
// A bare Kotlin/Native test executable has no host, so its link step fails with
// that undefined symbol. The gate links the real framework and never a stub:
// build OpenKuiklyIOSRender once from the sample Pods project, then point the
// test link at that products directory (scripts/ios_native_tests.sh does both).
//
//   -PgearuiIosTestHostDir=<.../Build/Products/Debug-iphonesimulator>
//   GEARUI_IOS_TEST_HOST_DIR=<same>
//
// Without it the native test link fails fast with this explanation instead of
// a raw linker error. Main binaries and the published klib are unaffected.
val iosTestHostDir = providers.gradleProperty("gearuiIosTestHostDir")
    .orElse(providers.environmentVariable("GEARUI_IOS_TEST_HOST_DIR"))

kotlin.targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
    binaries.withType<org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable>().configureEach {
        val hostDir = iosTestHostDir.orNull?.trimEnd('/')
        if (hostDir != null) {
            linkerOpts(
                "-F$hostDir/OpenKuiklyIOSRender",
                "-F$hostDir/SDWebImage",
                "-framework", "OpenKuiklyIOSRender",
                "-rpath", "$hostDir/OpenKuiklyIOSRender",
                "-rpath", "$hostDir/SDWebImage",
            )
        } else {
            linkTaskProvider.configure {
                doFirst {
                    throw GradleException(
                        "iOS Kotlin tests need the Kuikly host framework. Run " +
                            "scripts/ios_native_tests.sh, or build OpenKuiklyIOSRender from " +
                            "sample/iosApp/Pods and pass -PgearuiIosTestHostDir=<Debug-iphonesimulator dir>."
                    )
                }
            }
        }
    }
}
