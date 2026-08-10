package com.gearui.components.select

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.items
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.rememberOverlay
import com.gearui.theme.Theme
import com.gearui.i18n.formatArgs
import com.gearui.i18n.I18n
import com.gearui.foundation.elevation.Elevation
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.overlay.OverlayDefaults
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth

/**
 * Select - 100% Theme 驱动的下拉选择器
 *
 * 使用 GearUI Overlay 系统实现：
 * - 真正的浮层，不破坏页面布局
 * - 无全屏遮罩（点击外部关闭）
 * - 自动方向判断（底部空间不足时向上展开）
 * - 选项可滚动
 * - 宽度跟随触发器
 * - 支持 triggerOverlaid 连体覆盖模式
 */
@Composable
fun <T> Select(
    value: T?,
    options: List<SelectOption<T>>,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = I18n.strings.field.selectPlaceholder,
    label: String? = null,
    error: String? = null,
    panelMode: SelectPanelMode = SelectPanelMode.TRIGGER_OVERLAID
) {
    val colors = Theme.colors
    val overlay = rememberOverlay()
    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var overlayId by remember { mutableStateOf<Long?>(null) }
    val selectedOption = options.find { it.value == value }
    val triggerShape = FieldDefaults.shape

    // 用 State 包装，让 lambda 内部能访问最新值
    val valueState = rememberUpdatedState(value)
    val onValueChangeState = rememberUpdatedState(onValueChange)

    // 关闭下拉（仅清除状态，不调用 dismiss，因为 dismiss 会触发 onDismiss）
    fun clearDropdownState() {
        overlayId = null
        expanded = false
    }

    // 关闭下拉（手动调用 dismiss）
    fun closeDropdown() {
        overlayId?.let { overlay.dismiss(it) }
        // 注意：状态清除由 onDismiss 回调处理
    }

    // 打开下拉
    fun openDropdown() {
        if (anchorBounds == null) return

        val bounds = anchorBounds!!
        val anchorWidth = bounds.width

        overlayId = overlay.show(
            anchorBounds = bounds,
            options = OverlayOptions(
                placement = OverlayPlacement.BottomLeft,
                offsetY = panelMode.offsetY(),
                autoFlip = true,
                dismissPolicy = OverlayDismissPolicy.Dropdown
            ),
            onDismiss = {
                // 无论是手动关闭还是点击外部关闭，都清除状态
                clearDropdownState()
            }
        ) {
            // 直接从 State 对象读取最新值
            SelectDropdownContent(
                options = options,
                selectedValue = valueState.value,
                anchorWidth = anchorWidth,
                panelMode = panelMode,
                onOptionClick = { option ->
                    onValueChangeState.value(option.value)
                    closeDropdown()
                }
            )
        }
        expanded = true
    }

    // 组件销毁时关闭 Overlay
    DisposableEffect(Unit) {
        onDispose {
            overlayId?.let { overlay.dismiss(it) }
        }
    }

    Column(modifier = modifier) {
        // 标签
        if (label != null) {
            Text(
                text = label,
                style = Typography.BodyMedium,
                color = if (enabled) colors.foreground else colors.mutedForeground,
                modifier = Modifier.padding(bottom = Spacing.sm)
            )
        }

        // 触发器
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(FieldSizeTokens.Medium.height)
                .onGloballyPositioned { coordinates ->
                    if (!expanded) {
                        anchorBounds = coordinates.boundsInRoot()
                    }
                }
                .clip(triggerShape)
                .border(
                    width = FieldSizeTokens.Medium.borderWidth,
                    color = when {
                        error != null -> colors.destructive
                        !enabled -> colors.mutedForeground
                        expanded -> colors.border
                        else -> colors.border
                    },
                    shape = triggerShape
                )
                .background(if (enabled) colors.surface else colors.muted)
                .clickable(enabled = enabled) {
                    if (expanded) {
                        closeDropdown()
                    } else {
                        openDropdown()
                    }
                }
                .padding(horizontal = FieldSizeTokens.Medium.paddingHorizontal),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedOption?.label ?: placeholder,
                style = Typography.BodyMedium,
                color = if (selectedOption != null) {
                    if (enabled) colors.foreground else colors.mutedForeground
                } else {
                    colors.mutedForeground
                }
            )

            Icon(
                name = if (expanded) Icons.keyboard_arrow_up else Icons.keyboard_arrow_down,
                size = FieldDefaults.trailingIconSize,
                tint = if (enabled) colors.mutedForeground else colors.mutedForeground
            )
        }

        // 错误提示
        if (error != null) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = error,
                style = Typography.BodySmall,
                color = colors.destructive
            )
        }
    }
}

/**
 * SelectDropdownContent - 下拉内容（使用 LazyColumn 可滚动）
 */
@Composable
private fun <T> SelectDropdownContent(
    options: List<SelectOption<T>>,
    selectedValue: T?,
    anchorWidth: Float,
    panelMode: SelectPanelMode,
    onOptionClick: (SelectOption<T>) -> Unit
) {
    val colors = Theme.colors
    val density = LocalDensity.current

    val widthDp = with(density) { anchorWidth.toDp() }
    val panelShape = OverlayDefaults.panelShape
    val panelShadow =
        if (panelMode == SelectPanelMode.TRIGGER_OVERLAID) Elevation.raised else Elevation.floating
    val itemHeight = 44.dp
    val verticalPadding = Spacing.sm
    val rowSpacing = 4.dp
    val totalHeight = (options.size * itemHeight.value + verticalPadding.value * 2f + (options.size - 1).coerceAtLeast(0) * rowSpacing.value).dp
    val panelHeight = if (totalHeight > 240.dp) 240.dp else totalHeight

    Box(
        modifier = Modifier
            .width(widthDp)
            .height(panelHeight)
            .shadow(panelShadow, panelShape)
            .clip(panelShape)
            .background(colors.surface, panelShape)
            .border(BorderWidth.thin, colors.border, panelShape)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            items(options) { option ->
                SelectOptionItem(
                    option = option,
                    isSelected = option.value == selectedValue,
                    onClick = { onOptionClick(option) }
                )
            }
        }
    }
}

/**
 * SelectOptionItem - 选项项
 */
@Composable
private fun <T> SelectOptionItem(
    option: SelectOption<T>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = Theme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.lg)
            .background(if (isSelected) colors.muted else colors.surface)
            .clickable(enabled = !option.disabled) { onClick() }
            .height(44.dp)
            .padding(horizontal = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = option.label,
            style = Typography.BodyMedium,
            color = when {
                option.disabled -> colors.mutedForeground
                else -> colors.foreground
            }
        )
    }
}

/**
 * SelectOption - 选项数据类
 */
data class SelectOption<T>(
    val value: T,
    val label: String,
    val disabled: Boolean = false,
    val group: String? = null
)

/**
 * MultiSelect - 多选下拉选择器
 */
@Composable
fun <T> MultiSelect(
    values: Set<T>,
    options: List<SelectOption<T>>,
    onValuesChange: (Set<T>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = I18n.strings.field.selectPlaceholder,
    label: String? = null,
    maxSelection: Int? = null,
    panelMode: SelectPanelMode = SelectPanelMode.TRIGGER_OVERLAID
) {
    val colors = Theme.colors
    val overlay = rememberOverlay()
    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var overlayId by remember { mutableStateOf<Long?>(null) }
    val triggerShape = FieldDefaults.shape

    // 用 State 包装，让 lambda 内部能访问最新值
    val valuesState = rememberUpdatedState(values)
    val onValuesChangeState = rememberUpdatedState(onValuesChange)

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
                offsetY = panelMode.offsetY(),
                autoFlip = true,
                dismissPolicy = OverlayDismissPolicy.Dropdown
            ),
            onDismiss = {
                clearDropdownState()
            }
        ) {
            // 直接从 State 对象读取最新值
            MultiSelectDropdownContent(
                options = options,
                selectedValues = valuesState.value,
                anchorWidth = anchorWidth,
                panelMode = panelMode,
                onSelectionChange = { newValues ->
                    if (maxSelection == null || newValues.size <= maxSelection) {
                        onValuesChangeState.value(newValues)
                    }
                }
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
        if (label != null) {
            Text(
                text = label,
                style = Typography.BodyMedium,
                color = if (enabled) colors.foreground else colors.mutedForeground,
                modifier = Modifier.padding(bottom = Spacing.sm)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(FieldSizeTokens.Medium.height)
                .onGloballyPositioned { coordinates ->
                    if (!expanded) {
                        anchorBounds = coordinates.boundsInRoot()
                    }
                }
                .clip(triggerShape)
                .border(
                    FieldSizeTokens.Medium.borderWidth,
                    if (expanded) colors.border else colors.border,
                    triggerShape
                )
                .background(if (enabled) colors.surface else colors.muted)
                .clickable(enabled = enabled) {
                    if (expanded) closeDropdown() else openDropdown()
                }
                .padding(horizontal = FieldSizeTokens.Medium.paddingHorizontal),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (values.isEmpty()) placeholder
                    else I18n.strings.field.selectedCountFormat.formatArgs("count" to values.size),
                style = Typography.BodyMedium,
                color = if (values.isNotEmpty()) {
                    if (enabled) colors.foreground else colors.mutedForeground
                } else {
                    colors.mutedForeground
                }
            )

            Icon(
                name = if (expanded) Icons.keyboard_arrow_up else Icons.keyboard_arrow_down,
                size = FieldDefaults.trailingIconSize,
                tint = if (enabled) colors.mutedForeground else colors.mutedForeground
            )
        }
    }
}

/**
 * Select 面板模式
 */
enum class SelectPanelMode {
    ITEM_ALIGNED,
    TRIGGER_OVERLAID
}

private fun SelectPanelMode.offsetY() = when (this) {
    SelectPanelMode.ITEM_ALIGNED -> 4.dp
    SelectPanelMode.TRIGGER_OVERLAID -> 0.dp
}

/**
 * MultiSelectDropdownContent - 多选下拉内容
 */
@Composable
private fun <T> MultiSelectDropdownContent(
    options: List<SelectOption<T>>,
    selectedValues: Set<T>,
    anchorWidth: Float,
    panelMode: SelectPanelMode,
    onSelectionChange: (Set<T>) -> Unit
) {
    val colors = Theme.colors
    val density = LocalDensity.current

    val widthDp = with(density) { anchorWidth.toDp() }
    val panelShape = OverlayDefaults.panelShape
    val panelShadow =
        if (panelMode == SelectPanelMode.TRIGGER_OVERLAID) Elevation.raised else Elevation.floating
    val itemHeight = 44.dp
    val verticalPadding = Spacing.sm
    val rowSpacing = 4.dp
    val totalHeight = (options.size * itemHeight.value + verticalPadding.value * 2f + (options.size - 1).coerceAtLeast(0) * rowSpacing.value).dp
    val panelHeight = if (totalHeight > 240.dp) 240.dp else totalHeight

    Box(
        modifier = Modifier
            .width(widthDp)
            .height(panelHeight)
            .shadow(panelShadow, panelShape)
            .clip(panelShape)
            .background(colors.surface, panelShape)
            .border(BorderWidth.thin, colors.border, panelShape)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            items(options) { option ->
                val isSelected = option.value in selectedValues

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Theme.shapes.lg)
                        .background(if (isSelected) colors.muted else colors.surface)
                        .clickable(enabled = !option.disabled) {
                            val newValues = if (isSelected) {
                                selectedValues - option.value
                            } else {
                                selectedValues + option.value
                            }
                            onSelectionChange(newValues)
                        }
                        .height(44.dp)
                        .padding(horizontal = Spacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option.label,
                        style = Typography.BodyMedium,
                        color = when {
                            option.disabled -> colors.mutedForeground
                            isSelected -> colors.primary
                            else -> colors.foreground
                        }
                    )

                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(Theme.shapes.sm)
                            .border(BorderWidth.thin,
                                if (isSelected) colors.primary else colors.border,
                                Theme.shapes.sm
                            )
                            .background(if (isSelected) colors.primary else colors.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                name = Icons.check,
                                size = 12.dp,
                                tint = colors.primaryForeground
                            )
                        }
                    }
                }
            }
        }
    }
}
