package com.gearui.foundation.field

import com.tencent.kuikly.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import com.gearui.foundation.border.BorderWidth
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.drawWithContent
import com.tencent.kuikly.compose.ui.geometry.Size
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.graphics.drawOutline
import com.tencent.kuikly.compose.ui.graphics.drawscope.Stroke
import com.tencent.kuikly.compose.ui.graphics.drawscope.translate

/** Read transient state during drawing, never rebuild the native input's modifier on focus. */
@Composable
internal fun rememberInputFeedback(
    colors: InputColors,
    shape: Shape,
    focused: State<Boolean>,
    hovered: State<Boolean>,
    enabled: Boolean,
    error: Color?,
): Modifier {
    return remember(colors, shape, focused, hovered, enabled, error) {
        Modifier.drawWithContent {
            drawContent()
            val border = BorderWidth.thin.toPx()
            val frame = size
            if (frame.width <= border || frame.height <= border) return@drawWithContent
            translate(border / 2, border / 2) {
                drawOutline(
                    shape.createOutline(Size(frame.width - border, frame.height - border), layoutDirection, this),
                    inputBorderColor(colors, enabled, focused.value, hovered.value, error),
                    style = Stroke(border),
                )
            }
            val focusColor = inputFocusColor(colors, enabled, focused.value, error)
            if (focusColor != null) {
                val ring = BorderWidth.thick.toPx()
                if (frame.width <= ring || frame.height <= ring) return@drawWithContent
                // Kuikly clips drawing to the native view bounds. Keep the ring
                // inside those bounds without changing padding or text geometry.
                translate(ring / 2, ring / 2) {
                    drawOutline(
                        shape.createOutline(Size(frame.width - ring, frame.height - ring), layoutDirection, this),
                        focusColor,
                        style = Stroke(ring),
                    )
                }
            }
        }
    }
}

/** Isolate native-view decoration updates from the text input's modifier chain. */
@Composable
internal fun com.tencent.kuikly.compose.foundation.layout.BoxScope.FieldFocusOverlay(
    colors: InputColors,
    shape: Shape,
    focused: State<Boolean>,
    enabled: Boolean,
    error: Color?,
) {
    com.tencent.kuikly.compose.foundation.layout.Box(
        Modifier.matchParentSize().border(
            BorderWidth.thick,
            inputFocusColor(colors, enabled, focused.value, error) ?: Color.Transparent,
            shape,
        )
    )
}
