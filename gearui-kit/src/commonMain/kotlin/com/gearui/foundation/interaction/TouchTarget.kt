package com.gearui.foundation.interaction

import com.gearui.foundation.control.ControlGeometry
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.layout.layout
import com.tencent.kuikly.compose.ui.unit.Constraints
import com.tencent.kuikly.compose.ui.unit.Dp

/**
 * Gives a small control a touch area of at least [target] without moving anything:
 * the node is measured [target] square but reports [visual] to its parent, overflowing
 * it evenly on every side. Modifiers after this one (the click handler, semantics)
 * cover the whole target; draw the control's visual part in a [visual]-sized child.
 *
 * Compose widens small targets for touch on its own; KuiklyUI does not, so a 32dp
 * close button took taps only inside its 32dp.
 */
internal fun Modifier.touchTarget(visual: Dp, target: Dp = ControlGeometry.selectionTouchTarget): Modifier =
    layout { measurable, _ ->
        val v = visual.roundToPx()
        val t = maxOf(target.roundToPx(), v)
        val placeable = measurable.measure(Constraints.fixed(t, t))
        layout(v, v) { placeable.place((v - t) / 2, (v - t) / 2) }
    }
