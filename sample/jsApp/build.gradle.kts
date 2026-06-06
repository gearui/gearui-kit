plugins {
    kotlin("multiplatform")
}

kotlin {
    js(IR) {
        browser {
            webpackTask {
                outputFileName = "jsApp.js"
            }
            commonWebpackConfig {
                output?.library = null
            }
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("com.tencent.kuikly-open.core-render-web:base:2.21.0-2.1.21")
                implementation("com.tencent.kuikly-open.core-render-web:h5:2.21.0-2.1.21")
            }
        }
    }
}
