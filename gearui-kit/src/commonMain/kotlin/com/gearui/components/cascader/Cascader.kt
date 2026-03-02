package com.gearui.components.cascader

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.items
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Alignment
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
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.overlay.GearOverlayOptions
import com.gearui.overlay.GearOverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.rememberGearOverlay
import com.gearui.theme.Theme

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
 * Cascader - 级联选择器
 *
 * 基于 GearOverlay 系统实现
 *
 * Features:
 * - 多级选择
 * - 动态加载
 * - 真正的浮层，不破坏布局
 */
@Composable
fun Cascader(
    options: List<CascaderOption>,
    selectedPath: List<String>,
    onSelect: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "请选择",
    separator: String = " / ",
    dropdownHeight: Dp = 300.dp
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    val overlay = rememberGearOverlay()
    val density = LocalDensity.current

    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var overlayId by remember { mutableStateOf<Long?>(null) }

    // 用 State 包装
    val selectedPathState = rememberUpdatedState(selectedPath)
    val onSelectState = rememberUpdatedState(onSelect)

    val displayText = remember(selectedPath, options) {
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
            options = GearOverlayOptions(
                placement = GearOverlayPlacement.BottomLeft,
                offsetY = 4.dp,
                autoFlip = true,
                dismissPolicy = OverlayDismissPolicy.Dropdown
            ),
            onDismiss = {
                clearDropdownState()
            }
        ) {
            val widthDp = with(density) { anchorWidth.toDp() }

            CascaderDropdown(
                options = options,
                selectedPath = selectedPathState.value,
                onSelect = { path ->
                    onSelectState.value(path)
                    // Only close if reached leaf node
                    val option = findOptionByPath(options, path)
                    if (option?.children?.isEmpty() == true) {
                        closeDropdown()
                    }
                },
                height = dropdownHeight,
                width = widthDp
            )
        }
        expanded = true
    }

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
                .height(40.dp)
                .onGloballyPositioned { coordinates ->
                    anchorBounds = coordinates.boundsInRoot()
                }
                .clip(shapes.small)
                .border(
                    width = 1.dp,
                    color = if (expanded) colors.primary else colors.stroke,
                    shape = shapes.small
                )
                .background(colors.surface)
                .clickable {
                    if (expanded) closeDropdown() else openDropdown()
                }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = displayText,
                style = Typography.BodyMedium,
                color = if (selectedPath.isNotEmpty()) colors.textPrimary else colors.textPlaceholder
            )

            Icon(
                name = if (expanded) Icons.keyboard_arrow_up else Icons.keyboard_arrow_down,
                size = 16.dp,
                tint = colors.textSecondary
            )
        }
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
            .shadow(8.dp, shapes.small)
            .background(colors.surface, shapes.small)
            .border(1.dp, colors.stroke, shapes.small)
    ) {
        levels.forEachIndexed { levelIndex, levelOptions ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(
                        if (levelIndex > 0) {
                            Modifier.border(width = 1.dp, color = colors.stroke)
                        } else {
                            Modifier
                        }
                    )
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
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
                                .clip(shapes.small)
                                .background(
                                    when {
                                        isSelected -> colors.surfaceVariant
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable(enabled = !option.disabled) {
                                    val newPath = selectedPath.take(levelIndex) + option.value
                                    onSelect(newPath)
                                }
                                .padding(vertical = 8.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option.label,
                                style = Typography.BodyMedium,
                                color = when {
                                    option.disabled -> colors.textDisabled
                                    isLeafSelected -> colors.primary
                                    isSelected -> colors.textPrimary
                                    else -> colors.textPrimary
                                }
                            )

                            if (option.children.isNotEmpty()) {
                                Icon(
                                    name = Icons.chevron_right,
                                    size = 16.dp,
                                    tint = if (isSelected) colors.textPrimary else colors.textSecondary
                                )
                            } else if (isLeafSelected) {
                                Icon(
                                    name = Icons.check,
                                    size = 16.dp,
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
