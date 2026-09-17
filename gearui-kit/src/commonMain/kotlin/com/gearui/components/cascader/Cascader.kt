package com.gearui.components.cascader

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.items
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.control.ControlGeometry
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.rememberOverlay
import com.gearui.theme.Theme
import com.gearui.theme.LocalInputColors
import com.gearui.i18n.I18n
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.overlay.OverlayDefaults
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.field.fieldTriggerModifier
import com.gearui.foundation.field.FieldErrorText

/**
 * Cascader option data
 */
data class CascaderOption(
    val value: String,
    val label: String,
    val children: List<CascaderOption> = emptyList(),
    val disabled: Boolean = false
)

/**
 * Cascader - cascading select
 *
 * Built on the Overlay system
 *
 * Features:
 * - multi-level selection
 * - dynamic loading
 * - a real floating layer, leaving the layout untouched
 */
@Composable
fun Cascader(
    options: List<CascaderOption>,
    selectedPath: List<String>,
    onSelect: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = I18n.strings.field.selectPlaceholder,
    enabled: Boolean = true,
    error: String? = null,
    separator: String = " / ",
    dropdownHeight: Dp = 300.dp
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    val overlay = rememberOverlay()
    val density = LocalDensity.current
    val optionsState = rememberUpdatedState(options)
    val heightState = rememberUpdatedState(dropdownHeight)

    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var overlayId by remember { mutableStateOf<Long?>(null) }

    // Wrapped in State
    val selectedPathState = rememberUpdatedState(selectedPath)
    val onSelectState = rememberUpdatedState(onSelect)

    val displayText = remember(selectedPath, options, placeholder, separator) {
        if (selectedPath.isEmpty()) {
            placeholder
        } else {
            getDisplayText(options, selectedPath, separator)
        }
    }

    fun clearDropdownState() {
        overlayId = null
        expanded = false
    }

    fun closeDropdown() {
        overlayId?.let { overlay.dismiss(it) }
    }

    fun openDropdown() {
        if (anchorBounds == null) return

        val bounds = anchorBounds!!
        val anchorWidth = bounds.width

        overlayId = overlay.show(
            anchorBounds = bounds,
            options = OverlayOptions(
                placement = OverlayPlacement.BottomLeft,
                offsetY = ControlGeometry.selectPanelOffset,
                autoFlip = true,
                dismissPolicy = OverlayDismissPolicy.Dropdown
            ),
            onDismiss = {
                clearDropdownState()
            }
        ) {
            val widthDp = with(density) { anchorWidth.toDp() }

            CascaderDropdown(
                options = optionsState.value,
                selectedPath = selectedPathState.value,
                onSelect = { path ->
                    onSelectState.value(path)
                    // Only close if reached leaf node
                    val option = findOptionByPath(optionsState.value, path)
                    if (option?.children?.isEmpty() == true) {
                        closeDropdown()
                    }
                },
                height = heightState.value,
                width = widthDp
            )
        }
        expanded = true
    }

    LaunchedEffect(enabled) { if (!enabled) closeDropdown() }

    DisposableEffect(Unit) {
        onDispose {
            overlayId?.let { overlay.dismiss(it) }
        }
    }

    Column(modifier = modifier) {
        // Trigger
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(FieldSizeTokens.Medium.height)
                .onGloballyPositioned { coordinates ->
                    anchorBounds = coordinates.boundsInRoot()
                }
                .then(fieldTriggerModifier(enabled, error) {
                    if (expanded) closeDropdown() else openDropdown()
                })
                .padding(horizontal = FieldSizeTokens.Medium.paddingHorizontal),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = displayText,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = Theme.typography.bodyMedium,
                color = when {
                    selectedPath.isNotEmpty() -> LocalInputColors.current.foreground
                    else -> LocalInputColors.current.placeholder
                }
            )

            Icon(
                name = if (expanded) Icons.caret_up else Icons.caret_down,
                size = FieldDefaults.trailingIconSize,
                tint = colors.mutedForeground
            )
        }

        FieldErrorText(error)
    }
}

@Composable
private fun CascaderDropdown(
    options: List<CascaderOption>,
    selectedPath: List<String>,
    onSelect: (List<String>) -> Unit,
    height: Dp,
    width: Dp
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    // Build cascading levels
    val levels = remember(options, selectedPath) {
        buildCascaderLevels(options, selectedPath)
    }

    Row(
        modifier = Modifier
            .width(width)
            .height(height)
            .shadow(Theme.elevation.floating, OverlayDefaults.panelShape)
            .clip(OverlayDefaults.panelShape)
            .background(colors.popover, OverlayDefaults.panelShape)
            .border(BorderWidth.thin, colors.border, OverlayDefaults.panelShape)
    ) {
        levels.forEachIndexed { levelIndex, levelOptions ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(
                        if (levelIndex > 0) {
                            Modifier.border(width = BorderWidth.thin, color = colors.border)
                        } else {
                            Modifier
                        }
                    )
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(ControlGeometry.selectItemPadding)
                ) {
                    items(levelOptions) { option ->
                        val isSelected = selectedPath.getOrNull(levelIndex) == option.value
                        val isLeafSelected = option.children.isEmpty() &&
                            selectedPath.isNotEmpty() &&
                            selectedPath.last() == option.value &&
                            selectedPath.size == levelIndex + 1

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = FieldSizeTokens.Medium.height)
                                .clip(shapes.sm)
                                .background(
                                    when {
                                        isSelected -> colors.muted
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable(enabled = !option.disabled) {
                                    val newPath = selectedPath.take(levelIndex) + option.value
                                    onSelect(newPath)
                                }
                                .padding(vertical = Spacing.sm, horizontal = Spacing.md),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option.label,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = Theme.typography.bodyMedium,
                                color = when {
                                    option.disabled -> colors.mutedForeground
                                    isLeafSelected -> colors.primary
                                    isSelected -> colors.foreground
                                    else -> colors.foreground
                                }
                            )

                            if (option.children.isNotEmpty()) {
                                Icon(
                                    name = Icons.caret_right,
                                    size = FieldDefaults.trailingIconSize,
                                    tint = if (isSelected) colors.foreground else colors.mutedForeground
                                )
                            } else if (isLeafSelected) {
                                Icon(
                                    name = Icons.check,
                                    size = FieldDefaults.trailingIconSize,
                                    tint = colors.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun buildCascaderLevels(
    options: List<CascaderOption>,
    selectedPath: List<String>
): List<List<CascaderOption>> {
    val levels = mutableListOf<List<CascaderOption>>()
    var currentOptions = options

    levels.add(currentOptions)

    selectedPath.forEach { value ->
        val selected = currentOptions.find { it.value == value }
        if (selected != null && selected.children.isNotEmpty()) {
            currentOptions = selected.children
            levels.add(currentOptions)
        }
    }

    return levels
}

private fun getDisplayText(
    options: List<CascaderOption>,
    selectedPath: List<String>,
    separator: String
): String {
    val labels = mutableListOf<String>()
    var currentOptions = options

    selectedPath.forEach { value ->
        val option = currentOptions.find { it.value == value }
        if (option != null) {
            labels.add(option.label)
            currentOptions = option.children
        }
    }

    return labels.joinToString(separator)
}

private fun findOptionByPath(
    options: List<CascaderOption>,
    path: List<String>
): CascaderOption? {
    var current: CascaderOption? = null
    var currentOptions = options

    path.forEach { value ->
        current = currentOptions.find { it.value == value }
        if (current == null) return null
        currentOptions = current!!.children
    }

    return current
}
