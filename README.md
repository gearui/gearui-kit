# GearUI Kit

[English](./README.md) | [简体中文](./README.zh-Hans.md)

A Kotlin Multiplatform UI component library built on Kuikly.

## Release Information

- Coordinates: `com.gearui:gearui-kit:1.0.0-beta1`
- Website: [https://gearui.com](https://gearui.com)
- License: Apache License 2.0

## Author Information

- Author: `zoujiaqing`
- Email: `zoujiaqing@gmail.com`

## Quick Integration

### 1. Published Dependency (Recommended)

If the version is published to a remote repository (such as Maven Central):

```kotlin
repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.gearui:gearui-kit:1.0.0-beta1")
        }
    }
}
```

### 2. Local Development Dependency (`mavenLocal`)

First publish from the `gearui-kit` project to your local Maven repository:

```bash
./gradlew :gearui-kit:publishToMavenLocal
```

Then add it in your app project:

```kotlin
repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.gearui:gearui-kit:1.0.0-beta1")
        }
    }
}
```

### 3. In-Repo Module Dependency (During Development)

```kotlin
dependencies {
    implementation(project(":gearui-kit"))
}
```

### 4. Release to Maven Central (Sonatype)

Set credentials and signing keys in environment variables:

```bash
export OSSRH_USERNAME=your_sonatype_username
export OSSRH_PASSWORD=your_sonatype_password
export SIGNING_KEY_ID=your_gpg_key_id
export SIGNING_KEY='-----BEGIN PGP PRIVATE KEY BLOCK-----...'
export SIGNING_PASSWORD=your_gpg_passphrase
```

Then publish:

```bash
./gradlew :gearui-kit:publish
```

## Basic Usage

```kotlin
@Page("MainPage")
class MainPage : View() {
    @Composable
    override fun Content() {
        GearApp(themeMode = ThemeMode.System) {
            MainPageContent()
        }
    }
}

@Composable
private fun MainPageContent() {
    val colors = Theme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        Button(
            text = I18n.strings.buttonConfirm,
            theme = ButtonTheme.PRIMARY,
            onClick = {}
        )
    }
}
```

## Project Notes

- Component layer path: `gearui-kit/src/commonMain/kotlin/com/gearui/components`
- Current component directory scale: `50+` (source of truth is the code)
- Sample project: `sample/`

## Component Convergence Strategy

- For navigation, only the core entry is kept: `Tabs` (content switching).
- Accordion mode is unified into: `Collapse.Accordion` (no standalone `Accordion` component maintained).
- No synonymous wrapper components are kept, to avoid duplicate APIs and duplicate sample pages.

## Documentation Entry

- Architecture overview: [ARCHITECTURE.md](./ARCHITECTURE.md)
- Spec entry: [docs/SPEC.md](./docs/SPEC.md)

## Development Commands

```bash
# Build Android target
./gradlew :gearui-kit:compileDebugKotlinAndroid

# Build iOS target
./gradlew :gearui-kit:compileKotlinIosSimulatorArm64

# Run sample app (Android)
./gradlew :sample:installDebug
```

## License

Apache License 2.0
