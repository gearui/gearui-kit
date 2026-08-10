package com.gearui.i18n

import androidx.compose.runtime.Immutable

/**
 * Domain-level override for [Strings]. Every domain is nullable, and every
 * field inside a domain patch is nullable too; null means "inherit from base
 * pack". Apps supply patches keyed by language tag via [I18nProvider] (or the
 * `stringsOverrides` parameter of `App`).
 */
@Immutable
data class StringsPatch(
    val common: CommonStringsPatch? = null,
    val theming: ThemeStringsPatch? = null,
    val field: FieldStringsPatch? = null,
    val dateTime: DateTimeStringsPatch? = null,
    val feedback: FeedbackStringsPatch? = null,
    val media: MediaStringsPatch? = null,
    val guide: GuideStringsPatch? = null,
)

val StringsPatch.isEmpty: Boolean
    get() = (common == null || common.isEmpty) &&
        (theming == null || theming.isEmpty) &&
        (field == null || field.isEmpty) &&
        (dateTime == null || dateTime.isEmpty) &&
        (feedback == null || feedback.isEmpty) &&
        (media == null || media.isEmpty) &&
        (guide == null || guide.isEmpty)

/**
 * Apply [patch] on top of base strings. Returns the receiver unchanged when
 * [patch] is `null` or all-null, so the hot path allocates nothing.
 */
fun Strings.merge(patch: StringsPatch?): Strings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        common = common.merge(patch.common),
        theming = theming.merge(patch.theming),
        field = field.merge(patch.field),
        dateTime = dateTime.merge(patch.dateTime),
        feedback = feedback.merge(patch.feedback),
        media = media.merge(patch.media),
        guide = guide.merge(patch.guide),
    )
}
