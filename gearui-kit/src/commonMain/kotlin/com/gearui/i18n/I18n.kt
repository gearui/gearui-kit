package com.gearui.i18n

import androidx.compose.runtime.*

val LocalStrings = staticCompositionLocalOf {
    GearStringPacks.English
}

val LocalLanguageTag = staticCompositionLocalOf {
    GearStringPacks.DEFAULT_LANGUAGE_TAG
}

@Composable
fun I18n(
    strings: Strings = GearStringPacks.English,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalStrings provides strings,
        LocalLanguageTag provides GearStringPacks.DEFAULT_LANGUAGE_TAG,
        content = content
    )
}

@Composable
fun I18n(
    languageTag: String,
    packs: Map<String, Strings> = GearStringPacks.builtIn,
    defaultTag: String = GearStringPacks.DEFAULT_LANGUAGE_TAG,
    content: @Composable () -> Unit
) {
    val normalizedTag = remember(languageTag) { normalizeLanguageTag(languageTag) }
    val strings = remember(normalizedTag, packs, defaultTag) {
        resolveLanguagePack(
            languageTag = normalizedTag,
            packs = packs,
            defaultTag = defaultTag
        )
    }
    CompositionLocalProvider(
        LocalStrings provides strings,
        LocalLanguageTag provides normalizedTag,
        content = content
    )
}

object I18n {
    val strings: Strings
        @Composable get() = LocalStrings.current

    val languageTag: String
        @Composable get() = LocalLanguageTag.current
}
