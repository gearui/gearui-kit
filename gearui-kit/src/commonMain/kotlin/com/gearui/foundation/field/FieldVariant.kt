package com.gearui.foundation.field

/**
 * Field fill, after the reference Input `variant` (`input.css`).
 *
 * - [PRIMARY]: field colour (white in light) with the field shadow; for fields on
 *   the page background.
 * - [SECONDARY]: `default` fill (light gray), no shadow; for fields that sit on a
 *   surface such as a header, card or sheet, where a white field would vanish.
 *   The reference examples switch to it in exactly that case.
 */
enum class FieldVariant {
    PRIMARY,
    SECONDARY,
}
