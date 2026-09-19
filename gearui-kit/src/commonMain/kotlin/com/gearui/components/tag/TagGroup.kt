package com.gearui.components.tag

import androidx.compose.runtime.Composable
import com.gearui.components.icon.Icons
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.interaction.PressableFeedback
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.theme.LocalInputColors
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.ExperimentalLayoutApi
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.FlowRow
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.Dp

/** How a [TagGroup] selects: not at all, one tag, or any number. */
enum class TagGroupSelectionMode { NONE, SINGLE, MULTIPLE }

/** Tag size inside a [TagGroup]; text is xs / sm / base. */
enum class TagGroupSize { SMALL, MEDIUM, LARGE }

/** Unselected tag fill: the neutral fill, or the surface colour for use on a plain background. */
enum class TagGroupVariant { DEFAULT, SURFACE }

/** One tag in a [TagGroup]. [key] identifies it in the selection. */
data class TagGroupItem(
    val key: String,
    val label: String,
    val icon: String? = null,
    val enabled: Boolean = true,
)

/**
 * TagGroup — HeroUI Native `TagGroup`: a wrapping set of tags that can be selected
 * and removed, for filters, topics and interests.
 *
 * Tags wrap with an 8dp gap. A selected tag takes the accent-soft fill and the
 * accent-soft foreground; the rest use the neutral (or surface) fill with the field
 * foreground in medium weight. Tags press with [PressableFeedback].
 *
 * - SINGLE: tapping a tag selects it alone; tapping the selected tag clears it.
 * - MULTIPLE: tapping toggles the tag.
 * - NONE: tags are display only (or removable).
 * - [onRemove] adds a remove glyph to each tag and reports the removed key.
 *
 * Use [Tag] for a single static label; TagGroup is the interactive collection.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagGroup(
    items: List<TagGroupItem>,
    selectedKeys: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    selectionMode: TagGroupSelectionMode = TagGroupSelectionMode.SINGLE,
    size: TagGroupSize = TagGroupSize.MEDIUM,
    variant: TagGroupVariant = TagGroupVariant.DEFAULT,
    enabled: Boolean = true,
    onRemove: ((String) -> Unit)? = null,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ControlGeometry.tagGroupGap),
        verticalArrangement = Arrangement.spacedBy(ControlGeometry.tagGroupGap),
    ) {
        items.forEach { item ->
            val itemEnabled = enabled && item.enabled
            val selected = item.key in selectedKeys
            GroupTag(
                item = item,
                selected = selected,
                enabled = itemEnabled,
                size = size,
                variant = variant,
                onClick = if (selectionMode == TagGroupSelectionMode.NONE) null else {
                    { onSelectionChange(nextTagSelection(selectedKeys, item.key, selectionMode)) }
                },
                onRemove = onRemove?.let { remove -> { remove(item.key) } },
            )
        }
    }
}

@Composable
private fun GroupTag(
    item: TagGroupItem,
    selected: Boolean,
    enabled: Boolean,
    size: TagGroupSize,
    variant: TagGroupVariant,
    onClick: (() -> Unit)?,
    onRemove: (() -> Unit)?,
) {
    val colors = Theme.colors
    val metrics = tagGroupMetrics(size)
    val shape = RoundedCornerShape(metrics.radius)
    val fill = when {
        selected -> colors.primarySoft
        variant == TagGroupVariant.SURFACE -> colors.surface
        else -> colors.muted
    }
    val foreground = if (selected) colors.primarySoftForeground else LocalInputColors.current.foreground
    val style = when (size) {
        TagGroupSize.SMALL -> Theme.typography.bodyExtraSmall
        TagGroupSize.MEDIUM -> Theme.typography.bodySmall
        TagGroupSize.LARGE -> Theme.typography.bodyMedium
    }.copy(fontWeight = FontWeight.Medium)
    val iconSize = style.fontSize.value.let { Dp(it) }

    val body: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .clip(shape)
                .background(fill)
                .padding(horizontal = metrics.paddingInline, vertical = metrics.paddingBlock),
            horizontalArrangement = Arrangement.spacedBy(metrics.gap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (item.icon != null) Icon(name = item.icon, size = iconSize, tint = foreground)
            Text(text = item.label, style = style, color = foreground, maxLines = 1)
            if (onRemove != null) {
                Icon(
                    name = Icons.x,
                    size = ControlGeometry.tagGroupRemoveIcon,
                    tint = if (selected) foreground else colors.mutedForeground,
                    modifier = Modifier
                        .clip(Theme.shapes.sm)
                        .clickable(enabled = enabled, onClick = onRemove),
                )
            }
        }
    }

    if (onClick != null) {
        PressableFeedback(onClick = onClick, enabled = enabled, shape = shape) { body() }
    } else {
        com.tencent.kuikly.compose.foundation.layout.Box(Modifier.alpha(if (enabled) 1f else FeedbackDefaults.disabledOpacity)) { body() }
    }
}

private class TagGroupMetrics(val paddingInline: Dp, val paddingBlock: Dp, val radius: Dp, val gap: Dp)

private fun tagGroupMetrics(size: TagGroupSize) = when (size) {
    TagGroupSize.SMALL -> TagGroupMetrics(ControlGeometry.tagGroupSmallPaddingInline, ControlGeometry.tagGroupSmallPaddingBlock, ControlGeometry.tagGroupSmallRadius, ControlGeometry.tagGroupSmallPaddingInline / 2)
    TagGroupSize.MEDIUM -> TagGroupMetrics(ControlGeometry.tagGroupMediumPaddingInline, ControlGeometry.tagGroupMediumPaddingBlock, ControlGeometry.tagGroupMediumRadius, ControlGeometry.tagGroupSmallPaddingInline / 2)
    TagGroupSize.LARGE -> TagGroupMetrics(ControlGeometry.tagGroupLargePaddingInline, ControlGeometry.tagGroupLargePaddingBlock, ControlGeometry.tagGroupLargeRadius, ControlGeometry.tagGroupLargePaddingBlock)
}

/** Selection after tapping [key]. SINGLE clears when the selected tag is tapped again. */
internal fun nextTagSelection(current: Set<String>, key: String, mode: TagGroupSelectionMode): Set<String> = when (mode) {
    TagGroupSelectionMode.NONE -> current
    TagGroupSelectionMode.SINGLE -> if (key in current) emptySet() else setOf(key)
    TagGroupSelectionMode.MULTIPLE -> if (key in current) current - key else current + key
}
