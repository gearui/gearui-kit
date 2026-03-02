package com.gearui.components.bottomsheet

import androidx.compose.runtime.*
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.gestures.detectTapGestures
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.primitives.DividerFull
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
import com.gearui.overlay.GearOverlayOptions
import com.gearui.overlay.GearOverlayPlacement
import com.gearui.overlay.LocalGearOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.Spacing

/**
 * BottomSheet - 基于 Overlay 系统的底部动作面板
 *
 * 使用 GearUI Overlay 系统实现，无论在哪里调用都能全屏弹出。
 *
 * 特性：
 * - 基于 Overlay 系统，全局弹出
 * - 底部弹出面板
 * - 列表型选项（支持滚动）
 * - 支持取消按钮
 * - 支持标题/描述
 * - 点击遮罩关闭
 * - 危险项高亮
 *
 * @param visible 是否显示
 * @param onDismiss 关闭回调
 * @param title 标题
 * @param description 描述
 * @param items 选项列表
 * @param showCancel 是否显示取消按钮
 * @param cancelText 取消按钮文字
 * @param closeOnClickOutside 点击外部是否关闭
 * @param maxListHeight 列表最大高度，超过则滚动，默认 400dp
 * @param onItemClick 选项点击回调
 */
@Composable
fun BottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String? = null,
    description: String? = null,
    items: List<BottomSheetItem>,
    showCancel: Boolean = true,
    cancelText: String = "取消",
    closeOnClickOutside: Boolean = true,
    maxListHeight: Dp = 400.dp,
    onItemClick: (BottomSheetItem, Int) -> Unit
) {
    val colors = Theme.colors
    val controller = LocalGearOverlayController.current
    var overlayId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(visible) {
        if (visible) {
            overlayId = controller.show(
                anchorBounds = null,
                options = GearOverlayOptions(
                    placement = GearOverlayPlacement.Fullscreen,
                    modal = true,
                    maskColor = colors.mask,
                    dismissPolicy = OverlayDismissPolicy.Sheet.copy(
                        outsideClick = closeOnClickOutside
                    )
                ),
                onDismiss = onDismiss
            ) {
                BottomSheetSurface(
                    title = title,
                    description = description,
                    items = items,
                    showCancel = showCancel,
                    cancelText = cancelText,
                    maxListHeight = maxListHeight,
                    onDismiss = onDismiss,
                    onItemClick = onItemClick
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

/**
 * BottomSheetSurface - BottomSheet 统一视觉容器
 */
@Composable
internal fun BottomSheetSurface(
    title: String? = null,
    description: String? = null,
    items: List<BottomSheetItem>,
    showCancel: Boolean = true,
    cancelText: String = "取消",
    maxListHeight: Dp = 400.dp,
    onDismiss: () -> Unit,
    onItemClick: (BottomSheetItem, Int) -> Unit
) {
    val colors = Theme.colors

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    // 阻止事件穿透到背景
                    detectTapGestures { }
                }
        ) {
            // 主面板
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = Spacing.spacer12.dp, topEnd = Spacing.spacer12.dp))
                    .background(colors.surface)
            ) {
                // 标题区域
                if (title != null || description != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.spacer16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (title != null) {
                            Text(
                                text = title,
                                style = Typography.TitleMedium,
                                color = colors.textPrimary
                            )
                        }

                        if (description != null) {
                            Spacer(modifier = Modifier.height(Spacing.spacer4.dp))
                            Text(
                                text = description,
                                style = Typography.BodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }

                    DividerFull()
                }

                // 选项列表 - 支持滚动
                BottomSheetItemList(
                    items = items,
                    maxHeight = maxListHeight,
                    onDismiss = onDismiss,
                    onItemClick = onItemClick
                )
            }

            // 取消按钮
            if (showCancel) {
                Spacer(modifier = Modifier.height(Spacing.spacer8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface)
                        .clickable(onClick = onDismiss)
                        .padding(vertical = Spacing.spacer16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cancelText,
                        style = Typography.BodyLarge,
                        color = colors.textPrimary
                    )
                }
            }

            // 底部安全区域
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Spacing.spacer16.dp)
                    .background(colors.surface)
            )
        }
    }
}

/**
 * BottomSheetItemList - 选项列表（支持滚动）
 */
@Composable
private fun BottomSheetItemList(
    items: List<BottomSheetItem>,
    maxHeight: Dp,
    onDismiss: () -> Unit,
    onItemClick: (BottomSheetItem, Int) -> Unit
) {
    val colors = Theme.colors

    // 计算列表高度：每项 56dp，最大不超过 maxHeight
    val itemHeight = 56
    val totalHeightValue = items.size * itemHeight
    val totalHeight = totalHeightValue.dp
    val listHeight = if (totalHeight > maxHeight) maxHeight else totalHeight

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(listHeight)
            .background(colors.surface)
    ) {
        itemsIndexed(items) { index, item ->
            BottomSheetItemRow(
                item = item,
                showDivider = index < items.size - 1,
                onClick = {
                    if (!item.disabled) {
                        onItemClick(item, index)
                        onDismiss()
                    }
                }
            )
        }
    }
}

/**
 * BottomSheetItemRow - 底部面板选项行
 */
@Composable
private fun BottomSheetItemRow(
    item: BottomSheetItem,
    showDivider: Boolean = true,
    onClick: () -> Unit
) {
    val colors = Theme.colors

    val textColor = when {
        item.disabled -> colors.textDisabled
        item.danger -> colors.danger
        else -> colors.textPrimary
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable(enabled = !item.disabled, onClick = onClick)
                .padding(horizontal = Spacing.spacer16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (item.icon != null) {
                    item.icon.invoke()
                    Spacer(modifier = Modifier.width(Spacing.spacer8.dp))
                }

                Text(
                    text = item.label,
                    style = Typography.BodyLarge,
                    color = textColor
                )
            }
        }

        // 分隔线
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.spacer16.dp)
                    .height(0.5.dp)
                    .background(colors.border)
            )
        }
    }
}

/**
 * BottomSheetItem - 底部面板选项数据类
 */
data class BottomSheetItem(
    /** 选项文本 */
    val label: String,

    /** 选项图标（可选） */
    val icon: (@Composable () -> Unit)? = null,

    /** 是否为危险操作（红色文字） */
    val danger: Boolean = false,

    /** 是否禁用 */
    val disabled: Boolean = false,

    /** 附加数据 */
    val data: Any? = null
)

/**
 * BottomSheet.Host - 基于 Overlay 系统的通用底部弹出容器
 *
 * 用于自定义内容的底部弹出面板
 */
object BottomSheet {

    @Composable
    fun Host(
        visible: Boolean,
        onDismiss: () -> Unit,
        closeOnClickOutside: Boolean = true,
        content: @Composable () -> Unit
    ) {
        val colors = Theme.colors
        val controller = LocalGearOverlayController.current
        var overlayId by remember { mutableStateOf<Long?>(null) }

        LaunchedEffect(visible) {
            if (visible) {
                overlayId = controller.show(
                    anchorBounds = null,
                    options = GearOverlayOptions(
                        placement = GearOverlayPlacement.Fullscreen,
                        modal = true,
                        maskColor = colors.mask,
                        dismissPolicy = OverlayDismissPolicy.Sheet.copy(
                            outsideClick = closeOnClickOutside
                        )
                    ),
                    onDismiss = onDismiss
                ) {
                    BottomSheetHostSurface(content = content)
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
}

/**
 * BottomSheetHostSurface - 自定义内容的底部弹出容器
 */
@Composable
private fun BottomSheetHostSurface(
    content: @Composable () -> Unit
) {
    val colors = Theme.colors

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = Spacing.spacer12.dp, topEnd = Spacing.spacer12.dp))
                .background(colors.surface)
                .pointerInput(Unit) {
                    // 阻止事件穿透到背景
                    detectTapGestures { }
                }
        ) {
            content()
        }
    }
}

/**
 * BottomSheetState - 便捷状态管理类
 *
 * 使用方式：
 * ```
 * val sheetState = remember { BottomSheetState() }
 *
 * BottomSheet(
 *     visible = sheetState.visible,
 *     onDismiss = { sheetState.hide() },
 *     items = listOf(
 *         BottomSheetItem("分享"),
 *         BottomSheetItem("删除", danger = true)
 *     ),
 *     onItemClick = { item, index ->
 *         // 处理点击
 *     }
 * )
 * ```
 */
class BottomSheetState {
    var visible by mutableStateOf(false)
        private set

    fun show() {
        visible = true
    }

    fun hide() {
        visible = false
    }

    fun toggle() {
        visible = !visible
    }
}
