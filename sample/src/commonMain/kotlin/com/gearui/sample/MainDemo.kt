package com.gearui.sample

import com.gearui.navigation.NavOptions
import com.gearui.navigation.NavRoute
import com.gearui.navigation.Navigator
import com.gearui.navigation.rememberNavigatorController
import com.gearui.sample.config.ComponentConfig
import com.gearui.sample.perf.StartupMark
import com.gearui.sample.perf.MarkStartupContent
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.core.annotations.Page
import com.gearui.View
import com.gearui.App
import com.gearui.sample.i18n.SampleI18nProvider
import com.gearui.sample.pages.HomePage
import com.gearui.sample.pages.SettingsPage
import com.gearui.sample.pages.SettingsState
import com.gearui.sample.pages.LocalSettingsState
import com.gearui.sample.pages.ThemeStyle
import com.gearui.sample.navigation.NavigationManager
import com.gearui.sample.theme.CustomThemes
import com.gearui.theme.Theme
import com.gearui.theme.ThemeMode
import com.gearui.theme.ThemeSpec
import com.gearui.theme.Themes
import com.gearui.theme.Shapes
import com.gearui.theme.ShapesDefault
import com.gearui.theme.withBrandAccent

/**
 * GearUI sample main page
 *
 * - HomePage: the component index
 * - ExamplePages: standalone pages from the component registry
 * - SettingsPage: settings
 * - Navigator: the page stack (push, pop, swipe back), as in privchat-app
 * - NavigationManager: component id to example page
 *
 * Supports switching theme and language at runtime
 */
@Page("MainDemo")
class MainDemo : View() {

    init {
        // Splits startup: everything before this is loading, the host and the Kuikly
        // runtime; everything after it, up to the home content, is the page and the kit.
        StartupMark.pageCreated()
    }

    // MainDemoContent mounts the one App itself (it owns theme, brand and language).
    // Leaving the base class's wrapper on nests a second, light-themed App around it:
    // two OverlayRoots, two ToastHosts, two ActionSheet hosts, and the outer light
    // toast drawn on top of the dark one.
    override fun autoWrapApp(): Boolean = false

    @Composable
    override fun Content() {
        // Automation hook: the host may open a page directly (`route`, a component id,
        // or "settings") in a given theme (`theme`: light | dark) and language
        // (`lang`). The Android host fills these from intent extras.
        val params = pageData.params
        MainDemoContent(
            startRoute = params.optString("route"),
            startTheme = params.optString("theme"),
            startLanguage = params.optString("lang"),
        )
    }
}

@Composable
fun MainDemoContent(startRoute: String = "", startTheme: String = "", startLanguage: String = "") {
    // Settings state - drives theme and language
    val settingsState = remember {
        SettingsState().apply {
            when (startTheme) {
                "light" -> themeStyle = ThemeStyle.LIGHT
                "dark" -> themeStyle = ThemeStyle.DARK
                "system" -> themeStyle = ThemeStyle.SYSTEM
            }
            if (startLanguage.isNotEmpty()) languageTag = startLanguage
        }
    }

    // System dark mode state
    val isSystemDark = StatusBarControllerImpl.isSystemDarkMode()

    // Theme mode and custom theme derived from the settings state
    val (themeMode, customTheme) = when (settingsState.themeStyle) {
        ThemeStyle.LIGHT -> ThemeMode.Light to null
        ThemeStyle.DARK -> ThemeMode.Dark to null
        ThemeStyle.DARK_PURPLE -> ThemeMode.Dark to CustomThemes.DarkPurple
        ThemeStyle.SYSTEM -> ThemeMode.System to null
    }

    val baseTheme = customTheme ?: if (
        themeMode == ThemeMode.Dark || (themeMode == ThemeMode.System && isSystemDark)
    ) Themes.Dark else Themes.Light
    val brandedTheme = settingsState.brandAccent.color?.let { baseTheme.withBrandAccent(it) } ?: baseTheme
    val square = ShapesDefault.Default.none
    val shapes = if (settingsState.squareControls) Shapes(
        none = square, sm = square, md = square, lg = square,
        xl = square, full = square, controlLarge = square
    ) else ShapesDefault.Default

    // App as the single entry point (i18n + Theme + Overlay + Toast together)
    App(
        themeMode = themeMode,
        isSystemDark = isSystemDark,
        theme = brandedTheme,
        shapes = shapes,
        languageTag = settingsState.languageTag,
    ) {
        // Update the status bar colour
        StatusBarEffect()

        // The sample's own language pack, relying on the LocalLanguageTag that App already provides
        SampleI18nProvider {
            CompositionLocalProvider(LocalSettingsState provides settingsState) {
                MainDemoContentInner(settingsState = settingsState, startRoute = startRoute)
            }
        }
    }
}

/**
 * Status bar colour synchronisation
 * Updates the status bar to match the current theme
 */
@Composable
private fun StatusBarEffect() {
    val colors = Theme.colors
    val statusBarColor = colors.surface

    // The surface colour is used as the status bar background
    // Dark theme takes light icons, light theme takes dark icons
    val isDarkTheme = run {
        (statusBarColor.red + statusBarColor.green + statusBarColor.blue) / 3f < 0.5f
    }

    LaunchedEffect(statusBarColor, isDarkTheme) {
        StatusBarControllerImpl.setStatusBarColor(
            color = statusBarColor,
            darkIcons = !isDarkTheme
        )
    }
}

/**
 * Status bar controller implementation (platform specific)
 * On Android the real implementation is registered by MainActivity
 */
expect object StatusBarControllerImpl {
    fun setStatusBarColor(color: com.tencent.kuikly.compose.ui.graphics.Color, darkIcons: Boolean)
    fun isSystemDarkMode(): Boolean
}

@Composable
private fun MainDemoContentInner(settingsState: SettingsState, startRoute: String = "") {
    // Opt-in build fixture for identical native/web screenshots; normal builds stay on Home.
    if (SampleBuildInfo.SURFACE_ACCEPTANCE.isNotEmpty()) {
        com.gearui.sample.pages.ExamplePage(
            com.gearui.sample.config.ComponentConfig.all.first { it.id == "card" },
            onBack = {},
        ) {
            com.gearui.sample.examples.card.SurfaceLab(
                initialDark = SampleBuildInfo.SURFACE_ACCEPTANCE.startsWith("dark"),
                initialSquare = SampleBuildInfo.SURFACE_ACCEPTANCE.endsWith("square"),
            )
        }
        return
    }
    // A page stack, as in privchat-app: the page underneath stays composed while the top
    // one is dragged away, so a swipe from anywhere on the page reveals it, and Android
    // BACK pops before it leaves the app.
    val nav = rememberNavigatorController<SampleRoute>(SampleRoute.Home)
    // Automation opens a page directly. Pushed before the Navigator's first frame, the
    // page is simply there (no slide-in to catch half-way in a screenshot), with Home
    // underneath so back still works.
    remember(startRoute) {
        when {
            startRoute == "settings" -> nav.push(SampleRoute.Settings)
            ComponentConfig.all.any { it.id == startRoute } -> nav.push(SampleRoute.Component(startRoute))
        }
    }
    // Hoisted: Home leaves composition while covered and comes back as the lower layer.
    val homeListState = rememberLazyListState()

    Navigator(controller = nav, swipeBackEnabled = true, handleBack = true) { entry ->
        when (val route = entry.route) {
            SampleRoute.Home -> {
                MarkStartupContent()
                HomePage(
                    listState = homeListState,
                    onComponentClick = { nav.push(SampleRoute.Component(it.id)) },
                    onSettingsClick = { nav.push(SampleRoute.Settings) },
                )
            }
            SampleRoute.Settings -> SettingsPage(settingsState = settingsState, onBack = { nav.pop() })
            is SampleRoute.Component -> NavigationManager.getExamplePage(
                component = ComponentConfig.all.first { it.id == route.id },
                onBack = { nav.pop() },
            )
        }
    }
}

/** The sample's pages. A component page carries its id, as privchat-app's routes carry theirs. */
private sealed interface SampleRoute : NavRoute {
    data object Home : SampleRoute {
        override val routeName = "home"
    }

    data object Settings : SampleRoute {
        override val routeName = "settings"
    }

    data class Component(val id: String) : SampleRoute {
        override val routeName = "component"

        // These examples host a Navigator of their own; the drag belongs to it.
        override val options: NavOptions =
            if (id in NestedNavigatorExamples) NavOptions(swipeBackEnabled = false) else NavOptions.Default
    }
}

private val NestedNavigatorExamples = setOf("navigator-kuikly-spike", "navigator-v1-demo")
