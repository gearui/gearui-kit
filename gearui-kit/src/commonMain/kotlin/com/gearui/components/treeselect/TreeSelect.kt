package com.gearui.components.treeselect

import com.gearui.foundation.field.FieldSurface
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.select.SelectIndicator
import com.gearui.foundation.control.ControlGeometry
import com.gearui.components.tree.TreeContent
import com.gearui.components.tree.treeSelectionAncestors
import com.gearui.components.tree.TreeNode
import com.gearui.foundation.primitives.Text
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.rememberOverlay
import com.gearui.theme.Theme
import com.gearui.theme.LocalInputColors
import com.gearui.i18n.formatArgs
import com.gearui.i18n.I18n
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.field.fieldTriggerModifier
import com.gearui.foundation.field.FieldErrorText

/**
 * TreeSelect - tree select
 *
 * A dropdown tree select built on the Overlay system
 *
 * Features:
 * - dropdown tree view
 * - single and multiple selection
 * - a real floating layer, leaving the layout untouched
 * - dismisses on scroll (handled centrally by the Overlay Runtime)
 * - dropdownHeight is a maximum; short trees use their natural content height
 */
@Composable
fun TreeSelect(
    nodes: List<TreeNode>,
    selectedKey: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = I18n.strings.field.selectPlaceholder,
    enabled: Boolean = true,
    error: String? = null,
    dropdownHeight: Dp = 300.dp
) {
    val overlay = rememberOverlay()
    val density = LocalDensity.current
    val nodesState = rememberUpdatedState(nodes)
    val heightState = rememberUpdatedState(dropdownHeight)

    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var overlayId by remember { mutableStateOf<Long?>(null) }

    // Wrapped in State so the lambdas can read the current value
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

            TreeSelectContent(
                nodes = nodesState.value, selectedKey = selectedKeyState.value,
                width = widthDp, height = heightState.value,
                onNodeClick = { node ->
                    if (node.children.isEmpty()) {
                        onSelectState.value(node.key)
                        closeDropdown()
                    }
                }
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
        FieldSurface(Modifier.fillMaxWidth()) {
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
                    text = selectedNode?.title ?: placeholder,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = Theme.typography.bodyMedium,
                    color = if (selectedNode != null) LocalInputColors.current.foreground else LocalInputColors.current.placeholder
                )

                SelectIndicator(expanded)
            }
        }

        FieldErrorText(error)
    }
}

/**
 * TreeSelectMultiple - multi-select tree select
 */
@Composable
fun TreeSelectMultiple(
    nodes: List<TreeNode>,
    selectedKeys: Set<String>,
    onSelectedChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = I18n.strings.field.selectPlaceholder,
    enabled: Boolean = true,
    error: String? = null,
    dropdownHeight: Dp = 300.dp
) {
    val overlay = rememberOverlay()
    val density = LocalDensity.current
    val nodesState = rememberUpdatedState(nodes)
    val heightState = rememberUpdatedState(dropdownHeight)

    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var overlayId by remember { mutableStateOf<Long?>(null) }

    val selectedKeysState = rememberUpdatedState(selectedKeys)
    val onSelectedChangeState = rememberUpdatedState(onSelectedChange)

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

            TreeSelectContent(
                nodes = nodesState.value,
                selectedKeys = selectedKeysState.value,
                multiple = true,
                onSelectedChange = { onSelectedChangeState.value(it) },
                width = widthDp,
                height = heightState.value
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
        FieldSurface(Modifier.fillMaxWidth()) {
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
                    text = if (selectedKeys.isEmpty()) placeholder
                    else I18n.strings.field.selectedCountFormat.formatArgs("count" to selectedKeys.size),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = Theme.typography.bodyMedium,
                    color = if (selectedKeys.isNotEmpty()) LocalInputColors.current.foreground else LocalInputColors.current.placeholder
                )

                SelectIndicator(expanded)
            }
        }

        FieldErrorText(error)
    }
}

/** Shared selection surface; expanded nodes become individually keyed lazy rows. */
@Composable
private fun TreeSelectContent(
    nodes: List<TreeNode>, width: Dp, height: Dp,
    selectedKey: String? = null, selectedKeys: Set<String> = emptySet(),
    multiple: Boolean = false, onSelectedChange: ((Set<String>) -> Unit)? = null,
    onNodeClick: ((TreeNode) -> Unit)? = null
) {
    val colors = Theme.colors
    val shape = Theme.shapes.xl
    var expandedKeys by remember {
        mutableStateOf(treeSelectionAncestors(nodes, selectedKeys + listOfNotNull(selectedKey)))
    }
    Box(Modifier.width(width).heightIn(max = height)
        .shadow(Theme.elevation.floating, shape).clip(shape)
        .background(colors.popover).padding(ControlGeometry.selectContentPadding)) {
        if (nodes.isEmpty()) {
            Text(I18n.strings.common.noData, modifier = Modifier.padding(Spacing.sm),
                style = Theme.typography.bodyMedium, color = colors.mutedForeground)
        } else TreeContent(nodes = nodes, checkable = multiple, checkedKeys = selectedKeys,
            onCheckedChange = onSelectedChange, expanded = expandedKeys,
            onExpandedChange = { expandedKeys = it }, onNodeClick = onNodeClick,
            selectedKey = selectedKey, scrollable = true)
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
