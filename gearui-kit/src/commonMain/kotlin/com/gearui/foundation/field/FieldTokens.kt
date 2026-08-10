package com.gearui.foundation.field

import androidx.compose.runtime.Composable
import com.gearui.foundation.layout.Spacing
import com.gearui.theme.Theme
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Field size tokens — shared by every input-like trigger: Input, SearchBar,
 * DatePickerInput, Select, Cascader, TreeSelect.
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
        borderWidth = 1.dp,
    )

    val Medium = FieldTokens(
        height = 40.dp,
        paddingHorizontal = Spacing.md,
        borderWidth = 1.dp,
    )

    val Small = FieldTokens(
        height = 32.dp,
        paddingHorizontal = Spacing.sm,
        borderWidth = 1.dp,
    )
}

object FieldDefaults {
    /** Default trigger shape for input-like controls (`Shapes.md`, 6dp). */
    val shape: Shape
        @Composable get() = Theme.shapes.md

    /** Compact variants only — keeps small controls from looking over-rounded. */
    val compactShape: Shape
        @Composable get() = Theme.shapes.sm
}
