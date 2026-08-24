// Root build for the HarmonyOS configuration. Versions follow KuiklyUI's
// build.2.0.ohos.gradle.kts: the Kotlin distribution is the one its ohos
// artifacts were compiled against, and AGP/KSP are pinned to what that Kotlin
// version supports.
plugins {
    kotlin("multiplatform") version "2.0.21-KBA-010" apply false
    kotlin("plugin.compose") version "2.0.21-KBA-010" apply false
    id("com.android.library") version "7.4.2" apply false
    id("org.jetbrains.compose") version "1.7.3" apply false
    // KSP is versioned against the Kotlin compiler, so this is the pairing for
    // 2.0.21 rather than the 2.1.21-2.0.1 the normal build uses.
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
}

allprojects {
    group = providers.gradleProperty("POM_GROUP_ID").orElse("com.gearui").get()
    version = providers.gradleProperty("POM_VERSION").orElse("0.1.0-SNAPSHOT").get()
}
