import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("com.vanniktech.maven.publish")
}

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
            api("com.tencent.kuikly-open:compose:2.27.0-2.1.21")
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
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
    compileSdk = 34

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
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = false)
    // Sign only when a GPG key is configured. publishToMavenLocal must work without one.
    if (hasSigningKey) {
        signAllPublications()
    }
}
