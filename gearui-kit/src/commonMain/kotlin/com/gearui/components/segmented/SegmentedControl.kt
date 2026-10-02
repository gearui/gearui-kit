package com.gearui.components.segmented

import com.gearui.components.icon.IconSource
import com.gearui.foundation.interaction.hitTarget
import com.gearui.foundation.interaction.choiceSemantics
import androidx.compose.runtime.*
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.foundation.motion.Motion
import com.tencent.kuikly.compose.foundation.interaction.collectIsPressedAsState
import com.gearui.foundation.interaction.pressScale
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.semantics.role
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
import com.tencent.kuikly.compose.ui.zIndex
import com.tencent.kuikly.compose.ui.layout.Layout
import com.tencent.kuikly.compose.ui.unit.Constraints
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.gearui.foundation.layout.Spacing
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
        label = { labelProvider(options[it]) },
    ) { index, selected ->
        Text(
            text = labelProvider(options[index]),
            style = Theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = if (selected) colors.foreground else colors.mutedForeground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
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
        label = { options[it].label },
    ) { index, selected ->
        val option = options[index]
        // Icon and label share one colour, so the icon follows the selection with the text.
        val contentColor = if (selected) colors.foreground else colors.mutedForeground
        Row(
            horizontalArrangement = Arrangement.spacedBy(ControlGeometry.tabsTriggerPaddingBlock),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (option.icon != null) {
                Icon(option.icon, size = IconSizes.Default.md, tint = contentColor)
            }
            Text(
                text = option.label,
                style = Theme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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

/**
 * Segment widths, as iOS lays out a segmented control: equal while every label fits its
 * equal share; otherwise each segment gets its own content width and the rest of the
 * track is shared out equally (UIKit's `apportionsSegmentWidthsByContent`). When the
 * contents do not fit with their padding, the padding gives way first, down to
 * [tightPadding]; only then do the segments shrink in proportion, ending in "…".
 *
 * [natural] is each segment's content width including [padding] (both sides);
 * [available] is the track width less the gaps. A label is never drawn past its segment.
 */
internal fun segmentWidths(natural: List<Int>, available: Int, padding: Int = 0, tightPadding: Int = padding): List<Int> {
    val count = natural.size
    if (count == 0 || available <= 0) return List(count) { 0 }
    val equal = available / count
    if (natural.all { it <= equal }) return share(List(count) { 0 }, available)
    if (natural.sum() <= available) return share(natural, available)
    val tight = natural.map { (it - padding + tightPadding).coerceAtLeast(0) }
    if (tight.sum() <= available) return share(tight, available)
    val needed = tight.sum().coerceAtLeast(1)
    return share(tight.map { (it.toLong() * available / needed).toInt() }, available)
}

/** [base] plus an equal share of what is left of [available], the remainder to the first segments. */
private fun share(base: List<Int>, available: Int): List<Int> {
    val extra = (available - base.sum()).coerceAtLeast(0)
    return base.mapIndexed { i, w -> w + extra / base.size + if (i < extra % base.size) 1 else 0 }
}

@Composable
internal fun SegmentedTrack(
    count: Int,
    selectedIndex: Int,
    enabled: Boolean,
    modifier: Modifier,
    onSelect: (Int) -> Unit,
    /** Each segment's name for screen readers; the drawn segment may be an icon. */
    label: (Int) -> String,
    segment: @Composable (index: Int, selected: Boolean) -> Unit,
) {
    val colors = Theme.colors
    val motion = Theme.motion
    val density = LocalDensity.current
    val pill = Theme.shapes.full
    var inner by remember { mutableStateOf(IntSize.Zero) }
    val gapPx = with(density) { ControlGeometry.tabsListGap.toPx() }
    // Measured by the label row below; the indicator and the hit columns follow it.
    var widths by remember(count) { mutableStateOf(emptyList<Int>()) }
    val segmentStart = { index: Int -> widths.take(index).sum() + gapPx.roundToInt() * index }
    val targetX = if (selectedIndex in widths.indices) segmentStart(selectedIndex).toFloat() else 0f
    val segmentPx = widths.getOrElse(selectedIndex) { 0 }
    val x = remember { Animatable(targetX) }
    var placed by remember { mutableStateOf(false) }
    LaunchedEffect(targetX, inner) {
        if (inner.width == 0 || widths.isEmpty()) return@LaunchedEffect
        val spec = tabsIndicatorSpring(motion)
        if (!placed || spec == null) {
            x.snapTo(targetX)
            placed = true
        } else {
            x.animateTo(targetX, spec)
        }
    }

    val interactions = remember(count) { List(count) { MutableInteractionSource() } }
    Box(
        modifier = modifier
            // HIG hit region: the track is drawn at its height inside a node at least 44
            // tall; the segments' hit columns below span all of it.
            .hitTarget(),
        contentAlignment = Alignment.Center,
    ) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(pill)
            .background(colors.muted)
            .padding(ControlGeometry.tabsListPadding)
            .alpha(if (enabled) 1f else FeedbackDefaults.disabledOpacity)
            .onSizeChanged { inner = it }
    ) {
        if (selectedIndex >= 0 && inner.width > 0 && segmentPx > 0) {
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
        // Web mounts the indicator after measurement, so DOM insertion order
        // alone can put its surface above the already-mounted labels.
        Layout(
            modifier = Modifier.zIndex(1f).fillMaxWidth(),
            content = {
                repeat(count) { index ->
                    val selected = index == selectedIndex
                    val pressed by interactions[index].collectIsPressedAsState()
                    Box(
                        modifier = Modifier
                            .pressScale(pressed && enabled)
                            .clip(pill)
                            // Inline padding is part of the width rule below, not of the box: it
                            // gives way on a narrow track, and the label is centred in what is left.
                            .padding(vertical = ControlGeometry.tabsTriggerPaddingBlock),
                        contentAlignment = Alignment.Center,
                    ) {
                        segment(index, selected)
                    }
                }
            },
        ) { measurables, constraints ->
            val gap = gapPx.roundToInt()
            val available = constraints.maxWidth - gap * (count - 1)
            val padding = Spacing.md.roundToPx() * 2
            val natural = measurables.map { it.maxIntrinsicWidth(constraints.maxHeight) + padding }
            val sized = segmentWidths(natural, available, padding = padding,
                tightPadding = ControlGeometry.tabsListGap.roundToPx() * 2)
            if (sized != widths) widths = sized
            val placeables = measurables.mapIndexed { i, m ->
                m.measure(Constraints(minWidth = sized[i], maxWidth = sized[i], maxHeight = constraints.maxHeight))
            }
            val height = placeables.maxOfOrNull { it.height } ?: 0
            layout(constraints.maxWidth, height) {
                var left = 0
                placeables.forEach { p ->
                    p.place(left, (height - p.height) / 2)
                    left += p.width + gap
                }
            }
        }
    }
    // Hit columns over the whole 44: one per segment, aligned with the drawn ones.
    Row(
        modifier = Modifier.matchParentSize().padding(horizontal = ControlGeometry.tabsListPadding).zIndex(2f),
        horizontalArrangement = Arrangement.spacedBy(ControlGeometry.tabsListGap),
    ) {
        repeat(count) { index ->
            val selected = index == selectedIndex
            Box(
                Modifier
                    .then(widths.getOrNull(index)?.let { Modifier.width(with(density) { it.toDp() }) } ?: Modifier.weight(1f))
                    .fillMaxHeight()
                    .choiceSemantics(
                        label = label(index),
                        selected = selected,
                        role = Role.Tab,
                        // Still a button when selected, as the platform's segments are; tapping it is a no-op.
                        onClick = if (enabled) ({ if (!selected) onSelect(index) }) else null,
                    )
                    .clickable(
                        enabled = enabled && !selected,
                        interactionSource = interactions[index],
                        indication = null,
                    ) { onSelect(index) }
            )
        }
    }
    }
}

/**
 * SegmentedOption - one option of an [IconSegmentedControl].
 *
 * @param icon an icon name from `com.gearui.components.icon.Icons`. The control draws
 *   and tints it itself with the label's colour, so it follows the selection.
 */
data class SegmentedOption<T>(
    val value: T,
    val label: String,
    val icon: IconSource? = null
)
