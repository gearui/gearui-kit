package com.gearui.components.segmented

import androidx.compose.runtime.*
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.motion.Motion
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.animation.core.Animatable
import com.tencent.kuikly.compose.animation.core.spring
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.IntOffset
import com.tencent.kuikly.compose.ui.unit.IntSize
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * SegmentedControl — HeroUI Native Tabs, primary variant.
 *
 * Reference `tabs.css`: a `default` (light gray) pill track with 3 of padding and 4
 * between triggers; the selected trigger is a `segment` (white) pill that springs
 * to its place (stiffness 1200, damping 120); labels are medium weight, the
 * selected one in the foreground colour and the rest muted. No border.
 */
@Composable
fun <T> SegmentedControl(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    labelProvider: (T) -> String = { it.toString() }
) {
    val colors = Theme.colors
    SegmentedTrack(
        count = options.size,
        selectedIndex = options.indexOf(selectedOption),
        enabled = enabled,
        modifier = modifier,
        onSelect = { onOptionSelected(options[it]) },
    ) { index, selected ->
        Text(
            text = labelProvider(options[index]),
            style = Theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = if (selected) colors.foreground else colors.mutedForeground,
            maxLines = 1,
        )
    }
}

/**
 * IconSegmentedControl - segmented control with icons, same track and indicator.
 */
@Composable
fun <T> IconSegmentedControl(
    options: List<SegmentedOption<T>>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = Theme.colors
    SegmentedTrack(
        count = options.size,
        selectedIndex = options.indexOfFirst { it.value == selectedOption },
        enabled = enabled,
        modifier = modifier,
        onSelect = { onOptionSelected(options[it].value) },
    ) { index, selected ->
        val option = options[index]
        Row(
            horizontalArrangement = Arrangement.spacedBy(ControlGeometry.tabsTriggerPaddingBlock),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            option.icon?.invoke()
            Text(
                text = option.label,
                style = Theme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = if (selected) colors.foreground else colors.mutedForeground,
                maxLines = 1,
            )
        }
    }
}

/** Reference spring for the sliding indicator, honouring reduced motion. */
internal fun tabsIndicatorSpring(motion: Motion) =
    spring<Float>(
        dampingRatio = FeedbackDefaults.tabsIndicatorDamping /
            (2f * sqrt(FeedbackDefaults.tabsIndicatorStiffness)),
        stiffness = FeedbackDefaults.tabsIndicatorStiffness,
    ).takeIf { motion.normal > 0 }

/** Left edge of segment [index] when [count] equal segments share [width] with [gap] between them. */
internal fun segmentOffset(width: Float, count: Int, gap: Float, index: Int): Pair<Float, Float> {
    if (count <= 0 || width <= 0f) return 0f to 0f
    val segment = (width - gap * (count - 1)) / count
    return index.coerceIn(0, count - 1) * (segment + gap) to segment
}

@Composable
private fun SegmentedTrack(
    count: Int,
    selectedIndex: Int,
    enabled: Boolean,
    modifier: Modifier,
    onSelect: (Int) -> Unit,
    segment: @Composable (index: Int, selected: Boolean) -> Unit,
) {
    val colors = Theme.colors
    val motion = Theme.motion
    val density = LocalDensity.current
    val pill = Theme.shapes.full
    var inner by remember { mutableStateOf(IntSize.Zero) }
    val gapPx = with(density) { ControlGeometry.tabsListGap.toPx() }
    val (targetX, segmentPx) = segmentOffset(inner.width.toFloat(), count, gapPx, selectedIndex)
    val x = remember { Animatable(targetX) }
    var placed by remember { mutableStateOf(false) }
    LaunchedEffect(targetX, inner) {
        if (inner.width == 0) return@LaunchedEffect
        val spec = tabsIndicatorSpring(motion)
        if (!placed || spec == null) {
            x.snapTo(targetX)
            placed = true
        } else {
            x.animateTo(targetX, spec)
        }
    }

    Box(
        modifier = modifier
            .clip(pill)
            .background(colors.muted)
            .padding(ControlGeometry.tabsListPadding)
            .alpha(if (enabled) 1f else FeedbackDefaults.disabledOpacity)
            .onSizeChanged { inner = it }
    ) {
        if (selectedIndex >= 0 && inner.width > 0) {
            Box(
                Modifier
                    .offset { IntOffset(x.value.roundToInt(), 0) }
                    .size(
                        width = with(density) { segmentPx.toDp() },
                        height = with(density) { inner.height.toDp() },
                    )
                    .clip(pill)
                    .background(colors.segment)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(ControlGeometry.tabsListGap)) {
            repeat(count) { index ->
                val selected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(pill)
                        .clickable(
                            enabled = enabled && !selected,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onSelect(index) }
                        // Segments share the width equally, so the reference inline
                        // padding (which sizes a hugging trigger) would only take room
                        // from the label; keep the block padding that sets the height.
                        .padding(vertical = ControlGeometry.tabsTriggerPaddingBlock),
                    contentAlignment = Alignment.Center,
                ) {
                    segment(index, selected)
                }
            }
        }
    }
}

/**
 * SegmentedOption - segment option data class
 */
data class SegmentedOption<T>(
    val value: T,
    val label: String,
    val icon: (@Composable () -> Unit)? = null
)
