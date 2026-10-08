package com.gearui.foundation.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gearui.foundation.interaction.activationTarget
import com.gearui.foundation.interaction.rememberActivationTracker
import com.gearui.foundation.motion.rowPressFeedback
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.RectangleShape

/**
 * Makes a row tappable with the press a [com.gearui.components.cell.Cell] row has, for a
 * row of an app's own layout — a chat in a conversation list, an order in an order list.
 *
 * The press fills the row edge to edge, without scaling, and shows only once the finger
 * rests for the row-press delay, so a scroll that starts on the row does not flash it; a
 * quick tap still flashes it. Inside a container that hands rows their interaction (a
 * CellGroup), the fill runs through the separators either side, as platform lists do.
 *
 * [background] is the row's own fill, which the press is laid over: pass the row's
 * colour (a pinned chat's tint, say) rather than also setting `background` on the row —
 * a KuiklyUI view has one background, and a second one would hide the press.
 */
@Composable
fun Modifier.rowClickable(
    onClick: () -> Unit,
    background: Color = Theme.colors.surface,
    enabled: Boolean = true,
): Modifier {
    val interaction = LocalRowInteractionSource.current ?: remember { MutableInteractionSource() }
    val activation = rememberActivationTracker()
    return this
        .rowPressFeedback(interaction = interaction, shape = RectangleShape, enabled = enabled, scale = false, base = background)
        .activationTarget(activation)
        .clickable(interactionSource = interaction, indication = null, enabled = enabled) {
            activation.mark()
            onClick()
        }
}
