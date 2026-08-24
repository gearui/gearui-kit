// Generates SampleBuildInfo.kt for the sample.
//
// Lives in its own script because the sample is built by two build files: the
// normal build.gradle.kts and build.ohos.gradle.kts, which exists because the
// KuiklyUI artifacts carrying ohosArm64 are published against a different
// Kotlin distribution. Anything the sample's sources need has to be in both,
// and duplicating it is how the two drift apart — the ohos build broke exactly
// this way the first time it was run after the generator was added.
//
// The About row used to hardcode "1.0.0" and had already drifted from the
// published version by the first release, which is why this is generated from
// the same property the POM is built from.
//
// Consumers apply this and wire the task into their source set:
//
//     apply(from = rootProject.file("gradle/sample-build-info.gradle.kts"))
//     kotlin.sourceSets.commonMain { kotlin.srcDir(tasks.named("generateSampleBuildInfo")) }

tasks.register("generateSampleBuildInfo") {
    val version = providers.gradleProperty("POM_VERSION").orElse("dev")
    val outputDir = layout.buildDirectory.dir("generated/sampleBuildInfo")
    inputs.property("version", version)
    outputs.dir(outputDir)
    doLast {
        val file = outputDir.get().asFile.resolve("com/gearui/sample/SampleBuildInfo.kt")
        file.parentFile.mkdirs()
        file.writeText(
            """
            package com.gearui.sample

            /** Generated from POM_VERSION by :sample:generateSampleBuildInfo. Do not edit. */
            object SampleBuildInfo {
                const val VERSION: String = "${version.get()}"
            }

            """.trimIndent()
        )
    }
}
