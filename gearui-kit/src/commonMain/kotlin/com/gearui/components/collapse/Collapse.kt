package com.gearui.components.collapse

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.animation.core.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.rotate
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.primitives.DividerFull
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.typography.IconSizes

/**
 * Collapse panel style
 */
enum class CollapseStyle {
    Block,
    Card
}

/**
 * CollapsePanel - one collapse panel
 */
data class CollapsePanel(
    val value: Any? = null,
    val headerBuilder: @Composable (isExpanded: Boolean) -> Unit,
    val expandIconTextBuilder: ((isExpanded: Boolean) -> String)? = null,
    val body: @Composable () -> Unit,
    val isExpanded: Boolean = false
)

/**
 * Collapse - collapse panel component
 */
@Composable
fun Collapse(
    children: List<CollapsePanel>,
    style: CollapseStyle = CollapseStyle.Block,
    expansionCallback: ((Int, Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    val containerModifier = if (style == CollapseStyle.Card) {
        modifier
            .padding(horizontal = Spacing.lg)
            .clip(shapes.lg)
    } else {
        modifier
    }

    Column(
        modifier = containerModifier
            .fillMaxWidth()
            .background(colors.surface)
    ) {
        children.forEachIndexed { index, panel ->
            val isLast = index == children.lastIndex

            CollapsePanelItem(
                panel = panel,
                onToggle = {
                    expansionCallback?.invoke(index, panel.isExpanded)
                }
            )

            if (!isLast && !panel.isExpanded) {
                DividerFull()
            }
        }
    }
}

/**
 * Collapse.Accordion - accordion mode
 */
object Collapse {
    @Composable
    fun Accordion(
        children: List<CollapsePanel>,
        style: CollapseStyle = CollapseStyle.Block,
        expansionCallback: ((Int, Boolean) -> Unit)? = null,
        initialOpenPanelValue: Any? = null,
        modifier: Modifier = Modifier
    ) {
        val colors = Theme.colors
        val shapes = Theme.shapes

        var currentOpenValue by remember { mutableStateOf(initialOpenPanelValue) }

        val containerModifier = if (style == CollapseStyle.Card) {
            modifier
                .padding(horizontal = Spacing.lg)
                .clip(shapes.lg)
        } else {
            modifier
        }

        Column(
            modifier = containerModifier
                .fillMaxWidth()
                .background(colors.surface)
        ) {
            children.forEachIndexed { index, panel ->
                val isExpanded = currentOpenValue == panel.value
                val isLast = index == children.lastIndex

                CollapsePanelItem(
                    panel = panel.copy(isExpanded = isExpanded),
                    onToggle = {
                        val wasExpanded = isExpanded
                        currentOpenValue = if (wasExpanded) null else panel.value
                        expansionCallback?.invoke(index, wasExpanded)
                    }
                )

                if (!isLast && !isExpanded) {
                    DividerFull()
                }
            }
        }
    }
}

/**
 * A single collapse panel - plain show/hide with no animation
 */
@Composable
private fun CollapsePanelItem(
    panel: CollapsePanel,
    onToggle: () -> Unit
) {
    val colors = Theme.colors

    // Arrow rotation animation
    val rotation by animateFloatAsState(
        targetValue = if (panel.isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 200)
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Title bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                panel.headerBuilder(panel.isExpanded)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                panel.expandIconTextBuilder?.let { builder ->
                    Text(
                        text = builder(panel.isExpanded),
                        style = Typography.BodySmall,
                        color = colors.mutedForeground
                    )
                }

                Icon(
                    name = Icons.keyboard_arrow_down,
                    size = IconSizes.Default.md,
                    tint = colors.mutedForeground,
                    modifier = Modifier.rotate(rotation)
                )
            }
        }

        // Content area - shown when expanded
        if (panel.isExpanded) {
            DividerFull()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg)
            ) {
                panel.body()
            }
        }
    }
}

// ============ Legacy API compatibility ============

@Composable
fun CollapseItem(
    title: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 200)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapes.sm)
            .background(colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = enabled,
                    onClick = { onExpandChange(!expanded) }
                )
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = Typography.TitleMedium,
                color = if (enabled) colors.foreground else colors.mutedForeground
            )

            Icon(
                name = Icons.keyboard_arrow_down,
                size = IconSizes.Default.md,
                tint = if (enabled) colors.mutedForeground else colors.mutedForeground,
                modifier = Modifier.rotate(rotation)
            )
        }

        if (expanded) {
            DividerFull()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg)
            ) {
                content()
            }
        }
    }
}

@Composable
fun CollapseGroup(
    items: List<CollapseItemData>,
    modifier: Modifier = Modifier,
    accordion: Boolean = true
) {
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        items.forEachIndexed { index, item ->
            CollapseItem(
                title = item.title,
                expanded = if (accordion) expandedIndex == index else item.expanded,
                onExpandChange = { expanded ->
                    if (accordion) {
                        expandedIndex = if (expanded) index else null
                    } else {
                        item.onExpandChange(expanded)
                    }
                },
                content = item.content
            )
        }
    }
}

data class CollapseItemData(
    val title: String,
    val expanded: Boolean = false,
    val enabled: Boolean = true,
    val onExpandChange: (Boolean) -> Unit = {},
    val content: @Composable () -> Unit
)
