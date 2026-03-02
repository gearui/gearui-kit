plugins {
    // Kotlin version must match KuiklyUI's Kotlin version (2.1.21)
    kotlin("multiplatform") version "2.1.21" apply false
    kotlin("plugin.serialization") version "2.1.21" apply false
    kotlin("plugin.compose") version "2.1.21" apply false
    // Android Gradle Plugin
    id("com.android.library") version "8.7.2" apply false
    id("com.android.application") version "8.7.2" apply false
    id("org.jetbrains.compose") version "1.7.3" apply false
}

allprojects {
    group = providers.gradleProperty("POM_GROUP_ID").orElse("com.gearui").get()
    version = providers.gradleProperty("POM_VERSION").orElse("0.1.0-SNAPSHOT").get()
}
