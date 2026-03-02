package com.gearui.sample.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import com.gearui.i18n.resolveLanguagePack
import com.gearui.i18n.normalizeLanguageTag

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

object SampleStringPacks {
    const val DEFAULT_LANGUAGE_TAG: String = "en-US"
    val English = SampleStringsEnUs
    val ChineseSimplified = SampleStringsZhHans
    val ChineseTraditional = SampleStringsZhHant

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

@Composable
fun SampleI18n(
    languageTag: String,
    packs: Map<String, SampleStrings> = SampleStringPacks.builtIn,
    defaultTag: String = SampleStringPacks.DEFAULT_LANGUAGE_TAG,
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
    CompositionLocalProvider(LocalSampleStrings provides strings, content = content)
}

object SampleI18n {
    val strings: SampleStrings
        @Composable get() = LocalSampleStrings.current
}

data class SampleLanguageOption(
    val tag: String,
    val displayName: String,
    val code: String = tag
)

val DefaultSampleLanguageOptions = listOf(
    SampleLanguageOption(tag = "zh-Hans", displayName = "简体中文"),
    SampleLanguageOption(tag = "zh-Hant", displayName = "繁體中文"),
    SampleLanguageOption(tag = "en-US", displayName = "English"),
)
