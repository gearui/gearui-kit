package com.gearui.system

import com.gearui.theme.ThemeMode

/**
 * Tells the platform which appearance the app has chosen, so system-drawn parts — the
 * software keyboard above all — match the app instead of the system setting.
 *
 * [ThemeMode.System] hands the choice back to the system; Light and Dark force it.
 *
 * Only iOS can honour it: the keyboard follows its window's interface style. On Android
 * the keyboard is another app that follows the system's dark setting alone (a per-app
 * night mode does not reach it); there is no API for an app to choose it.
 */
internal expect fun applyPlatformInterfaceStyle(mode: ThemeMode)
