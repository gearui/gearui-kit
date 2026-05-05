package com.gearui.i18n

object StringPacks {
    const val DEFAULT_LANGUAGE_TAG: String = "en-US"
    const val ENGLISH_TAG: String = "en-US"
    const val CHINESE_SIMPLIFIED_TAG: String = "zh-Hans"
    const val CHINESE_TRADITIONAL_TAG: String = "zh-Hant"

    val English: Strings = StringsEnUs
    val ChineseSimplified: Strings = StringsZhHans
    val ChineseTraditional: Strings = StringsZhHant

    val builtIn: Map<String, Strings> = mapOf(
        ENGLISH_TAG to English,
        CHINESE_SIMPLIFIED_TAG to ChineseSimplified,
        CHINESE_TRADITIONAL_TAG to ChineseTraditional,
        "zh-CN" to ChineseSimplified,
        "zh-TW" to ChineseTraditional,
        "zh-HK" to ChineseTraditional,
        "zh" to ChineseSimplified,
        "en" to English,
    )
}
