plugins {
    // Kotlin version must match KuiklyUI's Kotlin version (2.1.21)
    kotlin("multiplatform") version "2.1.21" apply false
    kotlin("plugin.serialization") version "2.1.21" apply false
    kotlin("plugin.compose") version "2.1.21" apply false
    // Android Gradle Plugin
    id("com.android.library") version "8.7.2" apply false
    id("com.android.application") version "8.7.2" apply false
    id("org.jetbrains.compose") version "1.7.3" apply false
    // Public API surface freeze (SPEC 4.2 / 11.1 §5)
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.17.0"
    // Maven Central publish (Sonatype Central Portal + signing)
    id("com.vanniktech.maven.publish") version "0.30.0" apply false
}

allprojects {
    group = providers.gradleProperty("POM_GROUP_ID").orElse("com.gearui").get()
    version = providers.gradleProperty("POM_VERSION").orElse("0.1.0-SNAPSHOT").get()
}

apiValidation {
    // sample and its platform hosts are apps, not part of the public API surface.
    // ignoredProjects matches on project name, so the subprojects need naming too.
    ignoredProjects.addAll(listOf("sample", "jsApp"))

    // Freeze the KLib ABI for non-JVM targets (iOS) as well.
    // The dump is host-sensitive: iOS targets only dump on macOS;
    // CI runs `apiCheck` on macOS to enforce the full surface.
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
}
