package com.gearui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

val LocalLanguageTag = staticCompositionLocalOf { "en-US" }

val LocalFallbackLanguageTag = staticCompositionLocalOf { "en-US" }

/**
 * Root for layered i18n. Provides only the language environment — no strings —
 * so each library can layer its own per-library Provider without gearui-kit
 * knowing about it. Apps usually do not call this directly; [com.gearui.App]
 * mounts it. See `docs/I18N_INTEGRATION.md`.
 */
@Composable
fun I18nRoot(
    languageTag: String,
    fallbackLanguageTag: String = "en-US",
    content: @Composable () -> Unit,
) {
    val normalizedTag = remember(languageTag) { normalizeLanguageTag(languageTag) }
    val normalizedFallback = remember(fallbackLanguageTag) { normalizeLanguageTag(fallbackLanguageTag) }
    CompositionLocalProvider(
        LocalLanguageTag provides normalizedTag,
        LocalFallbackLanguageTag provides normalizedFallback,
        content = content,
    )
}
