package com.gearui.components.tabs

import androidx.compose.runtime.Composable
import com.gearui.Spacing
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.GearLazyRow
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
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
    outlineType: TabsOutlineType = TabsOutlineType.UNDERLINE
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
                    horizontalArrangement = Arrangement.spacedBy(Spacing.spacer12.dp)
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

            else -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp)
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
                                showIndicator = showIndicator
                            )
                        }
                    }
                }
            }
        }

        if (showDivider && outlineType == TabsOutlineType.UNDERLINE) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.border)
            )
        }
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
    val textStyle = when (size) {
        TabsSize.SMALL -> Typography.BodySmall
        TabsSize.MEDIUM -> Typography.BodyMedium
        TabsSize.LARGE -> Typography.TitleSmall
    }

    val containerModifier = when (outlineType) {
        TabsOutlineType.UNDERLINE -> Modifier
        TabsOutlineType.CAPSULE -> Modifier
            .clip(shapes.default)
            .background(if (selected) colors.primary else colors.surfaceVariant)
        TabsOutlineType.CARD -> Modifier
            .clip(shapes.default)
            .background(if (selected) colors.surface else colors.surfaceVariant)
            .border(1.dp, if (selected) colors.border else Color.Transparent, shapes.default)
    }

    Column(
        modifier = containerModifier
            .fillMaxWidth()
            .height(tabHeight)
            .clickable(enabled = !item.disabled) {
                if (!selected) onSelect(item.id)
            }
            .padding(horizontal = Spacing.spacer8.dp, vertical = Spacing.spacer8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val contentColor = when {
            item.disabled -> colors.textDisabled
            outlineType == TabsOutlineType.CAPSULE && selected -> colors.onPrimary
            selected -> colors.textPrimary
            else -> colors.textSecondary
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.spacer4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.icon != null) {
                Icon(
                    name = item.icon,
                    size = 16.dp,
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

        if (outlineType == TabsOutlineType.UNDERLINE) {
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .width(20.dp)
                    .background(if (selected && showIndicator) colors.primary else Color.Transparent)
            )
        }
    }
}
