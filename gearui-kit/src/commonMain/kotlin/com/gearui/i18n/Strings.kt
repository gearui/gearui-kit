package com.gearui.i18n

import androidx.compose.runtime.Immutable

/**
 * GearUI Kit's own user-facing strings, grouped by semantic domain (see
 * [DomainStrings.kt]). Each downstream library defines its own `XxxStrings`
 * data class and `XxxI18n` accessor, sharing only the language environment
 * ([LocalLanguageTag] / [LocalFallbackLanguageTag]) exposed by [I18nRoot].
 * See `docs/I18N_INTEGRATION.md`.
 *
 * The five theme/language fields are also re-exposed flat (`strings.theme`)
 * so existing call sites keep working.
 */
@Immutable
data class Strings(
    val common: CommonStrings,
    val theming: ThemeStrings,
    val field: FieldStrings,
    val dateTime: DateTimeStrings,
    val feedback: FeedbackStrings,
    val media: MediaStrings,
    val guide: GuideStrings,
) {
    val buttonConfirm: String get() = common.confirm
    val buttonCancel: String get() = common.cancel
    val theme: String get() = theming.theme
    val language: String get() = theming.language
    val light: String get() = theming.light
    val dark: String get() = theming.dark
    val system: String get() = theming.system
}
