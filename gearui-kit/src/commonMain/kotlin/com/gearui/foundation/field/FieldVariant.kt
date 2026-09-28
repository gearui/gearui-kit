package com.gearui.foundation.field

import androidx.compose.runtime.Composable
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * Field fill, after the reference Input `variant` (`input.css`).
 *
 * Every field-family component takes it: Input, Textarea, AutoResizeTextarea, InputOTP,
 * ComboBox, NumberField, Select, MultiSelect, Cascader, DatePickerInput,
 * TimePickerInput, DateTimePickerInput, DateRangePickerInput, InputGroup and SearchBar.
 *
 * - [PRIMARY]: field colour (white in light) with the field shadow; for fields on
 *   the page background.
 * - [SECONDARY]: `muted` fill (light gray), no shadow; for fields that sit on a
 *   surface such as a header, card or sheet, where a white field would vanish.
 *   The reference examples switch to it in exactly that case.
 */
enum class FieldVariant {
    PRIMARY,
    SECONDARY,
}

/** Whether the field draws the field shadow stack outside its clip. */
internal val FieldVariant.shadowed: Boolean
    get() = this == FieldVariant.PRIMARY

/**
 * The field's resting fill: [field] (the component's own field colour, usually
 * `inputColors.background`) for [FieldVariant.PRIMARY], `muted` for [FieldVariant.SECONDARY].
 */
@Composable
internal fun FieldVariant.fill(field: Color): Color = when (this) {
    FieldVariant.PRIMARY -> field
    FieldVariant.SECONDARY -> Theme.colors.muted
}

/**
 * Fill of a tappable field trigger while pressed. The primary field darkens to `muted`;
 * the secondary field already sits on `muted`, so it keeps its fill and the border
 * hover colour carries the press.
 */
@Composable
internal fun FieldVariant.pressedFill(field: Color): Color = when (this) {
    FieldVariant.PRIMARY -> Theme.colors.muted
    FieldVariant.SECONDARY -> fill(field)
}
