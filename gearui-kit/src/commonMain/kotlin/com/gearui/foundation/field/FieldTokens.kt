package com.gearui.foundation.field

import androidx.compose.runtime.Composable
import com.gearui.theme.Theme
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.typography.IconSizes
import com.gearui.foundation.control.ControlGeometry

/**
 * Field size tokens — shared by every input-like trigger.
 *
 * The family splits in two, and the split decides the API surface:
 *
 *  - **Form fields** — Input, Textarea, Select, MultiSelect, Cascader,
 *    TreeSelect, DatePickerInput, TimePickerInput. They hold a value that can
 *    be validated, so they take `enabled: Boolean = true` *and*
 *    `error: String? = null`.
 *  - **Search fields** — SearchBar. It takes `enabled` but deliberately no
 *    `error`: there is no value to validate, and giving it one would widen the
 *    API to make two different things look alike.
 *
 * Geometry below is shared by both.
 *
 * Geometry is generated from the HeroUI Native reference profile. Regular
 * controls are 48dp high with 12dp horizontal padding. See
 * tokens/controls.tokens.json for the single source of values.
 *
 * ### Why there is no focus-state border here
 *
 * A shared field token set invites a `focusBorderWidth` / `focusedBorderColor`
 * pair. Do not add them. Input carries two constraints learned the hard way on
 * Kuikly, and both are load-bearing:
 *
 *  - Border *colour* must not depend on focus. Rebuilding the modifier chain
 *    at the moment focus changes recreates the underlying EditText, and when
 *    that happens mid-tap it shows up as intermittent focus loss.
 *  - Border *width* must stay constant across focus and error, otherwise the
 *    field's content box resizes and the layout jumps.
 *
 * FieldFocusOverlay updates a sibling decoration for touch and keyboard focus.
 * It never rebuilds the native input modifier chain or changes layout size.
 *
 * The removed pre-1.0 InputTokens did declare `focusBorderWidth = 2f`. Nothing
 * ever read it — which is precisely why it survived long enough to look like a
 * reasonable thing to generalise.
 *
 * Colors are NOT defined here — components read from `Theme.colors`.
 */
data class FieldTokens(
    val height: Dp,
    val paddingHorizontal: Dp,
    /** Constant across focus and error states — see the class doc. */
    val borderWidth: Dp,
)

object FieldSizeTokens {

    val Large = FieldTokens(
        height = ControlGeometry.controlLarge,
        paddingHorizontal = ControlGeometry.fieldPaddingLarge,
        borderWidth = BorderWidth.thin,
    )

    val Medium = FieldTokens(
        height = ControlGeometry.controlMedium,
        paddingHorizontal = ControlGeometry.fieldPaddingMedium,
        borderWidth = BorderWidth.thin,
    )

    val Small = FieldTokens(
        height = ControlGeometry.controlSmall,
        paddingHorizontal = ControlGeometry.fieldPaddingSmall,
        borderWidth = BorderWidth.thin,
    )
}

object FieldDefaults {
    /**
     * Size of the trailing affordance on a field trigger — the chevron on
     * Select/Cascader/TreeSelect, the calendar and clock on the pickers.
     *
     * This is now just `IconSizes.Default.md`. It briefly needed its own
     * literal because the icon scale was 14/18/24 and had no 16dp step, even
     * though the whole family had settled on 16; the scale has since been
     * reconciled with actual usage. The alias stays because "the field
     * family's trailing icon" is the thing call sites mean.
     */
    val trailingIconSize: Dp = IconSizes.Default.md

    /** Default trigger shape follows the regular control radius. */
    val shape: Shape
        @Composable get() = Theme.shapes.lg

    /** Compact variants only — keeps small controls from looking over-rounded. */
    val compactShape: Shape
        @Composable get() = Theme.shapes.md

    val largeShape: Shape
        @Composable get() = Theme.shapes.controlLarge
}
