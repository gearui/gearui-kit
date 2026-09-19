package com.gearui.foundation.motion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.gearui.foundation.button.buttonPressScale
import com.gearui.theme.Theme
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.animation.core.animateFloatAsState
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.tencent.kuikly.compose.ui.graphics.lerp
import com.tencent.kuikly.compose.ui.platform.LocalDensity

/**
 * Press feedback for a small icon-only control drawn on the neutral fill: the clear
 * button of a field, a close glyph.
 *
 * The reference draws these as tertiary icon-only Buttons (`close-button.css`), so they
 * take the Button feedback: the width-compensated press scale (0.985 x 300/width over
 * 300ms ease-out) and the neutral highlight (the fill mixed 4% toward the foreground).
 * The caller reports [pressed]; gesture handling stays with the caller because a field's
 * clear button must not let the touch reach the native editor.
 */
@Composable
internal fun Modifier.iconPressFeedback(pressed: Boolean, size: Dp, shape: Shape): Modifier {
    val colors = Theme.colors
    val widthPx = with(LocalDensity.current) { size.toPx() }
    // Reduced motion shortens these to zero, as for Button.
    val pressDuration = Theme.motion.feedbackDuration(FeedbackDefaults.pressDuration)
    val highlightDuration = Theme.motion.feedbackDuration(FeedbackDefaults.highlightDuration)
    val scale by animateFloatAsState(
        buttonPressScale(widthPx, pressed, true, pressDuration),
        tween(pressDuration, easing = FeedbackDefaults.pressEasing),
    )
    val highlight by animateFloatAsState(
        if (pressed) FeedbackDefaults.highlightOpacity else 0f,
        tween(highlightDuration),
    )
    val pressedFill = lerp(colors.muted, colors.foreground, FeedbackDefaults.neutralMix)
    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clip(shape)
        .background(lerp(colors.muted, pressedFill, highlight))
}
