package com.gearui.components.tabs

import com.gearui.components.segmented.SegmentedTrack
import com.tencent.kuikly.compose.foundation.layout.offset
import com.gearui.foundation.control.ControlGeometry
import com.gearui.components.segmented.segmentOffset
import com.gearui.components.segmented.tabsIndicatorSpring
import com.tencent.kuikly.compose.animation.core.Animatable
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.IntOffset
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import kotlin.math.roundToInt
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.GearLazyRow
import com.gearui.foundation.primitives.Text
import com.gearui.i18n.I18n
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.semantics.role
import com.tencent.kuikly.compose.ui.semantics.semantics
import com.tencent.kuikly.compose.ui.semantics.stateDescription
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import androidx.compose.runtime.remember
import com.gearui.foundation.motion.rowPressFeedback
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.typography.IconSizes

data class Tab(
    val id: String,
    val label: String,
    val icon: String? = null,
    val disabled: Boolean = false
)

enum class TabsSize {
    SMALL,
    MEDIUM,
    LARGE
}

enum class TabsOutlineType {
    UNDERLINE,
    CAPSULE,
    CARD
}

@Composable
fun Tabs(
    items: List<Tab>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    isScrollable: Boolean = false,
    showIndicator: Boolean = true,
    showDivider: Boolean = true,
    size: TabsSize = TabsSize.MEDIUM,
    // The reference's Tabs default to the `primary` variant — a pill on a track.
    // `secondary`, the underlined one, is the alternative, not the default; ours had
    // them the other way round, which is why a plain Tabs read as a Material tab bar.
    outlineType: TabsOutlineType = TabsOutlineType.CAPSULE
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    val selected = selectedId ?: items.firstOrNull()?.id.orEmpty()
    val tabHeight = when (size) {
        TabsSize.SMALL -> 36.dp
        TabsSize.MEDIUM -> 44.dp
        TabsSize.LARGE -> 52.dp
    }
    Column(modifier = modifier.fillMaxWidth()) {
        when {
            isScrollable -> {
                GearLazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items.forEach { item ->
                        item {
                            TabCell(
                                item = item,
                                selected = selected == item.id,
                                onSelect = onSelect,
                                tabHeight = tabHeight,
                                size = size,
                                outlineType = outlineType,
                                showIndicator = showIndicator
                            )
                        }
                    }
                }
            }

            // The reference's primary variant is the segmented track: one rounded
            // indicator of `--color-segment` sliding over a `--color-default` track,
            // which is what SegmentedControl already draws. Tabs used to paint each
            // cell separately and fill the selected one with the brand colour — a
            // different control wearing the same name.
            outlineType == TabsOutlineType.CAPSULE -> {
                val selectedIndex = items.indexOfFirst { it.id == selected }
                SegmentedTrack(
                    count = items.size,
                    selectedIndex = selectedIndex,
                    enabled = true,
                    modifier = Modifier.fillMaxWidth(),
                    onSelect = { index -> items.getOrNull(index)?.let { if (it.id != selected) onSelect(it.id) } },
                ) { index, isSelected ->
                    TabsSegmentLabel(item = items[index], selected = isSelected, size = size)
                }
            }

            else -> {
                // Reference `tabs.css` secondary variant: triggers 4 apart and one 2px
                // accent indicator, as wide as the selected trigger, that springs to it
                // (stiffness 1200, damping 120) instead of a stub under each label.
                val motion = Theme.motion
                val density = LocalDensity.current
                var rowWidth by remember { mutableStateOf(0) }
                val gapPx = with(density) { ControlGeometry.tabsListGap.toPx() }
                val selectedIndex = items.indexOfFirst { it.id == selected }
                val (targetX, cellPx) = segmentOffset(rowWidth.toFloat(), items.size, gapPx, selectedIndex)
                val indicatorX = remember { Animatable(targetX) }
                var placed by remember { mutableStateOf(false) }
                LaunchedEffect(targetX, rowWidth) {
                    if (rowWidth == 0) return@LaunchedEffect
                    val spec = tabsIndicatorSpring(motion)
                    if (!placed || spec == null) {
                        indicatorX.snapTo(targetX)
                        placed = true
                    } else {
                        indicatorX.animateTo(targetX, spec)
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().onSizeChanged { rowWidth = it.width }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(ControlGeometry.tabsListGap)
                    ) {
                        items.forEach { item ->
                            Box(modifier = Modifier.weight(1f)) {
                                TabCell(
                                    item = item,
                                    selected = selected == item.id,
                                    onSelect = onSelect,
                                    tabHeight = tabHeight,
                                    size = size,
                                    outlineType = outlineType,
                                    // The shared sliding indicator below replaces the per-cell one.
                                    showIndicator = false
                                )
                            }
                        }
                    }
                    if (outlineType == TabsOutlineType.UNDERLINE && showIndicator &&
                        selectedIndex >= 0 && rowWidth > 0
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .offset { IntOffset(indicatorX.value.roundToInt(), 0) }
                                .width(with(density) { cellPx.toDp() })
                                .height(ControlGeometry.tabsIndicatorHeight)
                                .background(colors.primary)
                        )
                    }
                }
            }
        }

        if (showDivider && outlineType == TabsOutlineType.UNDERLINE) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BorderWidth.thin)
                    .background(colors.border)
            )
        }
    }
}

/**
 * The label inside a segmented tab. The track owns the surface, the indicator and the
 * press feedback, so this is only the content — which is the whole reason Tabs can
 * share it with SegmentedControl instead of drawing a second one.
 */
@Composable
private fun TabsSegmentLabel(item: Tab, selected: Boolean, size: TabsSize) {
    val colors = Theme.colors
    val textStyle = when (size) {
        TabsSize.SMALL -> Theme.typography.bodySmall
        TabsSize.MEDIUM -> Theme.typography.bodyMedium
        TabsSize.LARGE -> Theme.typography.bodyLarge
    }.copy(fontWeight = FontWeight.Medium)
    val contentColor = when {
        item.disabled -> colors.mutedForeground
        selected -> colors.foreground
        else -> colors.mutedForeground
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (item.icon != null) {
            Icon(name = item.icon, size = IconSizes.Default.sm, tint = contentColor)
        }
        Text(text = item.label, style = textStyle, color = contentColor, maxLines = 1)
    }
}

@Composable
private fun TabCell(
    item: Tab,
    selected: Boolean,
    onSelect: (String) -> Unit,
    tabHeight: com.tencent.kuikly.compose.ui.unit.Dp,
    size: TabsSize,
    outlineType: TabsOutlineType,
    showIndicator: Boolean
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    // Reference `.tabs__label`: medium weight.
    val textStyle = when (size) {
        TabsSize.SMALL -> Theme.typography.bodySmall
        TabsSize.MEDIUM -> Theme.typography.bodyMedium
        TabsSize.LARGE -> Theme.typography.bodyLarge
    }.copy(fontWeight = FontWeight.Medium)

    val containerModifier = when (outlineType) {
        TabsOutlineType.UNDERLINE -> Modifier
        TabsOutlineType.CAPSULE -> Modifier
            .clip(shapes.md)
            .background(if (selected) colors.primary else colors.muted)
        TabsOutlineType.CARD -> Modifier
            .clip(shapes.md)
            .background(if (selected) colors.surface else colors.muted)
            .border(BorderWidth.thin, if (selected) colors.border else Color.Transparent, shapes.md)
    }

    // The label is centred VERTICALLY in the cell with the underline overlaid on the bottom edge
    // (what Material TabRow and UIKit do). The old Column + SpaceBetween pushed the text to the top
    // edge and gave the indicator its own row — titles sat off-centre and looked squeezed by the bar.
    val state = if (selected) I18n.strings.common.selected else I18n.strings.common.unselected
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = containerModifier
            .fillMaxWidth()
            .height(tabHeight)
            .semantics {
                role = Role.Tab
                stateDescription = state
            }
            .rowPressFeedback(interaction = interaction, shape = shapes.md, enabled = !item.disabled)
            .clickable(enabled = !item.disabled, interactionSource = interaction, indication = null) {
                if (!selected) onSelect(item.id)
            },
        contentAlignment = Alignment.Center
    ) {
        val contentColor = when {
            item.disabled -> colors.mutedForeground
            outlineType == TabsOutlineType.CAPSULE && selected -> colors.primaryForeground
            selected -> colors.foreground
            else -> colors.mutedForeground
        }

        Row(
            modifier = Modifier.padding(horizontal = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.icon != null) {
                Icon(
                    name = item.icon,
                    size = IconSizes.Default.md,
                    tint = contentColor
                )
            }
            Text(
                text = item.label,
                style = textStyle,
                color = contentColor,
                maxLines = 1
            )
        }

        if (outlineType == TabsOutlineType.UNDERLINE && selected && showIndicator) {
            // matchParentSize takes the cell's size without taking part in measuring it,
            // so the indicator spans the cell even inside a LazyRow (unbounded width)
            // and cannot feed its own width back into the cell.
            Box(modifier = Modifier.matchParentSize(), contentAlignment = Alignment.BottomCenter) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ControlGeometry.tabsIndicatorHeight)
                        .background(colors.primary)
                )
            }
        }
    }
}
