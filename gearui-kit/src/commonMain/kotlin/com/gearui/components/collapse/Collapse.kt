package com.gearui.components.collapse

import com.gearui.foundation.disclosure.DisclosureBody
import com.gearui.foundation.disclosure.DisclosureHeader

import com.gearui.components.icon.*
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
 * A single collapse panel: the shared disclosure header and body.
 */
@Composable
private fun CollapsePanelItem(
    panel: CollapsePanel,
    surface: Boolean,
    onToggle: () -> Unit
) {
    val colors = Theme.colors
    val inline = if (surface) ControlGeometry.accordionSurfacePadding else ControlGeometry.accordionPadding
    Column(modifier = Modifier.fillMaxWidth()) {
        DisclosureHeader(
            expanded = panel.isExpanded,
            onToggle = onToggle,
            padding = PaddingValues(horizontal = inline, vertical = ControlGeometry.accordionVerticalPadding),
            gap = ControlGeometry.accordionTriggerGap,
            chevronTint = colors.foreground,
            trailing = panel.expandIconTextBuilder?.let { builder ->
                {
                    Text(
                        text = builder(panel.isExpanded),
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground
                    )
                }
            },
        ) {
            Box(modifier = Modifier.weight(1f)) {
                panel.headerBuilder(panel.isExpanded)
            }
        }
        DisclosureBody(
            expanded = panel.isExpanded,
            padding = PaddingValues(start = inline, end = inline, bottom = ControlGeometry.accordionVerticalPadding),
            content = panel.body,
        )
    }
}
