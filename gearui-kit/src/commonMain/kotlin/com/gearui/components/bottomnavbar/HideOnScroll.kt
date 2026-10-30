package com.gearui.components.bottomnavbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.scroll.ScrollState
import com.tencent.kuikly.compose.foundation.lazy.LazyListState
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.Dp

/**
 * Whether a bar should show while [state] scrolls: hidden while the content moves up (the
 * user reads on), shown again as soon as it moves down or reaches the top. Pass it as
 * [BottomNavBar]'s `visible`, so a list gets the screen while it is read — iOS 26's tab
 * bar does the same. Movements under [threshold] are ignored, so a resting finger does
 * not flicker the bar.
 */
@Composable
fun rememberHideOnScroll(state: LazyListState, threshold: Dp = Spacing.md): Boolean {
    val thresholdPx = with(LocalDensity.current) { threshold.roundToPx() }
    var visible by remember(state) { mutableStateOf(true) }
    LaunchedEffect(state, thresholdPx) {
        var anchor = ScrollPosition(state.firstVisibleItemIndex, state.firstVisibleItemScrollOffset)
        snapshotFlow { ScrollPosition(state.firstVisibleItemIndex, state.firstVisibleItemScrollOffset) }
            .collect { current ->
                val next = barVisibility(anchor, current, visible, thresholdPx)
                // Measure the next movement from where the bar last changed — or from
                // here, once the movement is past the threshold either way.
                if (next != visible || movedBeyond(anchor, current, thresholdPx)) anchor = current
                visible = next
            }
    }
    return visible
}

/** [rememberHideOnScroll] for a kit [ScrollState] (a ScrollView's). */
@Composable
fun rememberHideOnScroll(state: ScrollState, threshold: Dp = Spacing.md): Boolean =
    rememberHideOnScroll(state.raw, threshold)

internal data class ScrollPosition(val index: Int, val offset: Int)

/**
 * The bar's next visibility. At the top it always shows. Item indices decide first — a
 * later item means the content moved up — then offsets within the same item, compared
 * against [thresholdPx].
 */
internal fun barVisibility(from: ScrollPosition, to: ScrollPosition, visible: Boolean, thresholdPx: Int): Boolean {
    if (to.index == 0 && to.offset <= thresholdPx) return true
    return when {
        to.index > from.index -> false
        to.index < from.index -> true
        to.offset - from.offset > thresholdPx -> false
        from.offset - to.offset > thresholdPx -> true
        else -> visible
    }
}

private fun movedBeyond(from: ScrollPosition, to: ScrollPosition, thresholdPx: Int): Boolean =
    to.index != from.index || kotlin.math.abs(to.offset - from.offset) > thresholdPx
