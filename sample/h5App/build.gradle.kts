plugins {
    kotlin("multiplatform")
}

kotlin {
    js(IR) {
        moduleName = "h5App"
        browser {
            webpackTask {
                outputFileName = "h5App.js"
            }
            commonWebpackConfig {
                output?.library = null
                devtool = "source-map"
            }
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("com.tencent.kuikly-open.core-render-web:base:2.19.0-2.1.21")
                implementation("com.tencent.kuikly-open.core-render-web:h5:2.19.0-2.1.21")
            }
        }
    }
}
