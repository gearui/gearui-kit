package com.gearui.sample.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.gearui.i18n.LocalFallbackLanguageTag
import com.gearui.i18n.LocalLanguageTag
import com.gearui.i18n.normalizeLanguageTag
import com.gearui.i18n.resolveLanguagePack

@Immutable
data class SampleStrings(
    val homeTitle: String,
    val searchPlaceholder: String,
    val noResults: String,
    val componentCountSuffix: String,
    val categoryBasic: String,
    val categoryForm: String,
    val categoryNavigation: String,
    val categoryDataDisplay: String,
    val categoryFeedback: String,
    val categoryLayout: String,
    val settingsTitle: String,
    val aboutTitle: String,
    val versionLabel: String,
    val gearUiComponents: String,
    val darkPurple: String,
)

@Immutable
data class SampleStringsPatch(
    val homeTitle: String? = null,
    val searchPlaceholder: String? = null,
    val noResults: String? = null,
    val componentCountSuffix: String? = null,
    val categoryBasic: String? = null,
    val categoryForm: String? = null,
    val categoryNavigation: String? = null,
    val categoryDataDisplay: String? = null,
    val categoryFeedback: String? = null,
    val categoryLayout: String? = null,
    val settingsTitle: String? = null,
    val aboutTitle: String? = null,
    val versionLabel: String? = null,
    val gearUiComponents: String? = null,
    val darkPurple: String? = null,
)

val SampleStringsPatch.isEmpty: Boolean
    get() = homeTitle == null && searchPlaceholder == null && noResults == null &&
        componentCountSuffix == null && categoryBasic == null && categoryForm == null &&
        categoryNavigation == null && categoryDataDisplay == null && categoryFeedback == null &&
        categoryLayout == null && settingsTitle == null && aboutTitle == null &&
        versionLabel == null && gearUiComponents == null && darkPurple == null

fun SampleStrings.merge(patch: SampleStringsPatch?): SampleStrings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        homeTitle = patch.homeTitle ?: homeTitle,
        searchPlaceholder = patch.searchPlaceholder ?: searchPlaceholder,
        noResults = patch.noResults ?: noResults,
        componentCountSuffix = patch.componentCountSuffix ?: componentCountSuffix,
        categoryBasic = patch.categoryBasic ?: categoryBasic,
        categoryForm = patch.categoryForm ?: categoryForm,
        categoryNavigation = patch.categoryNavigation ?: categoryNavigation,
        categoryDataDisplay = patch.categoryDataDisplay ?: categoryDataDisplay,
        categoryFeedback = patch.categoryFeedback ?: categoryFeedback,
        categoryLayout = patch.categoryLayout ?: categoryLayout,
        settingsTitle = patch.settingsTitle ?: settingsTitle,
        aboutTitle = patch.aboutTitle ?: aboutTitle,
        versionLabel = patch.versionLabel ?: versionLabel,
        gearUiComponents = patch.gearUiComponents ?: gearUiComponents,
        darkPurple = patch.darkPurple ?: darkPurple,
    )
}

object SampleStringPacks {
    val English: SampleStrings = SampleStringsEnUs
    val ChineseSimplified: SampleStrings = SampleStringsZhHans
    val ChineseTraditional: SampleStrings = SampleStringsZhHant

    val builtIn: Map<String, SampleStrings> = mapOf(
        "en-US" to English,
        "en" to English,
        "zh-Hans" to ChineseSimplified,
        "zh-Hant" to ChineseTraditional,
        "zh-CN" to ChineseSimplified,
        "zh-TW" to ChineseTraditional,
        "zh-HK" to ChineseTraditional,
        "zh" to ChineseSimplified,
    )
}

val LocalSampleStrings = staticCompositionLocalOf { SampleStringPacks.English }

/**
 * Sample-layer i18n provider. Reads gearui-kit's [LocalLanguageTag] /
 * [LocalFallbackLanguageTag] — apps do not pass languageTag here.
 */
@Composable
fun SampleI18nProvider(
    overrides: Map<String, SampleStringsPatch> = emptyMap(),
    content: @Composable () -> Unit,
) {
    val tag = LocalLanguageTag.current
    val fallback = LocalFallbackLanguageTag.current
    val strings = remember(tag, fallback, overrides) {
        val base = resolveLanguagePack(
            languageTag = tag,
            packs = SampleStringPacks.builtIn,
            defaultTag = fallback,
        )
        base.merge(resolveSamplePatch(tag, fallback, overrides))
    }
    CompositionLocalProvider(LocalSampleStrings provides strings, content = content)
}

object SampleI18n {
    val strings: SampleStrings
        @Composable get() = LocalSampleStrings.current
}

private fun resolveSamplePatch(
    languageTag: String,
    fallbackTag: String,
    overrides: Map<String, SampleStringsPatch>,
): SampleStringsPatch? {
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

data class SampleLanguageOption(
    val tag: String,
    val displayName: String,
    val code: String = tag,
)

val DefaultSampleLanguageOptions = listOf(
    SampleLanguageOption(tag = "zh-Hans", displayName = "简体中文"),
    SampleLanguageOption(tag = "zh-Hant", displayName = "繁體中文"),
    SampleLanguageOption(tag = "en-US", displayName = "English"),
)
