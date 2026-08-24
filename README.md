# GearUI Kit

[English](./README.md) | [简体中文](./README.zh-Hans.md)

A Kotlin Multiplatform UI component library built on Kuikly.

## Release Information

- Coordinates: `com.gearui:gearui-kit:1.0.0-beta1`
- Available on Maven Central since 2026-08-15 (first public release)
- Targets: Android, iOS (arm64 / simulator arm64 / x64), JS (browser), HarmonyOS (ohosArm64, separate build)
- Website: [https://gearui.com](https://gearui.com)
- License: BSD 3-Clause License

## Author Information

- Author: `zoujiaqing`
- Email: `zoujiaqing@gmail.com`

## Screenshots

Captured from the sample app on an iPhone 17 Pro simulator.

| Home (Chinese) | Home (English) | Settings (Light) | Settings (Dark) |
| --- | --- | --- | --- |
| <img src="docs/screenshots/home-zh.png" width="220" alt="Component index in Chinese" /> | <img src="docs/screenshots/home-en.png" width="220" alt="Component index in English" /> | <img src="docs/screenshots/settings-light.png" width="220" alt="Settings page, light theme" /> | <img src="docs/screenshots/settings-dark.png" width="220" alt="Settings page, dark theme" /> |

Language and theme are switched at runtime from the settings page; every
component follows both without any per-screen wiring.

## Components

<!-- component-index:begin -->
**72 components** in 6 categories. Every one of them ships a demo page in the sample app.

| Category | Components |
| --- | --- |
| Basic (7) | `Button`, `Icon`, `Link`, `Text`, `Tag`, `Badge`, `Divider` |
| Form (17) | `Input`, `Checkbox`, `Radio`, `Switch`, `Slider`, `Stepper`, `Textarea`, `Rate`, `Select`, `Picker`, `DatePicker`, `DropdownMenu`, `Upload`, `Form`, `Cascader`, `Transfer`, `TreeSelect` |
| Navigation (12) | `NavBar`, `BottomNavBar`, `Tabs`, `NavigationMenu`, `Sidebar`, `Drawer`, `Steps`, `Pagination`, `Breadcrumb`, `Anchor`, `Segmented`, `FAB` |
| Data display (15) | `List`, `Card`, `Cell`, `Table`, `Image`, `ImageViewer`, `Avatar`, `Collapse`, `Progress`, `Empty`, `Skeleton`, `Timeline`, `Tree`, `Calendar`, `Watermark` |
| Feedback (15) | `SwipeCell`, `ActionSheet`, `Toast`, `Dialog`, `Tooltip`, `ContextMenu`, `Loading`, `Message`, `NoticeBar`, `Notification`, `Snackbar`, `Popup`, `Popover`, `Result`, `Tour` |
| Layout (6) | `Grid`, `Swiper`, `SearchBar`, `Refresh`, `BottomSheet`, `BackTop` |

<details>
<summary>What each component does</summary>

**Basic**

| Component | Purpose |
| --- | --- |
| `Button` | Trigger actions |
| `Icon` | Icon display |
| `Link` | Link text |
| `Text` | Text display |
| `Tag` | Marking and classification |
| `Badge` | Message count indicator |
| `Divider` | Content separator |

**Form**

| Component | Purpose |
| --- | --- |
| `Input` | Text input |
| `Checkbox` | Multiple selection |
| `Radio` | Single selection |
| `Switch` | Toggle switch |
| `Slider` | Value selection |
| `Stepper` | Number stepper |
| `Textarea` | Multiline text input |
| `Rate` | Rating |
| `Select` | Dropdown selector |
| `Picker` | Multi-column picker |
| `DatePicker` | Date & time picker |
| `DropdownMenu` | Filter dropdown menu |
| `Upload` | File upload |
| `Form` | Form container |
| `Cascader` | Cascade selector |
| `Transfer` | Data transfer |
| `TreeSelect` | Tree selector |

**Navigation**

| Component | Purpose |
| --- | --- |
| `NavBar` | Page navigation bar |
| `BottomNavBar` | App bottom navigation |
| `Tabs` | Content switching |
| `NavigationMenu` | Top navigation menu |
| `Sidebar` | Side navigation |
| `Drawer` | Slide drawer |
| `Steps` | Step indicator |
| `Pagination` | Pagination navigation |
| `Breadcrumb` | Path navigation |
| `Anchor` | Page anchor navigation |
| `Segmented` | Segmented control |
| `FAB` | Floating action button |

**Data display**

| Component | Purpose |
| --- | --- |
| `List` | List display |
| `Card` | Card container |
| `Cell` | List cell component |
| `Table` | Data table |
| `Image` | Image display |
| `ImageViewer` | Image preview |
| `Avatar` | User avatar |
| `Collapse` | Content collapse |
| `Progress` | Progress display |
| `Empty` | Empty state |
| `Skeleton` | Loading placeholder |
| `Timeline` | Timeline display |
| `Tree` | Tree structure |
| `Calendar` | Calendar display |
| `Watermark` | Page watermark |

**Feedback**

| Component | Purpose |
| --- | --- |
| `SwipeCell` | Swipeable cell |
| `ActionSheet` | Bottom action sheet |
| `Toast` | Message toast |
| `Dialog` | Modal dialog |
| `Tooltip` | Tooltip |
| `ContextMenu` | Context menu |
| `Loading` | Loading state |
| `Message` | Global message |
| `NoticeBar` | Notice bar |
| `Notification` | Global notification |
| `Snackbar` | Bottom message |
| `Popup` | Popup content |
| `Popover` | Popover tooltip |
| `Result` | Operation result |
| `Tour` | Feature guide |

**Layout**

| Component | Purpose |
| --- | --- |
| `Grid` | Grid layout |
| `Swiper` | Content carousel |
| `SearchBar` | Search input |
| `Refresh` | Pull-to-refresh showcase |
| `BottomSheet` | Bottom sheet |
| `BackTop` | Back to top |

</details>
<!-- component-index:end -->

The table above is generated from `sample/.../config/ComponentConfig.kt` by
`scripts/gen_component_index.py`; a CI check fails when it goes stale.

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

## Basic Usage

```kotlin
@Page("MainPage")
class MainPage : View() {
    @Composable
    // View mounts the App root (theme, i18n, overlays, safe area) for you.
    // Override themeMode() / themeSpec() to change it; do not call App() here.
    override fun Content() {
        MainPageContent()
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
| HarmonyOS | ✅ | ✅ builds | — |

Web runs through KuiklyUI's web renderer; the one demo that fails is `Table`,
on a Kotlin/JS partial-linkage error in the sample's own demo file rather than
in the component. See [sample/jsApp/README.md](./sample/jsApp/README.md).

HarmonyOS is supported and builds end to end — Kotlin/Native → CMake NAPI glue
→ ArkTS → an installable HAP carrying both `libshared.so` and
`libkuikly_entry.so`. It cannot be a target of the normal build: the KuiklyUI
artifacts carrying `ohosArm64` are published against Kotlin `2.0.21-KBA-010`, so
ohos uses a parallel build configuration selected with
`-c settings.ohos.gradle.kts`:

```bash
./gradlew -c settings.ohos.gradle.kts :sample:linkSharedDebugSharedOhosArm64
```

The HAP has **not been launched on a device or emulator yet**, so nothing about
the UI is verified there — installing needs an emulator image and a signed
package, both behind a Huawei developer account. See
[sample/ohosApp/README.md](./sample/ohosApp/README.md) for the build steps and
exactly what remains.

## Project Notes

- Component layer path: `gearui-kit/src/commonMain/kotlin/com/gearui/components`
- Sample project: `sample/`

## Component Convergence Strategy

- For navigation, only the core entry is kept: `Tabs` (content switching).
- Accordion mode is unified into: `Collapse.Accordion` (no standalone `Accordion` component maintained).
- No synonymous wrapper components are kept, to avoid duplicate APIs and duplicate sample pages.

## Documentation Entry

- Architecture overview: [ARCHITECTURE.md](./ARCHITECTURE.md)
- Spec entry: [docs/SPEC.md](./docs/SPEC.md)
- Releasing (maintainers): [docs/RELEASING.md](./docs/RELEASING.md)
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

# Architecture guardrails — 18 checks, all runnable locally
for f in scripts/ci/check_*.sh; do "$f"; done
```

## License

BSD 3-Clause License — see [LICENSE](./LICENSE).
