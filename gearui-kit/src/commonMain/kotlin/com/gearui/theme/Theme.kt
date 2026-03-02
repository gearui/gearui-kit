package com.gearui.theme

import androidx.compose.runtime.*

/* --------------------------------------------------------- */
/* ThemeMode - 三态主题策略 */
/* 对齐 Flutter ThemeMode / iOS UIUserInterfaceStyle */
/* --------------------------------------------------------- */

enum class ThemeMode {
    /** 强制浅色主题 */
    Light,
    /** 强制深色主题 */
    Dark,
    /** 跟随系统 */
    System
}

/* --------------------------------------------------------- */
/* CompositionLocal - 主题状态 */
/* --------------------------------------------------------- */

val LocalThemeColors = staticCompositionLocalOf { Themes.Light.colors }
val LocalThemeTypography = staticCompositionLocalOf { GearTypographies.Default }
val LocalThemeShapes = staticCompositionLocalOf { GearShapesDefault.Default }

/** 系统深色模式状态（由平台层提供） */
val LocalSystemDarkMode = staticCompositionLocalOf { false }

/* --------------------------------------------------------- */
/* 主题解析逻辑 */
/* --------------------------------------------------------- */

/**
 * 解析 ThemeMode 为实际的 dark boolean
 */
@Composable
fun resolveDarkMode(mode: ThemeMode): Boolean {
    val systemDark = LocalSystemDarkMode.current
    return when (mode) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.System -> systemDark
    }
}

/**
 * 根据 dark boolean 获取主题规格
 */
private fun resolveThemeSpec(isDark: Boolean): ThemeSpec {
    return if (isDark) Themes.Dark else Themes.Light
}

/* --------------------------------------------------------- */
/* ⭐ Theme 核心 Composable */
/* --------------------------------------------------------- */

/**
 * Theme - GearUI 主题包装器
 *
 * 使用方式：
 * ```kotlin
 * Theme(mode = ThemeMode.Dark) {
 *     // 深色主题内容
 * }
 * ```
 *
 * @param mode 主题模式（Light/Dark/System）
 * @param theme 自定义主题规格（优先级高于 mode）
 * @param typography 排版系统
 * @param shapes 形状系统
 * @param content 内容
 */
@Composable
fun Theme(
    mode: ThemeMode = ThemeMode.Light,
    theme: ThemeSpec? = null,
    typography: GearTypography = GearTypographies.Default,
    shapes: GearShapes = GearShapesDefault.Default,
    content: @Composable () -> Unit
) {
    // 解析最终主题
    val resolved = theme ?: resolveThemeSpec(resolveDarkMode(mode))

    CompositionLocalProvider(
        LocalThemeColors provides resolved.colors,
        LocalThemeTypography provides typography,
        LocalThemeShapes provides shapes,
        content = content
    )
}

/**
 * 提供系统深色模式状态
 * 应在 App 最外层调用，由平台层传入系统深色模式状态
 */
@Composable
fun ProvideSystemDarkMode(
    isSystemDark: Boolean,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSystemDarkMode provides isSystemDark,
        content = content
    )
}

/* --------------------------------------------------------- */
/* Theme 便捷访问对象 */
/* --------------------------------------------------------- */

object Theme {
    /** 当前主题颜色 */
    val colors: GearColors
        @Composable get() = LocalThemeColors.current

    /** 当前排版系统 */
    val typography: GearTypography
        @Composable get() = LocalThemeTypography.current

    /** 当前形状系统 */
    val shapes: GearShapes
        @Composable get() = LocalThemeShapes.current
}
