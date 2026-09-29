package com.gearui.components.collapse

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import androidx.compose.runtime.remember
import com.tencent.kuikly.compose.ui.graphics.RectangleShape
import com.gearui.foundation.motion.rowPressFeedback
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.rotate
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.primitives.Divider
import com.gearui.theme.Theme
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.typography.IconSizes

/**
 * Collapse panel style — HeroUI Native Accordion's two variants.
 */
enum class CollapseStyle {
    /** No surface of its own, rows split by full-width dividers: for use on a card or sheet. */
    Block,

    /** Its own rounded surface with inset dividers: for use on the page background. */
    Card
}

@Composable
private fun CollapseSeparator(style: CollapseStyle) {
    val inset = if (style == CollapseStyle.Card) ControlGeometry.accordionPadding else Spacing.none
    Divider(insetStart = inset, insetEnd = inset)
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
            .clip(shapes.xl)
    } else {
        modifier
    }

    Column(
        modifier = containerModifier
            .fillMaxWidth()
            .then(if (style == CollapseStyle.Card) Modifier.background(colors.surface) else Modifier)
    ) {
        children.forEachIndexed { index, panel ->
            val isLast = index == children.lastIndex

            CollapsePanelItem(
                panel = panel,
                surface = style == CollapseStyle.Card,
                onToggle = {
                    expansionCallback?.invoke(index, panel.isExpanded)
                }
            )

            if (!isLast) {
                CollapseSeparator(style)
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
                .clip(shapes.xl)
        } else {
            modifier
        }

        Column(
            modifier = containerModifier
                .fillMaxWidth()
                .then(if (style == CollapseStyle.Card) Modifier.background(colors.surface) else Modifier)
        ) {
            children.forEachIndexed { index, panel ->
                val isExpanded = currentOpenValue == panel.value
                val isLast = index == children.lastIndex

                CollapsePanelItem(
                    panel = panel.copy(isExpanded = isExpanded),
                    surface = style == CollapseStyle.Card,
                    onToggle = {
                        val wasExpanded = isExpanded
                        currentOpenValue = if (wasExpanded) null else panel.value
                        expansionCallback?.invoke(index, wasExpanded)
                    }
                )

                if (!isLast) {
                    CollapseSeparator(style)
                }
            }
        }
    }
}

/**
 * A single collapse panel with shared content and indicator feedback.
 */
@Composable
private fun CollapsePanelItem(
    panel: CollapsePanel,
    surface: Boolean,
    onToggle: () -> Unit
) {
    val colors = Theme.colors

    val rotation = collapseRotation(panel.isExpanded)
    val interaction = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Title bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .rowPressFeedback(interaction = interaction, shape = RectangleShape, scale = false)
                .clickable(interactionSource = interaction, indication = null, onClick = onToggle)
                .padding(horizontal = if (surface) ControlGeometry.accordionSurfacePadding else ControlGeometry.accordionPadding, vertical = ControlGeometry.accordionVerticalPadding),
            horizontalArrangement = Arrangement.spacedBy(ControlGeometry.accordionTriggerGap),
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
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground
                    )
                }

                Icon(
                    name = Icons.caret_down,
                    size = IconSizes.Default.md,
                    tint = colors.foreground,
                    modifier = Modifier.rotate(rotation)
                )
            }
        }

        CollapseContent(panel.isExpanded, surface, panel.body)
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

    val rotation = collapseRotation(expanded)
    val headerInteraction = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = if (enabled) 1f else FeedbackDefaults.disabledOpacity }
            .clip(shapes.xl)
            .background(colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .rowPressFeedback(interaction = headerInteraction, shape = RectangleShape, enabled = enabled, scale = false)
                .clickable(
                    enabled = enabled,
                    interactionSource = headerInteraction,
                    indication = null,
                    onClick = { onExpandChange(!expanded) }
                )
                .padding(horizontal = ControlGeometry.accordionSurfacePadding, vertical = ControlGeometry.accordionVerticalPadding),
            horizontalArrangement = Arrangement.spacedBy(ControlGeometry.accordionTriggerGap),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = Theme.typography.titleMedium,
                color = colors.foreground
            )

            Icon(
                name = Icons.caret_down,
                size = IconSizes.Default.md,
                tint = colors.foreground,
                modifier = Modifier.rotate(rotation)
            )
        }

        CollapseContent(expanded, surface = true, content = content)
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
                enabled = item.enabled,
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
