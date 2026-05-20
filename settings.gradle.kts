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
// gearui-kit    - GearUI UI Components (depends on KuiklyUI)
// sample        - Demo business bundle (depends on gearui-kit)
// sample:h5App  - Web host that loads sample's nativevue2.js bundle
// ============================================================================

include(":gearui-kit")
include(":sample")
include(":sample:h5App")
