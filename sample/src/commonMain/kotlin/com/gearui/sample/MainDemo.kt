package com.gearui.sample

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.BackHandler
import com.tencent.kuikly.compose.animation.core.Animatable
import com.tencent.kuikly.compose.animation.core.spring
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.lazy.LazyListState
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.core.annotations.Page
import com.gearui.View
import com.gearui.App
import com.gearui.gestures.SwipeBackConfig
import com.gearui.gestures.swipeBack
import com.gearui.sample.i18n.SampleI18nProvider
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
import kotlinx.coroutines.launch

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

    // 使用 App 统一入口（整合 i18n + Theme + Overlay + Toast）
    App(
        themeMode = themeMode,
        isSystemDark = isSystemDark,
        theme = customTheme,
        languageTag = settingsState.languageTag,
    ) {
        // 更新状态栏颜色
        StatusBarEffect(themeStyle = settingsState.themeStyle)

        // sample 自身的语言包，依赖 App 已挂的 LocalLanguageTag
        SampleI18nProvider {
            CompositionLocalProvider(LocalSettingsState provides settingsState) {
                MainDemoContentInner(settingsState = settingsState)
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
    val statusBarColor = if (themeStyle == ThemeStyle.DARK_PURPLE) colors.primary else colors.surface

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

    fun returnToHome() {
        currentPage = AppPage.HOME
        currentComponent = null
    }

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
                if (component.id == "navigator-kuikly-spike" || component.id == "navigator-v1-demo") {
                    // 这两个详情页内部自带 swipeBack/Navigator，绕过外层 SwipeBackHost 避免手势冲突
                    NavigationManager.getExamplePage(
                        component = component,
                        onBack = { returnToHome() }
                    )
                } else {
                    ExampleDetailSwipeBackHost(
                        homeListState = homeListState,
                        onHomeComponentClick = { nextComponent ->
                            currentComponent = nextComponent
                            currentPage = AppPage.COMPONENT_DETAIL
                        },
                        onSettingsClick = {
                            currentPage = AppPage.SETTINGS
                        },
                        onBack = { returnToHome() }
                    ) {
                        NavigationManager.getExamplePage(
                            component = component,
                            onBack = { returnToHome() }
                        )
                    }
                }
            } ?: run {
                returnToHome()
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

@Composable
private fun ExampleDetailSwipeBackHost(
    homeListState: LazyListState,
    onHomeComponentClick: (ComponentInfo) -> Unit,
    onSettingsClick: () -> Unit,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var previousLayerMounted by remember { mutableStateOf(false) }
    var isCompletingPop by remember { mutableStateOf(false) }
    var containerWidthPx by remember { mutableStateOf(0) }

    fun finishPop(animated: Boolean) {
        if (isCompletingPop) return
        isCompletingPop = true
        previousLayerMounted = true
        scope.launch {
            if (animated) {
                val targetX = if (containerWidthPx > 0) containerWidthPx.toFloat() else 480f
                offsetX.animateTo(
                    targetValue = targetX,
                    animationSpec = tween(durationMillis = 180)
                )
            }
            onBack()
            offsetX.snapTo(0f)
            previousLayerMounted = false
            isCompletingPop = false
        }
    }

    BackHandler {
        finishPop(animated = false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .onSizeChanged { size ->
                containerWidthPx = size.width
            }
    ) {
        if (previousLayerMounted) {
            HomePage(
                listState = homeListState,
                onComponentClick = onHomeComponentClick,
                onSettingsClick = onSettingsClick
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = offsetX.value
                }
                .swipeBack(
                    enabled = !isCompletingPop,
                    config = SwipeBackConfig(edgeWidthDp = 96f),
                    onStart = {
                        previousLayerMounted = true
                        scope.launch {
                            offsetX.snapTo(0f)
                        }
                    },
                    onProgress = { _, dragX ->
                        scope.launch {
                            offsetX.snapTo(dragX)
                        }
                    },
                    onCancel = {
                        scope.launch {
                            offsetX.animateTo(
                                targetValue = 0f,
                                animationSpec = spring()
                            )
                            previousLayerMounted = false
                        }
                    },
                    onCommit = {
                        finishPop(animated = true)
                    }
                )
        ) {
            content()
        }
    }
}
