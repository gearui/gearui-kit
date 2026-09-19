package com.gearui.foundation.interaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.foundation.button.buttonPressScale
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.motion.feedbackDuration
import com.gearui.theme.DefaultPalette
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.animation.core.animateFloatAsState
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.interaction.collectIsPressedAsState
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.BoxScope
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.RectangleShape
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.tencent.kuikly.compose.ui.graphics.lerp
import com.tencent.kuikly.compose.ui.graphics.luminance
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.semantics.Role

/**
 * PressableFeedback — HeroUI Native `PressableFeedback`.
 *
 * Makes any content tappable with the reference press feedback:
 *
 * - **Scale**: 0.985, compensated for width (`1 - 0.015 * 300 / width`), over 300ms
 *   ease-out. Small targets visibly shrink; wide cards barely move.
 * - **Highlight**: an overlay in `#3f3f46` (light) / `#d4d4d8` (dark) fading from 0 to
 *   10% over 200ms, stacked *above* the content so it also reads on images and colour.
 *
 * Both honour reduced motion.
 *
 * The highlight is a real view on top of [content]. On Kuikly every child is its own
 * native view layered above its parent's canvas, so an overlay drawn by a modifier on
 * the parent would end up underneath the children.
 *
 * ```kotlin
 * PressableFeedback(onClick = { open(order) }, shape = Theme.shapes.xl) {
 *     OrderRow(order)
 * }
 * ```
 */
@Composable
fun PressableFeedback(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    scale: Boolean = true,
    highlight: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val active = pressed && enabled
    val amount = highlightAmount(highlight && active)
    val overlay = pressHighlightColor()
    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else FeedbackDefaults.disabledOpacity)
            .pressScale(pressed = active && scale)
            .clip(shape)
            .clickable(
                enabled = enabled,
                interactionSource = source,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        content()
        if (highlight) {
            Box(
                Modifier
                    .matchParentSize()
                    .background(overlay.copy(alpha = overlay.alpha * FeedbackDefaults.pressHighlightOpacity * amount))
            )
        }
    }
}

/**
 * The press scale alone, for content that is not a surface (a text link) or that
 * tints its own fill with [pressedSurfaceColor]. Pass the same [interactionSource] to
 * the caller's click handler.
 */
@Composable
fun Modifier.pressScale(interactionSource: MutableInteractionSource, enabled: Boolean = true): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    return pressScale(pressed = pressed && enabled)
}

/**
 * The fill a pressable surface shows: [base] blended toward the press highlight
 * colour by 10% while [pressed], animated over 200ms. For nodes that paint their own
 * background and drive [pressed] themselves (a field's clear button keeps the touch
 * from the native editor). Content-wide highlight needs [PressableFeedback].
 */
@Composable
fun pressedSurfaceColor(base: Color, pressed: Boolean): Color {
    val amount = highlightAmount(pressed)
    return lerp(base, pressHighlightColor(), FeedbackDefaults.pressHighlightOpacity * amount)
}

/**
 * Width-compensated press scale driven by an explicit [pressed] state. Width is
 * measured in dp, as the reference formula expects.
 */
@Composable
fun Modifier.pressScale(pressed: Boolean): Modifier {
    var widthDp by remember { mutableStateOf(0f) }
    val density = LocalDensity.current.density
    val duration = Theme.motion.feedbackDuration(FeedbackDefaults.pressDuration)
    val target = buttonPressScale(widthDp, pressed, true, duration)
    val current by animateFloatAsState(target, tween(duration, easing = FeedbackDefaults.pressEasing))
    return this
        .onSizeChanged { widthDp = it.width / density }
        .graphicsLayer {
            scaleX = current
            scaleY = current
        }
}

@Composable
private fun highlightAmount(on: Boolean): Float {
    val duration = Theme.motion.feedbackDuration(FeedbackDefaults.highlightDuration)
    val amount by animateFloatAsState(if (on) 1f else 0f, tween(duration))
    return amount
}

@Composable
private fun pressHighlightColor(): Color =
    if (Theme.colors.background.luminance() < 0.5f) DefaultPalette.darkPressHighlight else DefaultPalette.lightPressHighlight
