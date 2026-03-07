package com.gearui.sample

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.core.annotations.Page
import com.gearui.View
import com.gearui.GearApp
import com.gearui.i18n.I18n
import com.gearui.sample.i18n.SampleI18n
import com.gearui.sample.config.ComponentInfo
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

/**
 * 导航页面枚举
 */
enum class AppPage {
    HOME,
    COMPONENT_DETAIL,
    SETTINGS
}

/**
 * GearUI 示例主页面
 *
 * - HomePage: 组件列表页
 * - ExamplePages: 62 个组件的独立展示页
 * - SettingsPage: 设置页面
 * - NavigationManager: 导航管理
 *
 * 支持动态主题和语言切换
 */
@Page("MainDemo")
class MainDemo : View() {

    @Composable
    override fun Content() {
        MainDemoContent()
    }
}

@Composable
fun MainDemoContent() {
    // 设置状态 - 控制主题和语言
    val settingsState = remember { SettingsState() }

    // 获取系统深色模式状态
    val isSystemDark = StatusBarControllerImpl.isSystemDarkMode()

    // 根据设置状态计算主题模式和自定义主题
    val (themeMode, customTheme) = when (settingsState.themeStyle) {
        ThemeStyle.LIGHT -> ThemeMode.Light to null
        ThemeStyle.DARK -> ThemeMode.Dark to null
        ThemeStyle.DARK_PURPLE -> ThemeMode.Dark to CustomThemes.DarkPurple
        ThemeStyle.SYSTEM -> ThemeMode.System to null
    }

    // 使用 GearApp 统一入口（整合 Theme + Overlay + Toast）
    GearApp(themeMode = themeMode, isSystemDark = isSystemDark, theme = customTheme) {
        // 更新状态栏颜色
        StatusBarEffect(themeStyle = settingsState.themeStyle)

        // 框架语言 + sample 语言
        I18n(languageTag = settingsState.languageTag) {
            SampleI18n(languageTag = settingsState.languageTag) {
                CompositionLocalProvider(LocalSettingsState provides settingsState) {
                    MainDemoContentInner(settingsState = settingsState)
                }
            }
        }
    }
}

/**
 * 状态栏颜色同步效果
 * 根据当前主题自动更新状态栏
 */
@Composable
private fun StatusBarEffect(themeStyle: ThemeStyle) {
    val colors = Theme.colors
    val statusBarColor = if (themeStyle == ThemeStyle.DARK_PURPLE) colors.primaryActive else colors.surface

    // 使用 surface 颜色作为状态栏背景
    // 深色主题用浅色图标，浅色主题用深色图标
    val forceLightIcons = themeStyle == ThemeStyle.DARK_PURPLE
    val isDarkTheme = if (forceLightIcons) true else {
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
 * 状态栏控制器实现（平台特定）
 * 在 Android 端由 MainActivity 注册实际实现
 */
expect object StatusBarControllerImpl {
    fun setStatusBarColor(color: com.tencent.kuikly.compose.ui.graphics.Color, darkIcons: Boolean)
    fun isSystemDarkMode(): Boolean
}

@Composable
private fun MainDemoContentInner(settingsState: SettingsState) {
    // 导航状态管理
    var currentPage by remember { mutableStateOf(AppPage.HOME) }
    var currentComponent by remember { mutableStateOf<ComponentInfo?>(null) }

    // 首页列表滚动状态
    val homeListState = rememberLazyListState()

    when (currentPage) {
        AppPage.HOME -> {
            HomePage(
                listState = homeListState,
                onComponentClick = { component ->
                    currentComponent = component
                    currentPage = AppPage.COMPONENT_DETAIL
                },
                onSettingsClick = {
                    currentPage = AppPage.SETTINGS
                }
            )
        }

        AppPage.COMPONENT_DETAIL -> {
            currentComponent?.let { component ->
                NavigationManager.getExamplePage(
                    component = component,
                    onBack = { currentPage = AppPage.HOME }
                )
            } ?: run {
                currentPage = AppPage.HOME
            }
        }

        AppPage.SETTINGS -> {
            SettingsPage(
                settingsState = settingsState,
                onBack = { currentPage = AppPage.HOME }
            )
        }
    }
}
