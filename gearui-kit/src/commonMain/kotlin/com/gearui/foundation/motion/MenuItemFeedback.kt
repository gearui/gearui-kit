package com.gearui.foundation.motion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.animation.core.animateFloatAsState
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.interaction.collectIsPressedAsState
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.tencent.kuikly.compose.ui.graphics.lerp

/**
 * Press feedback for a row inside an overlay list: menu, context menu, action sheet,
 * select options.
 *
 * Mirrors HeroUI Native `menu.animation.ts`: while pressed the row fades to the
 * `default` fill (danger rows to danger at 10%) and scales to 0.98, both over 150ms,
 * and returns the same way. Values come from `tokens/feedback.tokens.json`.
 *
 * The caller still owns the click handler and must pass the same [interaction] to it;
 * this modifier only draws. Place it before padding so the fill covers the whole row.
 */
@Composable
internal fun Modifier.menuItemFeedback(
    interaction: MutableInteractionSource,
    shape: Shape,
    enabled: Boolean = true,
    danger: Boolean = false,
): Modifier {
    val colors = Theme.colors
    val pressed by interaction.collectIsPressedAsState()
    val active = pressed && enabled
    val spec = tween<Float>(FeedbackDefaults.menuItemPressDuration)
    val fill by animateFloatAsState(if (active) 1f else 0f, spec)
    val scale by animateFloatAsState(if (active) FeedbackDefaults.menuItemPressScale else 1f, spec)
    val pressedFill = if (danger) {
        colors.destructive.copy(alpha = FeedbackDefaults.menuItemDangerPressOpacity)
    } else {
        colors.muted
    }
    val idle = pressedFill.copy(alpha = 0f)
    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clip(shape)
        .background(if (fill > 0f) lerp(idle, pressedFill, fill) else Color.Transparent)
}
