package com.gearui.theme

import androidx.compose.runtime.*

/* --------------------------------------------------------- */
/* ThemeMode - three-state theme strategy */
/* Aligned with Flutter ThemeMode / iOS UIUserInterfaceStyle */
/* --------------------------------------------------------- */

enum class ThemeMode {
    /** force the light theme */
    Light,
    /** force the dark theme */
    Dark,
    /** follow the system */
    System
}

/* --------------------------------------------------------- */
/* CompositionLocal - theme state */
/* --------------------------------------------------------- */

val LocalThemeColors = staticCompositionLocalOf { Themes.Light.colors }
val LocalThemeTypography = staticCompositionLocalOf { Typographies.Default }
val LocalThemeShapes = staticCompositionLocalOf { ShapesDefault.Default }

/** System dark mode state, supplied by the platform layer */
val LocalSystemDarkMode = staticCompositionLocalOf { false }

/* --------------------------------------------------------- */
/* Theme resolution */
/* --------------------------------------------------------- */

/**
 * Resolves a ThemeMode into an actual dark boolean
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
 * Returns the theme spec for a dark boolean
 */
private fun resolveThemeSpec(isDark: Boolean): ThemeSpec {
    return if (isDark) Themes.Dark else Themes.Light
}

/* --------------------------------------------------------- */
/* ⭐ Theme core composable */
/* --------------------------------------------------------- */

/**
 * Theme - GearUI theme wrapper
 *
 * Usage:
 * ```kotlin
 * Theme(mode = ThemeMode.Dark) {
 *     // dark theme content
 * }
 * ```
 *
 * @param mode theme mode (Light / Dark / System)
 * @param theme custom theme spec (takes precedence over mode)
 * @param typography the type scale
 * @param shapes the shape scale
 * @param content the content
 */
@Composable
fun Theme(
    mode: ThemeMode = ThemeMode.Light,
    theme: ThemeSpec? = null,
    typography: Typography = Typographies.Default,
    shapes: Shapes = ShapesDefault.Default,
    content: @Composable () -> Unit
) {
    // Resolve the final theme
    val resolved = theme ?: resolveThemeSpec(resolveDarkMode(mode))

    CompositionLocalProvider(
        LocalThemeColors provides resolved.colors,
        LocalThemeTypography provides typography,
        LocalThemeShapes provides shapes,
        content = content
    )
}

/**
 * Provides the system dark mode state.
 * Call this at the outermost level of the App; the platform layer supplies the value.
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
/* Convenience accessor for the theme */
/* --------------------------------------------------------- */

object Theme {
    /** current theme colours */
    val colors: Colors
        @Composable get() = LocalThemeColors.current

    /** current type scale */
    val typography: Typography
        @Composable get() = LocalThemeTypography.current

    /** current shape scale */
    val shapes: Shapes
        @Composable get() = LocalThemeShapes.current
}
