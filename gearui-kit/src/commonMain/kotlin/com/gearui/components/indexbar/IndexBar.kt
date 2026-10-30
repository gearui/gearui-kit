package com.gearui.components.indexbar

import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.input.pointer.PointerEventPass
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.gearui.system.Haptics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.offset
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.semantics.clearAndSetSemantics
import com.tencent.kuikly.compose.ui.semantics.contentDescription
import com.tencent.kuikly.compose.ui.semantics.onClick
import com.tencent.kuikly.compose.ui.semantics.role
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * IndexBar — the letter strip down the edge of a contact or city list.
 *
 * Tap a letter, or drag along the strip, to jump to its section: [onSelect] fires each
 * time the letter under the finger changes, and the list scrolls itself (typically
 * `listState.scrollToItem(indexOfHeader)`). While the finger is down, a bubble beside the
 * strip shows the letter, as the WeChat contact list does. [active] is the section at the
 * top of the list, drawn in the accent colour.
 *
 * Place it over the list, aligned to the end:
 *
 * ```kotlin
 * Box {
 *     GearLazyColumn(state = listState) { … }
 *     IndexBar(letters, active = current, onSelect = { jumpTo(it) },
 *         modifier = Modifier.align(Alignment.CenterEnd))
 * }
 * ```
 *
 * Only the strip takes touches; the bubble's column lets them through to the list.
 * Sorting names by pinyin, including surnames with more than one reading, is the app's.
 */
@Composable
fun IndexBar(
    indexes: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    active: String? = null,
) {
    // One letter indexes nothing — the list is already there — and a lone letter
    // floating mid-edge reads as a rendering error.
    if (indexes.size < 2) return
    val colors = Theme.colors
    val density = LocalDensity.current
    val select by rememberUpdatedState(onSelect)
    var touched by remember(indexes) { mutableStateOf<Int?>(null) }
    val itemPx = with(density) { ControlGeometry.indexBarItemHeight.toPx() }

    fun touch(y: Float) {
        val index = indexAt(y, itemPx, indexes.size) ?: return
        if (index != touched) {
            touched = index
            // The finger covers the letter: the tap is how the user knows it moved.
            Haptics.selectionChanged()
            select(indexes[index])
        }
    }

    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        // The bubble rides level with the touched letter, beside the strip, where the
        // finger does not cover it.
        Box(modifier = Modifier.width(ControlGeometry.indexBarBubbleSize)) {
            touched?.let { index ->
                val center = ControlGeometry.indexBarItemHeight * index + ControlGeometry.indexBarItemHeight / 2
                Box(
                    modifier = Modifier
                        .offset(y = center - ControlGeometry.indexBarBubbleSize / 2)
                        .size(ControlGeometry.indexBarBubbleSize)
                        .clip(CircleShape)
                        .background(colors.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = indexes[index], style = Theme.typography.titleLarge, color = colors.primaryForeground)
                }
            }
        }
        Spacer(Modifier.width(Spacing.sm))
        Column(
            modifier = Modifier
                .width(ControlGeometry.indexBarWidth)
                // One gesture from press to release: a tap and a slide are the same
                // touch. The press is taken even if consumed — the list underneath may see
                // it first, and a press on the strip belongs to the strip.
                .pointerInput(indexes) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        down.consume()
                        touch(down.position.y)
                        while (true) {
                            val change = awaitPointerEvent(PointerEventPass.Main).changes.firstOrNull() ?: break
                            if (!change.pressed) break
                            change.consume()
                            touch(change.position.y)
                        }
                        touched = null
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            indexes.forEachIndexed { i, letter ->
                val pressed = i == touched
                val on = letter == active || pressed
                Box(
                    modifier = Modifier
                        .height(ControlGeometry.indexBarItemHeight)
                        .width(ControlGeometry.indexBarWidth)
                        // Each letter is its own element for a screen reader; the strip's
                        // gesture serves touch.
                        .clearAndSetSemantics {
                            role = Role.Button
                            contentDescription = letter
                            onClick(label = null) { select(letter); true }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    // The letter under the finger is marked, not only tinted.
                    Box(
                        Modifier.size(ControlGeometry.indexBarItemHeight).clip(CircleShape)
                            .background(if (pressed) colors.primary else Color.Transparent),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = letter,
                            style = Theme.typography.bodyExtraSmall,
                            color = when {
                                pressed -> colors.primaryForeground
                                on -> colors.primarySoftForeground
                                else -> colors.mutedForeground
                            },
                        )
                    }
                }
            }
        }
    }
}

/** The index under [y] (pixels from the strip's top), clamped to the strip. */
internal fun indexAt(y: Float, itemPx: Float, count: Int): Int? {
    if (count == 0 || itemPx <= 0f) return null
    return (y / itemPx).toInt().coerceIn(0, count - 1)
}

/**
 * The index letter a name files under: its first character if that is a Latin letter,
 * uppercased, otherwise "#". Chinese names need their pinyin initial, which the app
 * supplies (for example from a pinyin library) before calling this.
 */
fun indexLetterOf(name: String): String {
    val first = name.firstOrNull()?.uppercaseChar() ?: return "#"
    return if (first in 'A'..'Z') first.toString() else "#"
}

/** A to Z and "#", the usual contact index. */
val AlphabetIndexes: List<String> = ('A'..'Z').map { it.toString() } + "#"
