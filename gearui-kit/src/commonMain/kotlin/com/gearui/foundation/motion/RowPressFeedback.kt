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
 * Press feedback for a tappable row: a menu or action sheet option, a cell, a list item.
 *
 * Mirrors HeroUI Native `menu.animation.ts`: while pressed the row fades to the
 * `default` fill (danger rows to danger at 10%) and scales to 0.98, both over 150ms,
 * and returns the same way. Values come from `tokens/feedback.tokens.json`.
 *
 * A row that paints its own background must pass it as [base]. Kuikly gives a view one
 * background colour — a second `.background` replaces the first rather than drawing
 * over it — so this modifier paints the base itself, with the press fill composited on
 * top, and adds nothing at all while idle on a transparent row.
 *
 * The caller still owns the click handler and must pass the same [interaction] to it;
 * this modifier only draws. Place it before padding so the fill covers the whole row.
 */
@Composable
internal fun Modifier.rowPressFeedback(
    interaction: MutableInteractionSource,
    shape: Shape,
    enabled: Boolean = true,
    danger: Boolean = false,
    /**
     * Scale the row while pressed. True for a discrete target — a menu or action sheet
     * option, which is an inset card inside the sheet. False for a row that spans a
     * card: scaling it leaves a sliver of card showing down both edges, so the press
     * stops short of the edges instead of filling the row as the platform's lists do.
     */
    scale: Boolean = true,
    /** The row's own background, if it has one. See the class note. */
    base: Color = Color.Transparent,
): Modifier {
    val colors = Theme.colors
    val pressed by interaction.collectIsPressedAsState()
    val active = pressed && enabled
    val spec = tween<Float>(FeedbackDefaults.menuItemPressDuration)
    val fill by animateFloatAsState(if (active) 1f else 0f, spec)
    val pressScale by animateFloatAsState(if (active && scale) FeedbackDefaults.menuItemPressScale else 1f, spec)
    val pressedFill = if (danger) {
        colors.destructive.copy(alpha = FeedbackDefaults.menuItemDangerPressOpacity)
    } else {
        colors.muted
    }
    val idle = pressedFill.copy(alpha = 0f)
    val paint = when {
        fill > 0f -> lerp(idle, pressedFill, fill).over(base)
        base.alpha > 0f -> base
        else -> null
    }
    return this
        .graphicsLayer {
            scaleX = pressScale
            scaleY = pressScale
        }
        .clip(shape)
        .then(if (paint != null) Modifier.background(paint) else Modifier)
}

/** Source-over: this colour drawn on top of [dst]. */
private fun Color.over(dst: Color): Color {
    if (dst.alpha == 0f) return this
    val a = alpha + dst.alpha * (1f - alpha)
    if (a == 0f) return Color.Transparent
    fun ch(s: Float, d: Float) = (s * alpha + d * dst.alpha * (1f - alpha)) / a
    return Color(ch(red, dst.red), ch(green, dst.green), ch(blue, dst.blue), a)
}
