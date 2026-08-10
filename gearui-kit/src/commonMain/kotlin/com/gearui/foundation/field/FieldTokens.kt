package com.gearui.foundation.field

import androidx.compose.runtime.Composable
import com.gearui.foundation.layout.Spacing
import com.gearui.theme.Theme
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.typography.IconSizes

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
 * These all used to size themselves independently, and had drifted: most sat
 * at 40dp with a 4dp radius, Input at 40dp with 6dp, and Select at 44dp with
 * 12dp. `Shapes` documents `md` (6dp) as the input radius, so that is the one
 * they converge on.
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
 * Focus is expressed through [com.gearui.foundation.interaction.InteractionState]
 * and background/foreground colour, never by re-deciding the border.
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
        height = 48.dp,
        paddingHorizontal = Spacing.lg,
        borderWidth = BorderWidth.thin,
    )

    val Medium = FieldTokens(
        height = 40.dp,
        paddingHorizontal = Spacing.md,
        borderWidth = BorderWidth.thin,
    )

    val Small = FieldTokens(
        height = 32.dp,
        paddingHorizontal = Spacing.sm,
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

    /** Default trigger shape for input-like controls (`Shapes.md`, 6dp). */
    val shape: Shape
        @Composable get() = Theme.shapes.md

    /** Compact variants only — keeps small controls from looking over-rounded. */
    val compactShape: Shape
        @Composable get() = Theme.shapes.sm
}
