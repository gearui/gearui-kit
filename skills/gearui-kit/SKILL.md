---
name: gearui-kit
description: Build Kotlin Multiplatform apps (Android, iOS, Web, HarmonyOS) on GearUI Kit and KuiklyUI that follow the GearUI standard — one App root, typed Navigator routes, kit components only, theme tokens only, typed i18n — and check the code with the bundled checker. Use whenever a project depends on com.gearui:gearui-kit or imports com.gearui, whenever the user mentions GearUI, GearUI Kit, gearui-kit, Kuikly or KuiklyUI, and for any screen, page, form, list, dialog, sheet, tab bar, navigation, theme or language work in such a project, even if the kit is not named. 用 GearUI Kit / Kuikly 开发 App、页面、界面、表单、列表、弹窗、导航、主题、多语言时都用这个 skill。
---

# GearUI Kit

GearUI Kit is a Kotlin Multiplatform UI kit on KuiklyUI: one Compose codebase renders
to real native views on iOS, Android, the Web and HarmonyOS. The kit's components,
tokens and runtime already solve the hard parts — press feedback, safe areas, keyboard,
overlays, swipe back, dark mode, accessibility, language switching. An app that follows
the standard gets all of that for free; an app that rebuilds any of it by hand gets
something that looks close and breaks on a device.

So the job is to **compose the kit, not to imitate it**. When you are about to draw a
button, a row, a divider or a dialog yourself, stop: the kit has it, and its version
works on all four platforms.

## The standard in six rules

Each rule exists because the hand-made alternative fails on KuiklyUI in a specific way.

1. **One `@Page`, one `App`, one `Navigator`.** Every screen is a typed `NavRoute`
   pushed on one `Navigator`. No `when(currentPage)`, no Kuikly page per screen
   (`RouterModule.openPage`), no second `App`. Only this gives finger-tracking swipe
   back on all four platforms, Android BACK page by page, and a theme or language
   change that reaches every screen at once.
2. **Kit components only.** A tappable thing is a `Button`, a `Cell`, a `Link`, a row
   with `Modifier.rowClickable`, or content in `PressableFeedback`. On KuiklyUI
   `LocalIndication` never reaches the native view, so a bare `.clickable` shows no
   press at all. Lists are `GearLazyColumn` (it dismisses overlays and the keyboard on
   scroll and keeps a focused field above the keyboard). Icons are `Icons.*` — never
   emoji or symbols.
3. **Tokens only.** `val colors = Theme.colors` first; spacing from `Spacing`, corners
   from `Theme.shapes`, borders from `BorderWidth`, icon sizes from `IconSizes`, text
   from `Theme.typography`. No `Color(0x…)`, no `16.dp`, no `fontSize`. Light, dark,
   brand accent and square-corner themes then restyle every screen with no per-screen
   code.
4. **Pages sit in `PageScaffold`.** It consumes the safe area from a stabilised runtime
   value; `NavBar`, `BottomNavBar` and sheets consume their own edges. Never read
   `safeAreaInsets` yourself.
5. **Every visible string comes from a typed string pack.** Language is set once on
   `App(languageTag = …)`; the app's own `AppStrings` pack follows it. A literal is
   wrong in every language but one.
6. **Floating UI goes through the overlay runtime.** `ConfirmDialog`, `BottomSheet`,
   `ActionSheet.showList`, `Toast.show`, `Popover`, `Select`… — never a hand-made
   scrim `Box`. The runtime owns dismissal (outside tap, Back, route change, scroll).

`scripts/check_gearui_app.py` enforces these mechanically. Its rule IDs (GU001…) are
listed in `references/standard.md` with the reasoning behind each one.

## Workflow

1. **Orient.** Find the `@Page` class, the `App(...)` call and the `Navigator`.
   - No GearUI project yet → read `references/app-structure.md` (Gradle setup, hosts,
     the App + Navigator + routes skeleton, bottom tabs).
   - Existing project → add a route to its sealed route type and a branch to its
     route `when`; put the screen in its own file.
2. **Pick components** from `references/components.md` (the full catalogue). For the
   exact parameters open the `references/api/*.md` file it names — signatures there
   are generated from the kit's source, so copy them rather than guess. Choosing
   between similar components: see "Which component" below.
3. **Write the screen** from the template below. App text goes into the app's string
   pack (`references/i18n-for-apps.md`).
4. **Check.** Run the checker on every file you touched and fix every error:
   ```bash
   python3 <this-skill>/scripts/check_gearui_app.py path/to/src/commonMain
   ```
   A genuine exception gets `// gearui-allow: GU101 <reason>` on that line; no reason,
   no exception.
5. **Build** at least one target, e.g. `./gradlew :<module>:compileKotlinIosSimulatorArm64`
   or `:<module>:compileDebugKotlinAndroid`. The checker proves the standard, not that
   the code compiles.
6. **Report** what you ran and what you did not (compile ≠ verified on a device).

## Imports

KuiklyUI's Compose lives under **`com.tencent.kuikly.compose.*`**. Only the runtime is
`androidx.compose.runtime.*`. Importing `androidx.compose.foundation…` or `…ui…` either
fails to resolve or pulls in a second, unrendered Compose.

```kotlin
import androidx.compose.runtime.*                                   // Composable, remember, mutableStateOf, getValue/setValue, LaunchedEffect
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.unit.dp                        // only for named constants
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*               // Column, Row, Box, Spacer, Arrangement, PaddingValues, padding, fillMaxWidth …
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.draw.clip

import com.gearui.App                                               // the root
import com.gearui.View                                              // @Page base class
import com.gearui.theme.Theme                                       // Theme.colors / .shapes / .typography
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.typography.IconSizes
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.GearLazyColumn
import com.gearui.foundation.list.rowClickable
import com.gearui.components.icon.*                                 // Icons.house … (each icon is an extension property)
import com.gearui.components.scaffold.PageScaffold
import com.gearui.components.navbar.NavBar
import com.gearui.components.button.*                               // Button, ButtonTheme, ButtonType, ButtonSize
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.navigation.*                                      // Navigator, NavRoute, rememberNavigatorController, TabHost
import com.gearui.i18n.I18n                                         // the kit's own copy: I18n.strings.common.confirm …
```

Every other component is `com.gearui.components.<lowercased name>.*`
(`components.input.Input`, `components.dialog.ConfirmDialog`, `components.toast.Toast`);
`references/components.md` gives the file and the API pages give the package of each.

## A screen

A pushed page: `PageScaffold` + `NavBar` + `GearLazyColumn`, everything from tokens.

```kotlin
@Composable
fun SettingsScreen(onBack: () -> Unit, onOpenProfile: () -> Unit) {
    val colors = Theme.colors
    val strings = AppI18n.strings.settings            // the app's own typed pack
    var notifications by remember { mutableStateOf(true) }
    val safeBottom = LocalRuntimeEnvironment.current.safeArea.bottom

    PageScaffold(backgroundColor = colors.background, topSafeAreaColor = colors.surface) {
        Column(Modifier.fillMaxSize()) {
            NavBar(
                title = strings.title,
                useDefaultBack = true,
                onBackClick = onBack,
                backgroundColor = colors.surface,
            )
            GearLazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Spacing.lg, top = Spacing.lg, end = Spacing.lg,
                    bottom = Spacing.xxl + safeBottom,
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.xl),
            ) {
                item {
                    CellGroup(items = listOf(0, 1), title = strings.general) { row ->
                        when (row) {
                            0 -> Cell(title = strings.profile, arrow = true, onClick = onOpenProfile)
                            else -> Cell(
                                title = strings.notifications,
                                trailing = { Switch(checked = notifications, onCheckedChange = { notifications = it }) },
                            )
                        }
                    }
                }
            }
        }
    }
}
```

The route that shows it lives in the app's `Navigator` (`references/app-structure.md`):
`AppRoute.Settings -> SettingsScreen(onBack = { nav.pop() }, onOpenProfile = { nav.push(AppRoute.Profile) })`.

A `Cell` inside a `CellGroup` names the `Switch` beside it for screen readers, so the
bare control needs no `contentDescription` there.

## Tokens at a glance

| Need | Use |
| --- | --- |
| Page background / text | `colors.background` / `colors.foreground` |
| Card, row, bar surface | `colors.surface` (+ `surfaceForeground`), `colors.card` |
| Secondary text | `colors.mutedForeground` |
| Brand action | `colors.primary` / `primaryForeground`; tinted: `primarySoft` / `primarySoftForeground` |
| Status | `destructive`, `success`, `warning`, `info` (each with `…Foreground`, and `…Soft` fills) |
| Lines | `colors.separator`, `colors.border` |
| Spacing | `Spacing.xs 4 · sm 8 · md 12 · lg 16 · xl 24 · xxl 32 · xxxl 40 · huge 48 · massive 64` |
| Corners | `Theme.shapes.sm 8 · md 12 · lg 14 · controlLarge 16 · xl 24 · full` (a `Shape`); `Radius.*` for a `Dp` |
| Borders | `BorderWidth.hairline 0.5 · thin 1 · thick 2` |
| Icons | `IconSizes.Default.xs 12 … xl 24` beside text; `IconSizes.Display.sm 28 · md 36 · lg 40` |
| Text | `Theme.typography.titleLarge … bodyMedium (default) … caption, label, markMedium (bold body)` |
| Shadow | `Theme.elevation.raised 4 · floating 6 · modal 8` — usually let `Card` own it |

A dimension that is the content itself (an image's height) is a **named constant** at
the top of the file, not a bare `dp` in the middle of a layout.

## Which component

| Need | Use | Not |
| --- | --- | --- |
| Main action / secondary action | `Button(theme = PRIMARY)` / `Button(type = OUTLINE or TEXT)` | a styled `Box` |
| A settings or menu list | `CellGroup` of `Cell`s | rows with dividers drawn by hand |
| A row of your own layout (chat, order) | `Modifier.rowClickable(onClick, background)` | `.clickable` |
| Text entry | `Input` / `Textarea`; on a card `variant = FieldVariant.SECONDARY` | `BasicTextField` |
| A form with validation | `Form` + `FormItem` (or `TypedForm`) | ad-hoc error `Text`s |
| 2–5 options all visible | `Radio` group / `SegmentedControl` | a dropdown |
| ~10 or fewer options | `Select` | a wheel |
| Long list found by typing | `ComboBox` | a scrolling `Select` |
| Province/city/district | `Picker.Linked` in a sheet | chained `Select`s |
| Any-depth hierarchy | `Cascader` | `Picker.Linked` |
| Date / time | `DatePickerInput` / `TimePickerInput` with `precision`; a range: `Calendar` | typed date fields |
| On/off | `Switch` (in a `Cell`'s `trailing`) | a checkbox |
| Ask before something destructive | `ConfirmDialog(destructive = true)` | a custom dialog |
| A few actions on an item | `ActionSheet.showList(...)` | nested menus |
| Brief confirmation | `Toast.show / success / error` | a dialog |
| Message with an action (Undo) | `Snackbar` | a toast |
| Persistent inline status | `Alert` / `NoticeBar` | a toast |
| Nothing to show | `EmptyState` / `EmptyStatePreset` | a lone `Text` |
| Loading | `Loading`, `Skeleton`; pull to refresh: `rememberPullRefreshState` + `pullRefreshItem`; paging: `LoadMoreFooter` | a spinner in a `Box` |
| Bottom tabs | `BottomNavBar` + `TabHost` | `when(tab)` |
| Top tabs / segmented content | `Tabs` / `SegmentedControl` | hand-made underline |
| Search | `SearchBar` | an `Input` with an icon |
| Avatar, user line | `Avatar`, `User` | a clipped `Image` + `Text` |

## Before you finish

- The checker reports **0 errors** on everything you wrote.
- At least one target compiles.
- Every new string is in the app's string pack, in every language the app ships.
- Nothing hand-made where the kit has a component (re-read the table above).
- You said plainly what you verified and what you did not.

## References

| File | Read when |
| --- | --- |
| `references/app-structure.md` | Starting an app, adding the App root, Navigator, routes, bottom tabs, screen state |
| `references/standard.md` | The full standard with every checker rule and why it exists |
| `references/i18n-for-apps.md` | Adding the app's own string pack, language switching |
| `references/kuikly-pitfalls.md` | Something renders or behaves differently from Jetpack Compose |
| `references/components.md` | Finding a component |
| `references/api/*.md` | Exact signatures (generated from the kit source) |
| `references/icons.txt` | Icon names (`grep -i heart references/icons.txt`) |
