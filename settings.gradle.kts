pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
        }
    }
}

rootProject.name = "GearUI-Kit"

// ============================================================================
// Module Structure (Framework Layer Design)
// ============================================================================
// gearui-kit  - GearUI UI Components (depends on KuiklyUI)
// sample     - Demo App (depends on gearui-kit)
// sample:jsApp - Web (H5) host that loads sample's JS bundle
// ============================================================================

include(":gearui-kit")
include(":sample")
// Web (H5) host for the sample. Kept inside the main build rather than as a
// standalone one so it can depend on :sample's JS bundle directly instead of
// asking anyone to copy files between two builds by hand.
include(":sample:jsApp")
