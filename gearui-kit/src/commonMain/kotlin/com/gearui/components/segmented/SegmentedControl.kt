package com.gearui.components.segmented

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
                Icon(name = option.icon, size = IconSizes.Default.md, tint = contentColor)
            }
            Text(
                text = option.label,
                style = Theme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = contentColor,
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
        // Web mounts the indicator after measurement, so DOM insertion order
        // alone can put its surface above the already-mounted labels.
        Row(
            modifier = Modifier.zIndex(1f),
            horizontalArrangement = Arrangement.spacedBy(ControlGeometry.tabsListGap),
        ) {
            repeat(count) { index ->
                val selected = index == selectedIndex
                val pressed by interactions[index].collectIsPressedAsState()
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .pressScale(pressed && enabled)
                        .clip(pill)
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
    // Hit columns over the whole 44: one per segment, aligned with the drawn ones.
    Row(
        modifier = Modifier.matchParentSize().padding(horizontal = ControlGeometry.tabsListPadding).zIndex(2f),
        horizontalArrangement = Arrangement.spacedBy(ControlGeometry.tabsListGap),
    ) {
        repeat(count) { index ->
            val selected = index == selectedIndex
            Box(
                Modifier
                    .weight(1f)
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
    val icon: String? = null
)
