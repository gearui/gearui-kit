package com.gearui.i18n

/**
 * Normalize language tag to BCP47-style casing:
 * - language: lower (en, zh, yue)
 * - script: title (Hans, Hant)
 * - region: upper (US, CN, HK)
 */
fun normalizeLanguageTag(tag: String): String {
    val cleaned = tag.trim().replace('_', '-')
    if (cleaned.isEmpty()) return GearStringPacks.DEFAULT_LANGUAGE_TAG
    val parts = cleaned.split('-').filter { it.isNotBlank() }
    if (parts.isEmpty()) return GearStringPacks.DEFAULT_LANGUAGE_TAG

    val normalized = mutableListOf<String>()
    normalized += parts.first().lowercase()
    for (part in parts.drop(1)) {
        normalized += when {
            part.length == 4 && part.all { it.isLetter() } -> {
                part.lowercase().replaceFirstChar { it.uppercase() }
            }
            (part.length == 2 || part.length == 3) && part.all { it.isLetterOrDigit() } -> {
                part.uppercase()
            }
            else -> part.lowercase()
        }
    }
    return normalized.joinToString("-")
}

/**
 * Resolve strings by language tag with fallback chain.
 * Example: zh-Hant-HK -> zh-Hant -> zh -> defaultTag
 */
fun <T> resolveLanguagePack(
    languageTag: String,
    packs: Map<String, T>,
    defaultTag: String,
): T {
    val normalizedInput = normalizeLanguageTag(languageTag)
    val normalizedPacks = packs.entries.associate { normalizeLanguageTag(it.key) to it.value }
    val normalizedDefault = normalizeLanguageTag(defaultTag)

    val candidates = mutableListOf<String>()
    var current = normalizedInput
    while (true) {
        candidates += current
        val idx = current.lastIndexOf('-')
        if (idx <= 0) break
        current = current.substring(0, idx)
    }

    val languageOnly = normalizedInput.substringBefore('-')
    if (!candidates.contains(languageOnly)) {
        candidates += languageOnly
    }
    if (!candidates.contains(normalizedDefault)) {
        candidates += normalizedDefault
    }

    for (candidate in candidates) {
        normalizedPacks[candidate]?.let { return it }
    }

    return normalizedPacks.values.first()
}
