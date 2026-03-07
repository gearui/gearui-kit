package com.gearui.components.navigationmenu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.Spacing
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.overlay.GearOverlayOptions
import com.gearui.overlay.GearOverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.rememberGearOverlay
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.widthIn
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.unit.dp

data class NavigationMenuItem(
    val id: String,
    val title: String,
    val description: String = "",
    val disabled: Boolean = false
)

data class NavigationMenuSection(
    val id: String,
    val label: String,
    val items: List<NavigationMenuItem>,
    val disabled: Boolean = false
)

@Composable
fun NavigationMenu(
    sections: List<NavigationMenuSection>,
    selectedSectionId: String?,
    selectedItemId: String? = null,
    modifier: Modifier = Modifier,
    onSectionSelect: (String) -> Unit,
    onItemSelect: (String) -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    val overlay = rememberGearOverlay()
    val triggerBounds = remember { mutableStateMapOf<String, Rect>() }
    var expandedSectionId by remember { mutableStateOf<String?>(null) }

    val expandedSection = sections.firstOrNull { it.id == expandedSectionId }
    val anchorBounds = expandedSectionId?.let { triggerBounds[it] }

    if (expandedSection != null && anchorBounds != null) {
        DisposableEffect(expandedSection.id, anchorBounds) {
            val overlayId = overlay.show(
                anchorBounds = anchorBounds,
                options = GearOverlayOptions(
                    placement = GearOverlayPlacement.BottomLeft,
                    offsetY = Spacing.spacer8.dp,
                    modal = false,
                    maskColor = null,
                    dismissPolicy = OverlayDismissPolicy.Dropdown.copy(
                        outsideClick = true,
                        scroll = true
                    )
                ),
                onDismiss = {
                    expandedSectionId = null
                }
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(min = 280.dp, max = 380.dp)
                        .clip(shapes.large)
                        .shadow(Spacing.spacer4.dp, shapes.large)
                        .background(colors.surface)
                        .border(1.dp, colors.border, shapes.large)
                        .padding(Spacing.spacer8.dp),
                    verticalArrangement = Arrangement.spacedBy(Spacing.spacer4.dp)
                ) {
                    expandedSection.items.forEach { item ->
                        val selected = item.id == selectedItemId
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shapes.default)
                                .background(if (selected) colors.surfaceVariant else colors.surface)
                                .clickable(enabled = !item.disabled) {
                                    onSectionSelect(expandedSection.id)
                                    onItemSelect(item.id)
                                    expandedSectionId = null
                                }
                                .padding(horizontal = Spacing.spacer12.dp, vertical = Spacing.spacer8.dp),
                            verticalArrangement = Arrangement.spacedBy(Spacing.spacer4.dp)
                        ) {
                            Text(
                                text = item.title,
                                style = Typography.TitleSmall,
                                color = if (item.disabled) colors.textDisabled else colors.textPrimary
                            )
                            if (item.description.isNotEmpty()) {
                                Text(
                                    text = item.description,
                                    style = Typography.BodyMedium,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            onDispose {
                overlay.dismiss(overlayId)
            }
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.spacer12.dp)
    ) {
        sections.forEach { section ->
            val selected = section.id == selectedSectionId
            val expanded = section.id == expandedSectionId
            val hasPopup = section.items.isNotEmpty()

            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        triggerBounds[section.id] = coordinates.boundsInRoot()
                    }
                    .clip(shapes.default)
                    .background(
                        if (expanded) colors.surfaceVariant
                        else if (selected) colors.surfaceVariant
                        else colors.surface
                    )
                    .clickable(enabled = !section.disabled) {
                        onSectionSelect(section.id)
                        expandedSectionId = when {
                            !hasPopup -> null
                            expanded -> null
                            else -> section.id
                        }
                    }
                    .padding(horizontal = Spacing.spacer12.dp, vertical = Spacing.spacer8.dp)
            ) {
                val textColor = if (section.disabled) colors.textDisabled else colors.textPrimary
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.spacer4.dp)
                ) {
                    Text(
                        text = section.label,
                        style = Typography.TitleSmall,
                        color = textColor
                    )
                    if (hasPopup) {
                        Icon(
                            name = if (expanded) Icons.keyboard_arrow_up else Icons.keyboard_arrow_down,
                            size = 16.dp,
                            tint = textColor
                        )
                    }
                }
            }
        }
    }
}
