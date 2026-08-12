plugins {
    kotlin("multiplatform")
}

val kuiklyVersion = "2.25.0-2.1.21"

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
 * Icons resolve as `assets://icons/<name>.png`, which the web renderer turns
 * into `/assets/icons/<name>.png`. Both gearui-kit and sample contribute
 * assets, and gearui-kit's are the ones that matter — that is where the 101
 * icon PNGs live.
 */
val copySampleAssets by tasks.registering(Copy::class) {
    description = "Stages gearui-kit and sample assets under /assets."
    from(project(":gearui-kit").layout.projectDirectory.dir("src/commonMain/assets"))
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
