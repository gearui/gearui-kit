# GearUI Kit

[English](./README.md) | [简体中文](./README.zh-Hans.md)

A Kotlin Multiplatform UI component library built on Kuikly.

## Release Information

- Coordinates: `com.gearui:gearui-kit:1.0.0-beta1`
- Available on Maven Central since 2026-08-15 (first public release)
- Targets: Android, iOS (arm64 / simulator arm64 / x64), JS (browser)
- Website: [https://gearui.com](https://gearui.com)
- License: BSD 3-Clause License

## Author Information

- Author: `zoujiaqing`
- Email: `zoujiaqing@gmail.com`

## Quick Integration

### 1. Published Dependency (Recommended)

Released on Maven Central. Declare the single root coordinate — Gradle reads the
module metadata and resolves the per-target artifact (`-android`, `-js`,
`-iosarm64`, …) for whatever you are compiling. Never depend on those directly.

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

### 4. Release to Maven Central (Central Portal)

Publishing is wired through `com.vanniktech.maven.publish` and Sonatype Central Portal.
Set credentials and signing keys as Gradle properties or env vars:

```bash
export ORG_GRADLE_PROJECT_mavenCentralUsername=<central_portal_token_name>
export ORG_GRADLE_PROJECT_mavenCentralPassword=<central_portal_token_secret>
# base64 encoded, single line: gpg --export-secret-keys <fpr> | base64 | tr -d '\n'
export ORG_GRADLE_PROJECT_signingInMemoryKey=<base64_gpg_private_key>
export ORG_GRADLE_PROJECT_signingInMemoryKeyPassword=<gpg_passphrase>
```

Do **not** set `signingInMemoryKeyId` unless you mean a specific subkey; Gradle
searches subkeys only, so a master key id there makes every signing task fail
with "no configured signatory". See the notes in `gearui-kit/build.gradle.kts`.

Publish from macOS — the three iOS targets build nowhere else, and on Linux they
are silently missing from the upload rather than failing it.

```bash
./gradlew :gearui-kit:publishToMavenCentral
```

⚠️ With the plugin at 0.30.0 this task reports BUILD SUCCESSFUL **without
uploading anything**: it is a lifecycle task (`Skipping task ... as it has no
actions`) and the artifacts only reach `build/publish/staging/<uuid>/`. The
1.0.0-beta1 release was uploaded by posting that bundle to the Portal directly:

```bash
TOKEN=$(printf '%s:%s' "$USERNAME" "$PASSWORD" | base64)
curl -X POST -H "Authorization: Bearer $TOKEN" \
  -F "bundle=@build/publish/staging/<uuid>.zip" \
  "https://central.sonatype.com/api/v1/publisher/upload?name=com.gearui:gearui-kit:<version>&publishingType=USER_MANAGED"
# -> prints a deployment id; then poll:
curl -X POST -H "Authorization: Bearer $TOKEN" \
  "https://central.sonatype.com/api/v1/publisher/status?id=<deployment_id>"
```

`USER_MANAGED` stops at VALIDATED so the final Publish stays a human decision.
Always verify against the Portal rather than trusting Gradle's exit code.

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

## Supported Platforms

| Platform | Library | Sample | CI |
|---|---|---|---|
| Android | ✅ | ✅ | ✅ |
| iOS | ✅ | ✅ | ✅ |
| Web (H5) | ✅ | ✅ 75 of 76 demos | ✅ |
| HarmonyOS | ⚠️ scaffolding | ⚠️ scaffolding | — |

Web runs through KuiklyUI's web renderer; the one demo that fails is `Table`,
on a Kotlin/JS partial-linkage error in the sample's own demo file rather than
in the component. See [sample/jsApp/README.md](./sample/jsApp/README.md).

HarmonyOS is unbuilt scaffolding. It cannot be a target of the normal build:
the KuiklyUI artifacts carrying `ohosArm64` are published against Kotlin
`2.0.21-KBA-010`, so ohos uses a parallel build configuration selected with
`-c settings.ohos.gradle.kts`. See
[sample/ohosApp/README.md](./sample/ohosApp/README.md) for what is and is not
verified.

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
- Web host: [sample/jsApp/README.md](./sample/jsApp/README.md)
- HarmonyOS host: [sample/ohosApp/README.md](./sample/ohosApp/README.md)

Documentation is written in English first; `*.zh-Hans.md` files are the Chinese
counterparts. Code comments are English only — see
[docs/SPEC_CI_MAPPING.md](./docs/SPEC_CI_MAPPING.md) entry 18 for the check
that enforces it.

## Development Commands

```bash
# Build the library per platform
./gradlew :gearui-kit:compileDebugKotlinAndroid
./gradlew :gearui-kit:compileKotlinIosSimulatorArm64
./gradlew :gearui-kit:compileKotlinJs

# Run the sample
./gradlew :sample:installDebug                    # Android
./gradlew :sample:jsApp:jsBrowserDevelopmentRun   # Web, then open http://localhost:8081/

# HarmonyOS uses a parallel build configuration (unbuilt — see sample/ohosApp/README.md)
./gradlew -c settings.ohos.gradle.kts :sample:linkSharedDebugSharedOhosArm64

# Architecture guardrails — 16 checks, all runnable locally
for f in scripts/ci/check_*.sh; do "$f"; done
```

## License

BSD 3-Clause License — see [LICENSE](./LICENSE).
