package com.gearui.components.treeselect

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.icon.Icons
import com.gearui.components.tree.Tree
import com.gearui.components.tree.TreeNode
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.overlay.GearOverlayOptions
import com.gearui.overlay.GearOverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.rememberGearOverlay
import com.gearui.theme.Theme

/**
 * TreeSelect - 树形选择器
 *
 * 基于 GearOverlay 系统实现的树形下拉选择器
 *
 * Features:
 * - 下拉树形视图
 * - 单选/多选支持
 * - 真正的浮层，不破坏布局
 * - 滚动自动关闭（由 Overlay Runtime 统一处理）
 */
@Composable
fun TreeSelect(
    nodes: List<TreeNode>,
    selectedKey: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "请选择",
    dropdownHeight: Dp = 300.dp
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    val overlay = rememberGearOverlay()
    val density = LocalDensity.current

    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var overlayId by remember { mutableStateOf<Long?>(null) }

    // 用 State 包装，让 lambda 内部能访问最新值
    val selectedKeyState = rememberUpdatedState(selectedKey)
    val onSelectState = rememberUpdatedState(onSelect)

    val selectedNode = remember(selectedKey, nodes) {
        findNodeByKey(nodes, selectedKey)
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

            Box(
                modifier = Modifier
                    .width(widthDp)
                    .height(dropdownHeight)
                    .shadow(8.dp, shapes.small)
                    .background(colors.surface, shapes.small)
                    .border(1.dp, colors.stroke, shapes.small)
                    .padding(8.dp)
            ) {
                Tree(
                    nodes = nodes,
                    onNodeClick = { node ->
                        if (node.children.isEmpty()) {
                            onSelectState.value(node.key)
                            closeDropdown()
                        }
                    }
                )
            }
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
                text = selectedNode?.title ?: placeholder,
                style = Typography.BodyMedium,
                color = if (selectedNode != null) colors.textPrimary else colors.textPlaceholder
            )

            Icon(
                name = if (expanded) Icons.keyboard_arrow_up else Icons.keyboard_arrow_down,
                size = 16.dp,
                tint = colors.textSecondary
            )
        }
    }
}

/**
 * TreeSelectMultiple - 多选树形选择器
 */
@Composable
fun TreeSelectMultiple(
    nodes: List<TreeNode>,
    selectedKeys: Set<String>,
    onSelectedChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "请选择",
    dropdownHeight: Dp = 300.dp
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    val overlay = rememberGearOverlay()
    val density = LocalDensity.current

    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var overlayId by remember { mutableStateOf<Long?>(null) }

    // 内部状态用于 Overlay 中的实时更新
    var internalSelectedKeys by remember(selectedKeys) { mutableStateOf(selectedKeys) }

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

        // 同步内部状态
        internalSelectedKeys = selectedKeys

        overlayId = overlay.show(
            anchorBounds = bounds,
            options = GearOverlayOptions(
                placement = GearOverlayPlacement.BottomLeft,
                offsetY = 4.dp,
                autoFlip = true,
                dismissPolicy = OverlayDismissPolicy.Dropdown
            ),
            onDismiss = {
                // 关闭时同步外部状态
                onSelectedChange(internalSelectedKeys)
                clearDropdownState()
            }
        ) {
            val widthDp = with(density) { anchorWidth.toDp() }

            TreeSelectMultipleContent(
                nodes = nodes,
                selectedKeys = internalSelectedKeys,
                onSelectedChange = { newKeys ->
                    internalSelectedKeys = newKeys
                    onSelectedChange(newKeys)
                },
                width = widthDp,
                height = dropdownHeight
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
                text = if (selectedKeys.isEmpty()) placeholder else "已选择 ${selectedKeys.size} 项",
                style = Typography.BodyMedium,
                color = if (selectedKeys.isNotEmpty()) colors.textPrimary else colors.textPlaceholder
            )

            Icon(
                name = if (expanded) Icons.keyboard_arrow_up else Icons.keyboard_arrow_down,
                size = 16.dp,
                tint = colors.textSecondary
            )
        }
    }
}

/**
 * 多选树内容组件 - 用于在 Overlay 中保持状态
 */
@Composable
private fun TreeSelectMultipleContent(
    nodes: List<TreeNode>,
    selectedKeys: Set<String>,
    onSelectedChange: (Set<String>) -> Unit,
    width: Dp,
    height: Dp
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .shadow(8.dp, shapes.small)
            .background(colors.surface, shapes.small)
            .border(1.dp, colors.stroke, shapes.small)
            .padding(8.dp)
    ) {
        Tree(
            nodes = nodes,
            checkable = true,
            checkedKeys = selectedKeys,
            onCheckedChange = onSelectedChange
        )
    }
}

private fun findNodeByKey(nodes: List<TreeNode>, key: String?): TreeNode? {
    if (key == null) return null

    fun search(nodes: List<TreeNode>): TreeNode? {
        for (node in nodes) {
            if (node.key == key) return node
            search(node.children)?.let { return it }
        }
        return null
    }

    return search(nodes)
}
