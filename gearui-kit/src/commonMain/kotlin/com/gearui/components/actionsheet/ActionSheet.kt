package com.gearui.components.actionsheet

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.LocalOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.OverlayDefaults
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.runtime.LocalRuntimeEnvironment
import com.gearui.runtime.LocalRuntimeFlags
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing

/**
 * ActionSheet - 动作面板组件
 *
 * 由用户操作后触发的一种特定的模态弹出框，呈现一组与当前情境相关的两个或多个选项。
 *
 * - 列表型动作面板（可滚动）
 * - 宫格型动作面板
 * - 带描述、图标、徽标
 * - 居中/左对齐
 * - 选项状态（禁用、警告等）
 * - 点击反馈效果
 */

/**
 * ActionSheet 主题类型
 */
enum class ActionSheetTheme {
    /** 列表型 */
    LIST,
    /** 宫格型 */
    GRID
}

/**
 * ActionSheet 对齐方式
 */
enum class ActionSheetAlign {
    /** 居中对齐 */
    CENTER,
    /** 左对齐 */
    LEFT
}

/**
 * ActionSheet 选项
 */
data class ActionSheetItem(
    /** 选项文本 */
    val label: String,
    /** 选项描述 */
    val description: String? = null,
    /** 图标（可以是 emoji） */
    val icon: String? = null,
    /** 徽标文本 */
    val badge: String? = null,
    /** 是否显示红点 */
    val showRedPoint: Boolean = false,
    /** 是否禁用 */
    val disabled: Boolean = false,
    /** 自定义文字颜色 */
    val textColor: Color? = null
)

/**
 * ActionSheet 状态管理
 */
object ActionSheet {
    private var currentVisible = mutableStateOf(false)
    private var currentItems = mutableStateOf<List<ActionSheetItem>>(emptyList())
    private var currentTheme = mutableStateOf(ActionSheetTheme.LIST)
    private var currentAlign = mutableStateOf(ActionSheetAlign.CENTER)
    private var currentDescription = mutableStateOf<String?>(null)
    private var currentShowCancel = mutableStateOf(true)
    private var currentCancelText = mutableStateOf("取消")
    private var currentOnSelected = mutableStateOf<((ActionSheetItem, Int) -> Unit)?>(null)
    private var currentOnCancel = mutableStateOf<(() -> Unit)?>(null)
    private var currentGridColumns = mutableStateOf(4)

    /**
     * 显示列表型动作面板
     */
    fun showList(
        items: List<ActionSheetItem>,
        description: String? = null,
        align: ActionSheetAlign = ActionSheetAlign.CENTER,
        showCancel: Boolean = true,
        cancelText: String = "取消",
        onSelected: ((ActionSheetItem, Int) -> Unit)? = null,
        onCancel: (() -> Unit)? = null
    ) {
        currentItems.value = items
        currentTheme.value = ActionSheetTheme.LIST
        currentAlign.value = align
        currentDescription.value = description
        currentShowCancel.value = showCancel
        currentCancelText.value = cancelText
        currentOnSelected.value = onSelected
        currentOnCancel.value = onCancel
        currentVisible.value = true
    }

    /**
     * 显示宫格型动作面板
     */
    fun showGrid(
        items: List<ActionSheetItem>,
        description: String? = null,
        columns: Int = 4,
        showCancel: Boolean = true,
        cancelText: String = "取消",
        onSelected: ((ActionSheetItem, Int) -> Unit)? = null,
        onCancel: (() -> Unit)? = null
    ) {
        currentItems.value = items
        currentTheme.value = ActionSheetTheme.GRID
        currentDescription.value = description
        currentGridColumns.value = columns
        currentShowCancel.value = showCancel
        currentCancelText.value = cancelText
        currentOnSelected.value = onSelected
        currentOnCancel.value = onCancel
        currentVisible.value = true
    }

    /**
     * 关闭动作面板
     */
    fun dismiss() {
        currentVisible.value = false
    }

    /**
     * ActionSheet 宿主组件
     * 需要放置在页面根部
     */
    @Composable
    fun Host() {
        val visible by currentVisible
        val items by currentItems
        val theme by currentTheme
        val align by currentAlign
        val description by currentDescription
        val showCancel by currentShowCancel
        val cancelText by currentCancelText
        val onSelected by currentOnSelected
        val onCancel by currentOnCancel
        val gridColumns by currentGridColumns

        if (visible) {
            ActionSheetContent(
                visible = true,
                items = items,
                theme = theme,
                align = align,
                description = description,
                showCancel = showCancel,
                cancelText = cancelText,
                gridColumns = gridColumns,
                onSelected = { item, index ->
                    onSelected?.invoke(item, index)
                    dismiss()
                },
                onCancel = {
                    onCancel?.invoke()
                    dismiss()
                },
                onDismiss = { dismiss() }
            )
        }
    }
}

/**
 * ActionSheet 内容组件
 */
@Composable
fun ActionSheetContent(
    visible: Boolean,
    items: List<ActionSheetItem>,
    theme: ActionSheetTheme = ActionSheetTheme.LIST,
    align: ActionSheetAlign = ActionSheetAlign.CENTER,
    description: String? = null,
    showCancel: Boolean = true,
    cancelText: String = "取消",
    gridColumns: Int = 4,
    maxListHeight: Dp = 400.dp,
    onSelected: ((ActionSheetItem, Int) -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onDismiss: () -> Unit = {}
) {
    val colors = Theme.colors
    val controller = LocalOverlayController.current
    var overlayId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(visible) {
        if (visible) {
            overlayId = controller.show(
                anchorBounds = null,
                options = OverlayOptions(
                    placement = OverlayPlacement.Fullscreen,
                    modal = true,
                    maskColor = OverlayDefaults.scrimColor,
                    dismissPolicy = OverlayDismissPolicy.Sheet.copy(
                        outsideClick = true
                    )
                ),
                onDismiss = onDismiss
            ) {
                ActionSheetSurface(
                    items = items,
                    theme = theme,
                    align = align,
                    description = description,
                    showCancel = showCancel,
                    cancelText = cancelText,
                    gridColumns = gridColumns,
                    maxListHeight = maxListHeight,
                    onSelected = onSelected,
                    onCancel = onCancel,
                    onDismiss = onDismiss
                )
            }
        } else {
            overlayId?.let { controller.dismiss(it) }
            overlayId = null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            overlayId?.let { controller.dismiss(it) }
        }
    }
}

@Composable
private fun ActionSheetSurface(
    items: List<ActionSheetItem>,
    theme: ActionSheetTheme,
    align: ActionSheetAlign,
    description: String?,
    showCancel: Boolean,
    cancelText: String,
    gridColumns: Int,
    maxListHeight: Dp,
    onSelected: ((ActionSheetItem, Int) -> Unit)?,
    onCancel: (() -> Unit)?,
    onDismiss: () -> Unit
) {
    val colors = Theme.colors
    val runtimeFlags = LocalRuntimeFlags.current
    val runtimeEnvironment = LocalRuntimeEnvironment.current
    val configuration = LocalConfiguration.current
    val safeAreaBottom = if (runtimeFlags.unifiedSafeAreaPipeline) {
        if (runtimeFlags.actionSheetConsumesBottomSafeArea) {
            runtimeEnvironment.safeArea.bottom
        } else {
            0.dp
        }
    } else {
        configuration.safeAreaInsets.bottom.dp
    }
    val bottomInset = if (safeAreaBottom > Spacing.lg) safeAreaBottom else Spacing.lg

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = Spacing.md, topEnd = Spacing.md))
                .background(colors.surface)
                .clickable { /* 阻止点击穿透 */ }
        ) {
            // 描述文本
            if (description != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface)
                        .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                    contentAlignment = when (align) {
                        ActionSheetAlign.CENTER -> Alignment.Center
                        ActionSheetAlign.LEFT -> Alignment.CenterStart
                    }
                ) {
                    Text(
                        text = description,
                        style = Typography.BodyMedium,
                        color = colors.mutedForeground
                    )
                }
                // 分隔线
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(colors.border)
                )
            }

            // 内容区域
            when (theme) {
                ActionSheetTheme.LIST -> {
                    ActionSheetList(
                        items = items,
                        align = align,
                        maxHeight = maxListHeight,
                        onSelected = onSelected
                    )
                }
                ActionSheetTheme.GRID -> {
                    ActionSheetGrid(
                        items = items,
                        columns = gridColumns,
                        onSelected = onSelected
                    )
                }
            }

            // 取消按钮
            if (showCancel) {
                // 间隔
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Spacing.sm)
                        .background(colors.muted)
                )

                // 取消按钮 - 带点击效果
                var cancelPressed by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            if (cancelPressed) colors.muted else colors.surface
                        )
                        .clickable {
                            cancelPressed = true
                            onCancel?.invoke()
                            onDismiss()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cancelText,
                        style = Typography.BodyLarge,
                        color = colors.foreground
                    )
                }
            }

            // 底部安全区
            Spacer(modifier = Modifier.height(bottomInset))
        }
    }
}

/**
 * 列表型动作面板内容 - 支持滚动
 */
@Composable
private fun ActionSheetList(
    items: List<ActionSheetItem>,
    align: ActionSheetAlign,
    maxHeight: Dp,
    onSelected: ((ActionSheetItem, Int) -> Unit)?
) {
    val colors = Theme.colors

    // 计算列表高度：每项56dp（有描述则78dp），最大不超过 maxHeight
    val itemHeightNormal = 56
    val itemHeightWithDesc = 78
    var totalHeightValue = 0
    items.forEach { item ->
        totalHeightValue += if (item.description != null) itemHeightWithDesc else itemHeightNormal
    }
    val totalHeight = totalHeightValue.dp
    val listHeight = if (totalHeight.value > maxHeight.value) maxHeight else totalHeight

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(listHeight)
            .background(colors.surface)
    ) {
        itemsIndexed(items) { index, item ->
            ActionSheetListItem(
                item = item,
                align = align,
                showDivider = index < items.size - 1,
                onClick = {
                    if (!item.disabled) {
                        onSelected?.invoke(item, index)
                    }
                }
            )
        }
    }
}

/**
 * 列表项 - 带点击反馈效果
 */
@Composable
private fun ActionSheetListItem(
    item: ActionSheetItem,
    align: ActionSheetAlign,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    var isPressed by remember { mutableStateOf(false) }

    val textColor = when {
        item.disabled -> colors.mutedForeground
        item.textColor != null -> item.textColor
        else -> colors.foreground
    }

    val horizontalArrangement = when (align) {
        ActionSheetAlign.CENTER -> Arrangement.Center
        ActionSheetAlign.LEFT -> Arrangement.Start
    }

    val itemHeight = if (item.description != null) 78.dp else 56.dp

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(
                    if (isPressed && !item.disabled) colors.muted else colors.surface
                )
                .clickable(enabled = !item.disabled) {
                    isPressed = true
                    onClick()
                }
                .padding(horizontal = Spacing.lg),
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            if (item.icon != null) {
                Text(
                    text = item.icon,
                    style = Typography.TitleMedium,
                    color = if (item.disabled) colors.mutedForeground else colors.foreground
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
            }

            // 文本内容
            Column(
                horizontalAlignment = when (align) {
                    ActionSheetAlign.CENTER -> Alignment.CenterHorizontally
                    ActionSheetAlign.LEFT -> Alignment.Start
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.label,
                        style = Typography.BodyLarge,
                        color = textColor
                    )

                    // 徽标
                    if (item.badge != null) {
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Spacing.sm))
                                .background(colors.destructive)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.badge,
                                style = Typography.BodyExtraSmall,
                                color = colors.destructiveForeground
                            )
                        }
                    }

                    // 红点
                    if (item.showRedPoint && item.badge == null) {
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Box(
                            modifier = Modifier
                                .size(Spacing.sm)
                                .clip(RoundedCornerShape(Spacing.xs))
                                .background(colors.destructive)
                        )
                    }
                }

                // 描述
                if (item.description != null) {
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = item.description,
                        style = Typography.BodyMedium,
                        color = colors.mutedForeground
                    )
                }
            }
        }

        // 分隔线
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .height(0.5.dp)
                    .background(colors.border)
            )
        }
    }
}

/**
 * 宫格型动作面板内容 - 支持滚动
 */
@Composable
private fun ActionSheetGrid(
    items: List<ActionSheetItem>,
    columns: Int,
    onSelected: ((ActionSheetItem, Int) -> Unit)?
) {
    val colors = Theme.colors
    val rows = (items.size + columns - 1) / columns

    // 每行高度 96dp，最多显示 2 行，超过则滚动
    val rowHeight = 96.dp
    val maxRows = 2
    val displayRows = minOf(rows, maxRows)
    val needScroll = rows > maxRows

    if (needScroll) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight * maxRows)
                .padding(vertical = Spacing.sm)
        ) {
            items(rows) { rowIndex ->
                ActionSheetGridRow(
                    items = items,
                    rowIndex = rowIndex,
                    columns = columns,
                    onSelected = onSelected
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.sm)
        ) {
            for (rowIndex in 0 until rows) {
                ActionSheetGridRow(
                    items = items,
                    rowIndex = rowIndex,
                    columns = columns,
                    onSelected = onSelected
                )
            }
        }
    }
}

/**
 * 宫格行
 */
@Composable
private fun ActionSheetGridRow(
    items: List<ActionSheetItem>,
    rowIndex: Int,
    columns: Int,
    onSelected: ((ActionSheetItem, Int) -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (colIndex in 0 until columns) {
            val itemIndex = rowIndex * columns + colIndex
            if (itemIndex < items.size) {
                val item = items[itemIndex]
                ActionSheetGridItem(
                    item = item,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (!item.disabled) {
                            onSelected?.invoke(item, itemIndex)
                        }
                    }
                )
            } else {
                // 空占位
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

/**
 * 宫格项 - 带点击反馈效果
 */
@Composable
private fun ActionSheetGridItem(
    item: ActionSheetItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    var isPressed by remember { mutableStateOf(false) }
    val textColor = if (item.disabled) colors.mutedForeground else colors.foreground

    val shapes = Theme.shapes

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Spacing.sm))
            .background(
                if (isPressed && !item.disabled) colors.muted else Color.Transparent
            )
            .clickable(enabled = !item.disabled) {
                isPressed = true
                onClick()
            }
            .padding(Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 图标区域
        Box {
            if (item.icon != null) {
                Box(
                    modifier = Modifier
                        .size(Spacing.huge)
                        .clip(RoundedCornerShape(Spacing.sm))
                        .background(colors.muted),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.icon,
                        style = Typography.HeadlineSmall,
                        color = if (item.disabled) colors.mutedForeground else colors.foreground
                    )
                }
            }

            // 徽标
            if (item.badge != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = Spacing.xs, y = -Spacing.xs)
                        .clip(RoundedCornerShape(Spacing.sm))
                        .background(colors.destructive)
                        .padding(horizontal = Spacing.xs, vertical = 1.dp)
                ) {
                    Text(
                        text = item.badge,
                        style = Typography.BodyExtraSmall,
                        color = colors.primaryForeground
                    )
                }
            }

            // 红点
            if (item.showRedPoint && item.badge == null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                        .size(Spacing.sm)
                        .clip(RoundedCornerShape(Spacing.xs))
                        .background(colors.destructive)
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        // 文本
        Text(
            text = item.label,
            style = Typography.BodySmall,
            color = textColor
        )
    }
}
