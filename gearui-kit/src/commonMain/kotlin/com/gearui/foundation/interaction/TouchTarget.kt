package com.gearui.foundation.interaction

import com.gearui.foundation.control.ControlGeometry
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.layout.layout
import com.tencent.kuikly.compose.foundation.layout.sizeIn
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

/**
 * Makes the node itself at least [min] each way — the hit region. Put it on the node that
 * carries the click handler and semantics, and draw the control in a child: the Box or Row
 * centres the child, so the control looks as designed and takes a tap anywhere in [min].
 * Visual modifiers (clip, background, border) belong on the child, or they grow too.
 *
 * Apple's rule (HIG, Buttons) is a hit region of at least 44×44 pt, whatever the
 * control's visual size; GearUI's spec takes the same floor. It must be the node's own
 * measured size: KuiklyUI sizes the native view from what the node's content measured,
 * so a layout modifier reporting a larger size around a smaller node leaves the native
 * view — and so the touch area — at the smaller size. It also occupies the space, because
 * a KuiklyUI node cannot take touches outside its parent (unlike [touchTarget]'s overhang).
 */
internal fun Modifier.hitTarget(min: Dp = ControlGeometry.selectionTouchTarget): Modifier =
    sizeIn(minWidth = min, minHeight = min)
