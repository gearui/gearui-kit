package com.gearui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

val LocalGearStrings = staticCompositionLocalOf { GearStringPacks.English }

/**
 * Provider for GearUI Kit's own strings. Reads [LocalLanguageTag] /
 * [LocalFallbackLanguageTag] set by [I18nRoot], resolves the matching pack
 * from [GearStringPacks.builtIn], and applies any [overrides] keyed by
 * language tag. Apps usually do not call this directly; [com.gearui.GearApp]
 * mounts it automatically.
 */
@Composable
fun GearI18nProvider(
    overrides: Map<String, GearStringsPatch> = emptyMap(),
    content: @Composable () -> Unit,
) {
    val tag = LocalLanguageTag.current
    val fallback = LocalFallbackLanguageTag.current
    val strings = remember(tag, fallback, overrides) {
        val base = resolveLanguagePack(
            languageTag = tag,
            packs = GearStringPacks.builtIn,
            defaultTag = fallback,
        )
        base.merge(resolvePatch(tag, fallback, overrides))
    }
    CompositionLocalProvider(LocalGearStrings provides strings, content = content)
}

object GearI18n {
    val strings: GearStrings
        @Composable get() = LocalGearStrings.current

    val languageTag: String
        @Composable get() = LocalLanguageTag.current
}

/**
 * Resolve a patch using the same fallback chain as [resolveLanguagePack]
 * (e.g. zh-Hant-HK → zh-Hant → zh → fallback). Returns null when no
 * applicable patch is found, so [GearStrings.merge] short-circuits.
 */
private fun resolvePatch(
    languageTag: String,
    fallbackTag: String,
    overrides: Map<String, GearStringsPatch>,
): GearStringsPatch? {
    if (overrides.isEmpty()) return null
    val normalized = overrides.entries.associate { normalizeLanguageTag(it.key) to it.value }

    val candidates = mutableListOf<String>()
    var current = languageTag
    while (true) {
        candidates += current
        val idx = current.lastIndexOf('-')
        if (idx <= 0) break
        current = current.substring(0, idx)
    }
    if (!candidates.contains(fallbackTag)) candidates += fallbackTag

    for (c in candidates) {
        normalized[c]?.let { if (!it.isEmpty) return it }
    }
    return null
}
