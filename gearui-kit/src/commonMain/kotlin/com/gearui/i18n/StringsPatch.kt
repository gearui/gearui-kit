package com.gearui.i18n

import androidx.compose.runtime.Immutable

/**
 * Field-level override for [Strings]. Every field is nullable; null means
 * "inherit from base pack". Apps supply patches keyed by language tag via
 * [I18nProvider] (or the `stringsOverrides` parameter of `App`).
 */
@Immutable
data class StringsPatch(
    val buttonConfirm: String? = null,
    val buttonCancel: String? = null,
    val theme: String? = null,
    val language: String? = null,
    val light: String? = null,
    val dark: String? = null,
    val system: String? = null,
)

val StringsPatch.isEmpty: Boolean
    get() = buttonConfirm == null &&
        buttonCancel == null &&
        theme == null &&
        language == null &&
        light == null &&
        dark == null &&
        system == null

/**
 * Apply [patch] on top of base strings. Returns the receiver unchanged when
 * [patch] is `null` or all-null, so the hot path allocates nothing.
 */
fun Strings.merge(patch: StringsPatch?): Strings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        buttonConfirm = patch.buttonConfirm ?: buttonConfirm,
        buttonCancel = patch.buttonCancel ?: buttonCancel,
        theme = patch.theme ?: theme,
        language = patch.language ?: language,
        light = patch.light ?: light,
        dark = patch.dark ?: dark,
        system = patch.system ?: system,
    )
}
