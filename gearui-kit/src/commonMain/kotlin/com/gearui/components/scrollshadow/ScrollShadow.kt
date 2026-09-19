package com.gearui.components.scrollshadow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.gearui.foundation.control.ControlGeometry
import com.gearui.overlay.OverlayDefaults
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.animation.core.animateFloatAsState
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.gestures.Orientation
import com.tencent.kuikly.compose.foundation.gestures.ScrollableState
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.BoxScope
import com.tencent.kuikly.compose.foundation.layout.fillMaxHeight
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.graphics.Brush
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp

/** Which edges of a [ScrollShadow] fade. AUTO fades only the edges with more content behind them. */
enum class ScrollShadowVisibility { AUTO, START, END, BOTH, NONE }

/**
 * ScrollShadow — HeroUI Native `ScrollShadow`: fades the edges of a scrolling area
 * into the background so the user can tell there is more content.
 *
 * Wrap the scrolling content and pass its state, the same one the list uses:
 *
 * ```kotlin
 * val state = rememberLazyListState()
 * ScrollShadow(state, Modifier.height(listHeight)) {
 *     GearLazyColumn(state = state) { … }
 * }
 * ```
 *
 * In [ScrollShadowVisibility.AUTO] an edge fades in only while content is hidden past
 * it, following the reference. The fade is a 50dp gradient from [color] (the page
 * background by default; pass the container colour inside a card) to transparent,
 * drawn as real views above the content.
 */
@Composable
fun ScrollShadow(
    state: ScrollableState,
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.Vertical,
    visibility: ScrollShadowVisibility = ScrollShadowVisibility.AUTO,
    size: Dp = ControlGeometry.scrollShadowSize,
    color: Color = Theme.colors.background,
    content: @Composable BoxScope.() -> Unit,
) {
    val (showStart, showEnd) = scrollShadowEdges(visibility, state.canScrollBackward, state.canScrollForward)
    val duration = if (Theme.motion.normal > 0) OverlayDefaults.exitDurationMillis else 0
    val startAlpha by animateFloatAsState(if (showStart) 1f else 0f, tween(duration))
    val endAlpha by animateFloatAsState(if (showEnd) 1f else 0f, tween(duration))
    val vertical = orientation == Orientation.Vertical
    Box(modifier) {
        content()
        if (visibility != ScrollShadowVisibility.NONE) {
            EdgeFade(color, size, vertical, atStart = true, alpha = startAlpha)
            EdgeFade(color, size, vertical, atStart = false, alpha = endAlpha)
        }
    }
}

@Composable
private fun BoxScope.EdgeFade(color: Color, size: Dp, vertical: Boolean, atStart: Boolean, alpha: Float) {
    if (alpha <= 0f) return
    val colors = if (atStart) listOf(color, color.copy(alpha = 0f)) else listOf(color.copy(alpha = 0f), color)
    val base = if (vertical) Modifier.fillMaxWidth().height(size) else Modifier.fillMaxHeight().width(size)
    Box(
        base
            .align(
                when {
                    vertical && atStart -> Alignment.TopCenter
                    vertical -> Alignment.BottomCenter
                    atStart -> Alignment.CenterStart
                    else -> Alignment.CenterEnd
                }
            )
            .alpha(alpha)
            .background(if (vertical) Brush.verticalGradient(colors) else Brush.horizontalGradient(colors))
    )
}

/** (start, end) edge visibility for a mode and the current scroll capability. */
internal fun scrollShadowEdges(
    visibility: ScrollShadowVisibility,
    canScrollBackward: Boolean,
    canScrollForward: Boolean,
): Pair<Boolean, Boolean> = when (visibility) {
    ScrollShadowVisibility.AUTO -> canScrollBackward to canScrollForward
    ScrollShadowVisibility.START -> true to false
    ScrollShadowVisibility.END -> false to true
    ScrollShadowVisibility.BOTH -> true to true
    ScrollShadowVisibility.NONE -> false to false
}
