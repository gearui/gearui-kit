import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
}

val kuiklyVersion = "${providers.gradleProperty("KUIKLY_VERSION").get()}-${providers.gradleProperty("KUIKLY_KOTLIN").get()}"

kotlin {
    js(IR) {
        browser {
            webpackTask {
                outputFileName = "jsApp.js"
            }
            commonWebpackConfig {
                // Export nothing; see webpack.config.d/output.js for why this
                // host must not carry a UMD wrapper.
                output?.library = null

                // webpack's default is 8080, which is busy often enough to be
                // worth making overridable: -PwebPort=9000
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).copy(
                    port = (findProperty("webPort") as String?)?.toInt() ?: 8081,
                )
            }
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("com.tencent.kuikly-open.core-render-web:base:$kuiklyVersion")
                implementation("com.tencent.kuikly-open.core-render-web:h5:$kuiklyVersion")
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Wiring to :sample
//
// The host serves three things from one directory: its own jsApp.js, the
// business bundle built from :sample, and the assets that bundle references.
// Both of the latter are produced elsewhere, so they are copied in as build
// steps rather than left to a README telling people to copy files by hand —
// that is the kind of instruction that is followed once and then forgotten.
//
// They are staged into a generated directory that is registered as a resource
// source, not written straight into processedResources. Writing into another
// task's output directory works right up until task ordering changes, and
// Gradle rightly refuses it.
// ---------------------------------------------------------------------------

val stagedWebResources = layout.buildDirectory.dir("generated/webResources")

val copySampleJsBundle by tasks.registering(Copy::class) {
    description = "Stages the sample's JS bundle for the host to serve."
    dependsOn(":sample:jsBrowserDevelopmentWebpack")
    from(project(":sample").layout.buildDirectory.dir("kotlin-webpack/js/developmentExecutable")) {
        include("gearui_sample.js")
        include("gearui_sample.js.map")
    }
    into(stagedWebResources)
}

/**
 * Images resolve as `assets://<path>`, which the host image processor resolves
 * relative to its own page. GearUI's icons are drawn vectors, so only the sample's
 * own images (avatars, app icons) are staged here.
 */
val copySampleAssets by tasks.registering(Copy::class) {
    description = "Stages the sample's assets under the host page's assets directory."
    from(project(":sample").layout.projectDirectory.dir("src/commonMain/assets"))
    into(stagedWebResources.map { it.dir("assets") })
}

kotlin.sourceSets.named("jsMain") {
    // One source dir, not one per task: both staging tasks write into the same
    // tree, and registering it twice made processResources see every file twice.
    resources.srcDir(stagedWebResources)
}

tasks.named("jsProcessResources") {
    dependsOn(copySampleJsBundle, copySampleAssets)
}
