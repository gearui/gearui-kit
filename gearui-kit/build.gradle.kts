import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.Sign

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("com.android.library")
    id("org.jetbrains.compose")
    `maven-publish`
    signing
}

kotlin {
    // Android target
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

    // iOS targets
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // Common compiler options
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
        // Common dependencies
        commonMain.dependencies {
            // ============================================================================
            // GearUI-KuiklyUI depends ONLY on KuiklyUI Runtime
            // ============================================================================

            // KuiklyUI Compose Runtime
            api("com.tencent.kuikly-open:compose:2.15.2-2.1.21")

            // Compose dependencies (needed for UI components)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
        }

        // Android-specific dependencies
        androidMain.dependencies {
            implementation("androidx.annotation:annotation:1.9.1")
        }

        // iOS-specific dependencies
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

    // Configure assets directory for shared framework icons/resources
    sourceSets {
        named("main") {
            assets.srcDirs("src/commonMain/assets")
        }
    }
}

publishing {
    repositories {
        maven {
            name = "sonatype"
            val releasesRepoUrl = providers.gradleProperty("SONATYPE_RELEASE_URL")
                .orElse("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                .get()
            val snapshotsRepoUrl = providers.gradleProperty("SONATYPE_SNAPSHOT_URL")
                .orElse("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                .get()
            url = uri(
                if (project.version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            )
            credentials {
                username = providers.gradleProperty("OSSRH_USERNAME")
                    .orElse(providers.environmentVariable("OSSRH_USERNAME"))
                    .orNull
                password = providers.gradleProperty("OSSRH_PASSWORD")
                    .orElse(providers.environmentVariable("OSSRH_PASSWORD"))
                    .orNull
            }
        }
    }

    publications.withType<MavenPublication>().configureEach {
        groupId = providers.gradleProperty("POM_GROUP_ID").orElse(project.group.toString()).get()
        version = providers.gradleProperty("POM_VERSION").orElse(project.version.toString()).get()
        if (name == "kotlinMultiplatform") {
            artifactId = providers.gradleProperty("POM_ARTIFACT_ID").orElse("kit").get()
        }
        pom {
            name.set(providers.gradleProperty("POM_NAME").orElse("gearui-kit"))
            description.set(
                providers.gradleProperty("POM_DESCRIPTION").orElse("GearUI Kotlin Multiplatform UI Kit")
            )
            url.set(providers.gradleProperty("POM_URL").orElse("https://gearui.com"))
            licenses {
                license {
                    name.set(
                        providers.gradleProperty("POM_LICENSE_NAME")
                            .orElse("Apache License, Version 2.0")
                    )
                    url.set(
                        providers.gradleProperty("POM_LICENSE_URL")
                            .orElse("https://www.apache.org/licenses/LICENSE-2.0")
                    )
                }
            }
            organization {
                name.set(
                    providers.gradleProperty("POM_ORGANIZATION_NAME")
                        .orElse("Shanghai Boyu Information Technology Co., Ltd.")
                )
                url.set(providers.gradleProperty("POM_ORGANIZATION_URL").orElse("https://gearui.com"))
            }
            developers {
                developer {
                    id.set(providers.gradleProperty("POM_DEVELOPER_ID").orElse("zoujiaqing"))
                    name.set(providers.gradleProperty("POM_DEVELOPER_NAME").orElse("zoujiaqing"))
                    email.set(
                        providers.gradleProperty("POM_DEVELOPER_EMAIL")
                            .orElse("zoujiaqing@gmail.com")
                    )
                    organization.set(
                        providers.gradleProperty("POM_ORGANIZATION_NAME")
                            .orElse("Shanghai Boyu Information Technology Co., Ltd.")
                    )
                }
            }
            scm {
                url.set(
                    providers.gradleProperty("POM_SCM_URL")
                        .orElse("https://github.com/gearui/gearui-kit")
                )
                connection.set(
                    providers.gradleProperty("POM_SCM_CONNECTION")
                        .orElse("scm:git:https://github.com/gearui/gearui-kit.git")
                )
                developerConnection.set(
                    providers.gradleProperty("POM_SCM_DEV_CONNECTION")
                        .orElse("scm:git:ssh://git@github.com/gearui/gearui-kit.git")
                )
            }
        }
    }
}

signing {
    val signingKeyId = providers.gradleProperty("SIGNING_KEY_ID")
        .orElse(providers.environmentVariable("SIGNING_KEY_ID"))
        .orNull
    val signingKey = providers.gradleProperty("SIGNING_KEY")
        .orElse(providers.environmentVariable("SIGNING_KEY"))
        .orNull
    val signingPassword = providers.gradleProperty("SIGNING_PASSWORD")
        .orElse(providers.environmentVariable("SIGNING_PASSWORD"))
        .orNull

    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        sign(publishing.publications)
    }
}

tasks.withType<Sign>().configureEach {
    onlyIf {
        (name.contains("publish", ignoreCase = true) || name.contains("sign", ignoreCase = true)) &&
            !name.contains("MavenLocal")
    }
}
