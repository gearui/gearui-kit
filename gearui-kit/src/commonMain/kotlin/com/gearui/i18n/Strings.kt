package com.gearui.i18n

import androidx.compose.runtime.Immutable

/**
 * GearUI Kit's own user-facing strings. Each downstream library defines its
 * own `XxxStrings` data class and `XxxI18n` accessor, sharing only the
 * language environment ([LocalLanguageTag] / [LocalFallbackLanguageTag])
 * exposed by [I18nRoot]. See `docs/I18N_INTEGRATION.md`.
 */
@Immutable
data class Strings(
    val buttonConfirm: String,
    val buttonCancel: String,
    val theme: String,
    val language: String,
    val light: String,
    val dark: String,
    val system: String,
)
