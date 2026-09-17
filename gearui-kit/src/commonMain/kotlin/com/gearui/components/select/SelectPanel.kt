package com.gearui.components.select

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.i18n.I18n
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.hoverable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.interaction.collectIsHoveredAsState
import com.tencent.kuikly.compose.foundation.interaction.collectIsPressedAsState
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.foundation.selection.selectable
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.graphics.lerp

/** Continuous viewport shared by single/multi selection; no per-row cards. */
@Composable
internal fun <T> SelectPanel(
    options: List<SelectOption<T>>,
    isSelected: (SelectOption<T>) -> Boolean,
    anchorWidth: Float,
    layout: SelectPanelLayout,
    viewportWidth: Int,
    enabled: Boolean,
    multiple: Boolean = false,
    onOptionClick: (SelectOption<T>) -> Unit,
) {
    val colors = Theme.colors
    val shape = Theme.shapes.xl
    val density = LocalDensity.current
    val rows = remember(options) { selectRows(options) }
    val state = rememberLazyListState(initialFirstVisibleItemIndex = layout.firstRow)
    val width = with(density) { anchorWidth.toDp().coerceAtMost(viewportWidth.toDp() - Spacing.lg) }
    val rowHeight = FieldSizeTokens.Medium.height

    Box(
        Modifier.width(width).height(layout.height.dp)
            .shadow(Theme.elevation.floating, shape)
            .clip(shape)
            .background(colors.popover)
            .padding(ControlGeometry.selectContentPadding)
    ) {
        if (rows.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(horizontal = ControlGeometry.selectItemPadding), contentAlignment = Alignment.CenterStart) {
                Text(I18n.strings.common.noData, style = Theme.typography.bodyMedium, color = colors.mutedForeground)
            }
        } else {
            LazyColumn(state = state, modifier = Modifier.fillMaxSize()) {
                itemsIndexed(rows) { _, row ->
                    val option = row.option
                    if (option == null) {
                        Box(Modifier.fillMaxWidth().height(rowHeight).padding(horizontal = ControlGeometry.selectItemPadding), contentAlignment = Alignment.CenterStart) {
                            Text(row.heading.orEmpty(), style = Theme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = colors.mutedForeground,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    } else {
                        SelectPanelRow(option, isSelected(option), enabled && !option.disabled, multiple) { onOptionClick(option) }
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> SelectPanelRow(option: SelectOption<T>, selected: Boolean, enabled: Boolean, multiple: Boolean, onClick: () -> Unit) {
    val colors = Theme.colors
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val pressed by interaction.collectIsPressedAsState()
    Row(
        Modifier.fillMaxWidth().height(FieldSizeTokens.Medium.height)
            .background(if (enabled && (hovered || pressed)) colors.muted else colors.popover)
            .hoverable(interaction, enabled)
            .selectable(selected = selected, enabled = enabled, role = if (multiple) Role.Checkbox else Role.RadioButton,
                interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = ControlGeometry.selectItemPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(option.label, modifier = Modifier.weight(1f), style = Theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = if (enabled) colors.popoverForeground else colors.mutedForeground,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        // Reserve the trailing slot: selecting never changes label width.
        Box(Modifier.size(ControlGeometry.selectIndicatorSlot), contentAlignment = Alignment.Center) {
            if (selected) Icon(Icons.check, size = IconSizes.Default.md,
                tint = if (enabled) lerp(colors.primary, colors.foreground, FeedbackDefaults.selectForegroundMix) else colors.mutedForeground)
        }
    }
}
