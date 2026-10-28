# App structure

How a GearUI app is put together: the build, the platform hosts, the one root page, its
`Navigator`, routes, bottom tabs and screen state. The reference implementation is the
kit's own sample (`sample/` in github.com/gearui/gearui-kit) — copy host files from there
rather than writing them from memory.

## Contents

1. Build setup
2. Platform hosts
3. The root page
4. Routes and the Navigator
5. Bottom tabs
6. Screen state
7. Leaving a screen: results, confirmation, logout, deep links
8. File layout

## 1. Build setup

| | Version |
| --- | --- |
| Kotlin | **2.1.21** — must equal KuiklyUI's Kotlin; a mismatch fails at dependency resolution |
| KuiklyUI | 2.28.0, artifacts `com.tencent.kuikly-open:*:2.28.0-2.1.21` |
| Compose Multiplatform plugin | 1.7.3 |
| KSP | 2.1.21-2.0.1 (Kuikly's `@Page` processor) |
| Android | minSdk 21, compileSdk 36 |
| iOS | 15.0+ |

`settings.gradle.kts` — KuiklyUI 2.x is only on Tencent's mirror; without it nothing resolves:

```kotlin
pluginManagement {
    repositories {
        google(); gradlePluginPortal(); mavenCentral()
        maven("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
    }
}
dependencyResolutionManagement {
    repositories {
        google(); mavenCentral()
        maven("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
    }
}
```

Shared module `build.gradle.kts` (the shape of `sample/build.gradle.kts`):

```kotlin
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.compose")
    id("com.android.application")          // or com.android.library when a separate androidApp module hosts it
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp") version "2.1.21-2.0.1"
}

val kuikly = "2.28.0-2.1.21"

kotlin {
    androidTarget()
    iosX64(); iosArm64(); iosSimulatorArm64()
    js(IR) { browser(); binaries.executable() }   // only if you ship the Web

    cocoapods {
        ios.deploymentTarget = "15.0"
        podfile = project.file("iosApp/Podfile")
        framework { baseName = "shared"; isStatic = true }
    }

    sourceSets {
        commonMain.dependencies {
            implementation("com.gearui:gearui-kit:1.0.0-beta8")   // the root coordinate only
            implementation("com.tencent.kuikly-open:core:$kuikly")
            implementation("com.tencent.kuikly-open:core-annotations:$kuikly")
            implementation(compose.runtime)
        }
        androidMain.dependencies {
            implementation("com.tencent.kuikly-open:core-render-android:$kuikly")
            implementation("androidx.appcompat:appcompat:1.7.1")
        }
    }
}
```

Declare only `com.gearui:gearui-kit`; Gradle picks the per-target artifact. Kuikly's KSP
processor must run for each target (the sample wires `kspAndroid`, `kspIosArm64`, … —
copy that block).

## 2. Platform hosts

The host is a thin native shell that opens the one Kuikly page by name. Copy it from the
sample and change the page name; each line in it is there for a reason:

**Android** (`sample/src/androidMain/.../MainActivity.kt`):
- `WindowCompat.setDecorFitsSystemWindows(window, false)` — edge to edge, so the runtime's
  safe area is the only top padding.
- The IME inset is forwarded with `RuntimeInsetsBridge.updateKeyboardHeight(...)` from an
  `OnApplyWindowInsetsListener`; Kuikly does not report the keyboard on Android, and
  without it dropdowns open under it. Manifest: `android:windowSoftInputMode="adjustResize"`.
- A thread adapter with an 8 MB stack (`SampleThreadAdapter`): Kuikly composes on its own
  thread with a 1 MB default stack, and deep screens overflow it in debug builds.
- An image adapter (`SampleImageAdapter`) so `Image`/`Avatar` URLs load.
- `dispatchKeyEvent` forwards BACK to `kuiklyDelegator.onBackPressed()` so the Navigator
  pops page by page and the app exits only at the bottom of the stack.
- `kuiklyDelegator.onAttach(container, "", "MainPage", params)` — the name is the `@Page` name.

**iOS** (`sample/iosApp/`): `KuiklyRenderViewController` (Objective-C) hosted from SwiftUI.
- `Info.plist` must set `CADisableMinimumFrameDurationOnPhone = YES`; without it a
  ProMotion iPhone holds every kit animation to 60 Hz while scrolling runs at 120 Hz.
- Run the Gradle build (it writes the podspec and framework) **before** `pod install`;
  otherwise CocoaPods sees an empty resources directory and drops the copy phase.

**Web** (`sample/jsApp/`): the host must not carry a UMD wrapper (`webpack.config.d/output.js`);
with it the render bridge disappears and you get `callNative is not defined`.

## 3. The root page

One Kuikly page. It owns `App`, the app's own string provider and the `Navigator`.

```kotlin
@Page("MainPage")
class MainPage : View() {
    // This page mounts App itself (it drives theme and language), so the base class
    // must not wrap a second one around it.
    override fun autoWrapApp(): Boolean = false

    @Composable
    override fun Content() {
        AppRoot()
    }
}

@Composable
fun AppRoot() {
    val settings = AppSettings.current            // your own state: theme choice, language
    App(
        themeMode = settings.themeMode,           // ThemeMode.Light / Dark / System
        isSystemDark = settings.isSystemDark,     // from the platform layer
        theme = null,                             // or Themes.Light.withBrandAccent(brand) — see below
        languageTag = settings.languageTag,       // "zh-Hans", "en-US", … the ONLY place language is set
    ) {
        AppI18nProvider {                         // the app's typed strings (i18n-for-apps.md)
            AppNavigator()
        }
    }
}
```

A brand colour is the one place a literal colour belongs, once, at the root:

```kotlin
private val BrandPurple = Color(0xFF7C3AED)   // gearui-allow: GU101 brand accent, applied once at the root
val theme = (if (dark) Themes.Dark else Themes.Light).withBrandAccent(BrandPurple)
```

`withBrandAccent` swaps `primary`, its foreground, the soft pair and the focus ring and
leaves surfaces, shapes and type alone. Shapes, typography, elevation and motion are
separate `App` parameters; changing one never resets another.

## 4. Routes and the Navigator

Routes are a sealed interface; a route *is* its arguments.

```kotlin
sealed interface AppRoute : NavRoute {
    data object Shell : AppRoute { override val routeName = "shell" }
    data object Settings : AppRoute { override val routeName = "settings" }
    data class Article(val id: String) : AppRoute { override val routeName = "article" }
    data class EditProfile(val userId: String) : AppRoute {
        override val routeName = "edit_profile"
        override val options = NavOptions(transition = NavTransition.ModalSheet, presentation = NavPresentation.Modal)
    }
    data class Photo(val url: String) : AppRoute {
        override val routeName = "photo"
        override val options = NavOptions(transition = NavTransition.FadeIn, presentation = NavPresentation.Overlay)
    }
}

@Composable
fun AppNavigator() {
    val nav = rememberNavigatorController<AppRoute>(AppRoute.Shell)
    Navigator(controller = nav) { entry ->
        when (val route = entry.route) {
            AppRoute.Shell -> MainShell(onOpenArticle = { nav.push(AppRoute.Article(it)) }, onOpenSettings = { nav.push(AppRoute.Settings) })
            AppRoute.Settings -> SettingsScreen(onBack = { nav.pop() }, onOpenProfile = { nav.push(AppRoute.EditProfile("me")) })
            is AppRoute.Article -> ArticleScreen(id = route.id, onBack = { nav.pop() })
            is AppRoute.EditProfile -> EditProfileScreen(userId = route.userId, onDone = { nav.pop() })
            is AppRoute.Photo -> PhotoScreen(url = route.url, onClose = { nav.pop() })
        }
    }
}
```

- `push`, `pop`, `replace`, `popTo(route)` / `popTo { predicate }`, `resetTo(route)`.
- Swipe back follows the finger from anywhere on the page, on all four platforms; Android
  BACK pops. Do not add your own swipe or `BackHandler` for page navigation.
- Screens take callbacks (`onBack`, `onOpenX`) rather than the controller: they stay
  previewable and do not know about the stack. Inside the `Navigator` lambda the
  `EntryScope` also offers `controller`, `isForeground`, `retain` and `entryCoroutineScope`.
- Presentation: `Push` (default, slides in, swipe back), `Modal` (a full-screen task or
  form; no swipe back), `Overlay` (an immersive preview; closes straight back).
- Hoist a list's `rememberLazyListState()` above the `Navigator` when the list must keep
  its scroll position while a page sits on top of it (the sample does this for Home).

## 5. Bottom tabs

Tabs are one route (`Shell`); pages *opened from* a tab are pushed over the whole shell, so
the tab bar slides away with the shell as on iOS and WeChat.

```kotlin
@Composable
fun MainShell(onOpenArticle: (String) -> Unit, onOpenSettings: () -> Unit) {
    val strings = AppI18n.strings.tabs
    var tab by remember { mutableStateOf(0) }
    val items = listOf(
        BottomNavItem(id = "0", label = strings.home, icon = Icons.house, selectedIcon = Icons.house.filled),
        BottomNavItem(id = "1", label = strings.discover, icon = Icons.compass, selectedIcon = Icons.compass.filled),
        BottomNavItem(id = "2", label = strings.me, icon = Icons.user, selectedIcon = Icons.user.filled),
    )
    Column(Modifier.fillMaxSize()) {
        // TabHost keeps every visited tab alive and hides the others properly. A `when (tab)`
        // here destroys and rebuilds a whole native view tree on every switch (measured:
        // 35–43% janky frames); alpha or graphicsLayer alone do not hide on KuiklyUI.
        TabHost(selectedIndex = tab, count = items.size, modifier = Modifier.weight(1f)) { index ->
            when (index) {
                0 -> HomeTab(onOpenArticle)
                1 -> DiscoverTab(onOpenArticle)
                else -> MeTab(onOpenSettings)
            }
        }
        BottomNavBar(items = items, selectedId = tab.toString(), onSelect = { tab = it.toInt() })
    }
}
```

Each tab is a `PageScaffold` with its own `NavBar` (no back button) over a `GearLazyColumn`.
`BottomNavBar` consumes the bottom safe area itself. A tab is composed the first time it is
selected, so do not rely on an unvisited tab's `LaunchedEffect` to load anything early.

## 6. Screen state

| State | Where |
| --- | --- |
| UI-only (a toggle, a text field) | `remember { mutableStateOf(...) }` in the screen |
| Must survive the screen going off-screen or into the back stack | `retain { MyScreenState() }` from the `EntryScope` — a screen-scoped ViewModel, created once per entry, closed when the entry leaves (it is closed if `AutoCloseable`) |
| Loading work that should not restart when the page reappears | `entryCoroutineScope.launch { … }` |
| App-wide (session, settings, caches) | a plain object or class exposing `StateFlow`s, collected with `collectAsState()` |

```kotlin
AppRoute.Settings -> {
    val state = retain { SettingsState(repository) }   // EntryScope.retain
    SettingsScreen(state = state, onBack = { nav.pop() })
}
```

Do not put screen state at the app root just because it must outlive recomposition —
that is what `retain` is for.

## 7. Leaving a screen

**Results.** Pass a callback into the route's screen and call it before `pop()`, or update
shared app state the previous screen already observes. Routes carry inputs, not results.

**Unsaved changes.** The route always asks; the screen, which knows whether anything
changed, answers. A route never holds screen state.

```kotlin
data class EditProfile(val userId: String) : AppRoute {
    override val routeName = "edit_profile"
    override val options = NavOptions(onPopRequest = { PopDecision.Pending })   // BACK, swipe and pop() all ask
}

// In the Navigator lambda:
is AppRoute.EditProfile -> EditProfileScreen(
    userId = route.userId,
    leaving = nav.pendingPop?.key == entry.key,
    onLeave = { nav.confirmPendingPop() },
    onStay = { nav.cancelPendingPop() },          // without this the stack stays frozen
    onBack = { nav.pop() },
)

// In the screen:
LaunchedEffect(leaving) { if (leaving && !dirty) onLeave() }
ConfirmDialog(
    visible = leaving && dirty,
    title = strings.discardTitle, message = strings.discardMessage,
    confirmText = strings.discard, destructive = true,
    onConfirm = onLeave, onCancel = onStay,
)
```

**Logout / session reset.** `nav.resetTo(AppRoute.Login)` — it interrupts any transition or
pending confirmation, which is what a forced logout needs.

**Deep links.** Push the target route before the `Navigator`'s first frame (e.g. in a
`remember(link) { nav.push(...) }`); Shell stays underneath so back still works.

## 8. File layout

```
shared/src/commonMain/kotlin/<app>/
  MainPage.kt              @Page + App root
  navigation/AppRoute.kt   sealed routes
  navigation/AppNavigator.kt
  i18n/                    AppStrings, packs, AppI18nProvider (i18n-for-apps.md)
  theme/                   brand colour, custom ThemeSpec if any
  feature/<name>/          one screen per file, its state class beside it
```
