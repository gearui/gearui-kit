package com.gearui.foundation.field

import androidx.compose.runtime.Composable
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * Field style, by what the field sits on. An enabled field is always a light box the
 * eye reads as "type here"; a gray fill is kept for the disabled state, which is what
 * both iOS (rounded-rect text fields) and Chinese app conventions read as unavailable.
 *
 * Every field-family component takes it: Input, Textarea, AutoResizeTextarea, InputOTP,
 * ComboBox, NumberField, Select, MultiSelect, Cascader, DatePickerInput,
 * TimePickerInput, DateTimePickerInput, DateRangePickerInput, InputGroup and SearchBar.
 *
 * - [PRIMARY]: field colour (white in light) with the field shadow; for fields on
 *   the page background.
 * - [SECONDARY]: field colour with a hairline border and no shadow; for fields that
 *   sit on a surface such as a header, card or sheet, where a shadow would not show.
 *   A search field is the one exception: it keeps the gray pill users know.
 */
enum class FieldVariant {
    PRIMARY,
    SECONDARY,
}

/** Whether the field draws the field shadow stack outside its clip. */
internal val FieldVariant.shadowed: Boolean
    get() = this == FieldVariant.PRIMARY

/** The field's resting fill: [field] while enabled, `muted` while disabled. */
@Composable
@Suppress("UnusedReceiverParameter")
internal fun FieldVariant.fill(field: Color, enabled: Boolean = true): Color =
    if (enabled) field else Theme.colors.muted

/**
 * The field's resting border: the hairline `border` colour for [FieldVariant.SECONDARY],
 * [field] (the palette's input border) for [FieldVariant.PRIMARY]; none while disabled.
 */
@Composable
internal fun FieldVariant.border(field: Color, enabled: Boolean = true): Color = when {
    !enabled -> Color.Transparent
    this == FieldVariant.SECONDARY -> Theme.colors.border
    else -> field
}

/** Fill of a tappable field trigger while pressed. */
@Composable
@Suppress("UnusedReceiverParameter")
internal fun FieldVariant.pressedFill(): Color = Theme.colors.muted
