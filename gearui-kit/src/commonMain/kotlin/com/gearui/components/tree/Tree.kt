package com.gearui.components.tree

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.items
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.interaction.collectIsPressedAsState
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.motion.FeedbackDefaults
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.components.checkbox.Checkbox
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text

import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.typography.IconSizes

/**
 * Tree node data
 */
data class TreeNode(
    val key: String,
    val title: String,
    val children: List<TreeNode> = emptyList(),
    val disabled: Boolean = false,
    val icon: String? = null
)

/**
 * Tree - Tree view component
 *
 * Tree control
 *
 * Features:
 * - Expandable nodes
 * - Checkable nodes
 * - Custom icons
 * - Disabled nodes
 *
 * Example:
 * ```
 * Tree(
 *     nodes = listOf(
 *         TreeNode(
 *             key = "1",
 *             title = "Parent node",
 *             children = listOf(
 *                 TreeNode(key = "1-1", title = "Child node")
 *             )
 *         )
 *     )
 * )
 * ```
 */
@Composable
fun Tree(
    nodes: List<TreeNode>,
    modifier: Modifier = Modifier,
    checkable: Boolean = false,
    checkedKeys: Set<String> = emptySet(),
    onCheckedChange: ((Set<String>) -> Unit)? = null,
    expandedKeys: Set<String> = emptySet(),
    onExpandedChange: ((Set<String>) -> Unit)? = null,
    onNodeClick: ((TreeNode) -> Unit)? = null
) {
    var internalExpanded by remember { mutableStateOf(expandedKeys) }
    val expanded = if (onExpandedChange != null) expandedKeys else internalExpanded
    TreeContent(nodes, modifier, checkable, checkedKeys, onCheckedChange, expanded,
        { if (onExpandedChange != null) onExpandedChange(it) else internalExpanded = it }, onNodeClick)
}

internal data class VisibleTreeNode(val node: TreeNode, val level: Int)

internal fun visibleTreeNodes(nodes: List<TreeNode>, expanded: Set<String>): List<VisibleTreeNode> = buildList {
    fun visit(items: List<TreeNode>, level: Int) {
        items.forEach { node ->
            add(VisibleTreeNode(node, level))
            if (node.key in expanded) visit(node.children, level + 1)
        }
    }
    visit(nodes, 0)
}

internal fun treeSelectionAncestors(nodes: List<TreeNode>, selected: Set<String>): Set<String> {
    val parents = buildParentMap(nodes)
    return selected.flatMap { getAncestors(it, parents).map { parent -> parent.key } }.toSet()
}

internal fun updateTreeChecked(nodes: List<TreeNode>, node: TreeNode, checked: Boolean, keys: Set<String>): Set<String> =
    handleNodeCheck(node, checked, keys, buildParentMap(nodes))

@Composable
internal fun TreeContent(
    nodes: List<TreeNode>, modifier: Modifier = Modifier,
    checkable: Boolean = false, checkedKeys: Set<String> = emptySet(),
    onCheckedChange: ((Set<String>) -> Unit)? = null,
    expanded: Set<String>, onExpandedChange: (Set<String>) -> Unit,
    onNodeClick: ((TreeNode) -> Unit)? = null, selectedKey: String? = null,
    scrollable: Boolean = false
) {
    val rows = remember(nodes, expanded) { visibleTreeNodes(nodes, expanded) }
    val row: @Composable (VisibleTreeNode) -> Unit = { item ->
        TreeNodeView(item.node, item.level, checkable, checkedKeys,
            { node, checked -> onCheckedChange?.invoke(updateTreeChecked(nodes, node, checked, checkedKeys)) },
            expanded, { key, open -> onExpandedChange(if (open) expanded + key else expanded - key) },
            onNodeClick, selectedKey == item.node.key)
    }
    if (scrollable) {
        LazyColumn(modifier.fillMaxWidth()) { items(rows, key = { it.node.key }) { row(it) } }
    } else {
        Column(modifier.fillMaxWidth()) { rows.forEach { item -> key(item.node.key) { row(item) } } }
    }
}

/**
 * Builds the parent map childKey -> parentNode
 */
private fun buildParentMap(nodes: List<TreeNode>): Map<String, TreeNode> {
    val map = mutableMapOf<String, TreeNode>()
    fun traverse(nodes: List<TreeNode>, parent: TreeNode?) {
        nodes.forEach { node ->
            if (parent != null) {
                map[node.key] = parent
            }
            traverse(node.children, node)
        }
    }
    traverse(nodes, null)
    return map
}

/**
 * Returns the keys of every descendant of a node
 */
private fun getAllDescendantKeys(node: TreeNode): Set<String> {
    val keys = mutableSetOf<String>()
    fun traverse(n: TreeNode) {
        n.children.forEach { child ->
            if (!child.disabled) {
                keys.add(child.key)
                traverse(child)
            }
        }
    }
    traverse(node)
    return keys
}

/**
 * Returns every ancestor of a node
 */
private fun getAncestors(nodeKey: String, parentMap: Map<String, TreeNode>): List<TreeNode> {
    val ancestors = mutableListOf<TreeNode>()
    var currentKey = nodeKey
    while (parentMap.containsKey(currentKey)) {
        val parent = parentMap[currentKey]!!
        ancestors.add(parent)
        currentKey = parent.key
    }
    return ancestors
}

/**
 * Whether every child of a node is checked
 */
private fun areAllChildrenChecked(node: TreeNode, checkedKeys: Set<String>): Boolean {
    if (node.children.isEmpty()) return true
    return node.children.filterNot { it.disabled }.all { child ->
        child.key in checkedKeys && areAllChildrenChecked(child, checkedKeys)
    }
}

/**
 * Whether any child of a node is checked
 */
private fun hasAnyChildChecked(node: TreeNode, checkedKeys: Set<String>): Boolean {
    if (node.children.isEmpty()) return false
    return node.children.any { child ->
        child.key in checkedKeys || hasAnyChildChecked(child, checkedKeys)
    }
}

/**
 * Handles node checking, including parent/child linkage
 */
private fun handleNodeCheck(
    targetNode: TreeNode,
    checked: Boolean,
    currentCheckedKeys: Set<String>,
    parentMap: Map<String, TreeNode>
): Set<String> {
    if (targetNode.disabled) return currentCheckedKeys
    val newKeys = currentCheckedKeys.toMutableSet()

    if (checked) {
        // Checking: add this node and every descendant
        newKeys.add(targetNode.key)
        newKeys.addAll(getAllDescendantKeys(targetNode))

        // Walk up and update the ancestors
        val ancestors = getAncestors(targetNode.key, parentMap)
        for (ancestor in ancestors) {
            if (ancestor.disabled) break
            // Check the parent once all of its children are checked
            if (areAllChildrenChecked(ancestor, newKeys)) {
                newKeys.add(ancestor.key)
            }
        }
    } else {
        // Unchecking: remove this node and every descendant
        newKeys.remove(targetNode.key)
        newKeys.removeAll(getAllDescendantKeys(targetNode))

        // Walk up and update the ancestors (unchecking)
        val ancestors = getAncestors(targetNode.key, parentMap)
        for (ancestor in ancestors) {
            if (ancestor.disabled) break
            newKeys.remove(ancestor.key)
        }
    }

    return newKeys
}

@Composable
private fun TreeNodeView(
    node: TreeNode, level: Int, checkable: Boolean, checkedKeys: Set<String>,
    onNodeCheckedChange: (TreeNode, Boolean) -> Unit,
    expanded: Set<String>, onExpandedChange: (String, Boolean) -> Unit,
    onNodeClick: ((TreeNode) -> Unit)?, selected: Boolean
) {
    val colors = Theme.colors
    val hasChildren = node.children.isNotEmpty()
    val isExpanded = node.key in expanded
    val isChecked = node.key in checkedKeys
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Row(
        Modifier.fillMaxWidth().heightIn(min = FieldSizeTokens.Medium.height)
            .background(if (pressed && !node.disabled) colors.muted else Color.Transparent)
            .clickable(enabled = !node.disabled, interactionSource = interaction, indication = null) {
                if (hasChildren) onExpandedChange(node.key, !isExpanded)
                else if (checkable) onNodeCheckedChange(node, !isChecked)
                onNodeClick?.invoke(node)
            }
            .padding(start = ControlGeometry.selectItemPadding + ControlGeometry.treeIndent * level,
                end = ControlGeometry.selectItemPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Box(Modifier.size(ControlGeometry.selectIndicatorSlot), contentAlignment = Alignment.Center) {
            if (hasChildren) Icon(if (isExpanded) Icons.caret_down else Icons.caret_right,
                size = IconSizes.Default.md, tint = colors.mutedForeground)
        }
        if (checkable) Checkbox(checked = isChecked,
            indeterminate = !isChecked && hasAnyChildChecked(node, checkedKeys),
            onCheckedChange = { onNodeCheckedChange(node, it) }, enabled = !node.disabled)
        node.icon?.let { Text(it, style = Theme.typography.bodyMedium, color = colors.mutedForeground) }
        Text(node.title, modifier = Modifier.weight(1f).alpha(if (node.disabled) FeedbackDefaults.disabledOpacity else 1f), style = Theme.typography.bodyMedium,
            color = colors.foreground, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (!checkable) Box(Modifier.size(ControlGeometry.selectIndicatorSlot), contentAlignment = Alignment.Center) {
            if (selected) Icon(Icons.check, size = IconSizes.Default.md, tint = if (node.disabled) colors.mutedForeground else colors.primary)
        }
    }
}

/**
 * Tree state manager
 */
class TreeState {
    var expandedKeys by mutableStateOf<Set<String>>(emptySet())
        // public setter

    var checkedKeys by mutableStateOf<Set<String>>(emptySet())
        // public setter

    fun expand(key: String) {
        expandedKeys = expandedKeys + key
    }

    fun collapse(key: String) {
        expandedKeys = expandedKeys - key
    }

    fun toggleExpand(key: String) {
        expandedKeys = if (key in expandedKeys) {
            expandedKeys - key
        } else {
            expandedKeys + key
        }
    }

    fun expandAll(nodes: List<TreeNode>) {
        expandedKeys = getAllKeys(nodes)
    }

    fun collapseAll() {
        expandedKeys = emptySet()
    }

    fun check(key: String) {
        checkedKeys = checkedKeys + key
    }

    fun uncheck(key: String) {
        checkedKeys = checkedKeys - key
    }

    fun toggleCheck(key: String) {
        checkedKeys = if (key in checkedKeys) {
            checkedKeys - key
        } else {
            checkedKeys + key
        }
    }

    fun checkAll(nodes: List<TreeNode>) {
        checkedKeys = getAllKeys(nodes)
    }

    fun uncheckAll() {
        checkedKeys = emptySet()
    }

    private fun getAllKeys(nodes: List<TreeNode>): Set<String> {
        val keys = mutableSetOf<String>()
        fun traverse(nodes: List<TreeNode>) {
            nodes.forEach { node ->
                keys.add(node.key)
                traverse(node.children)
            }
        }
        traverse(nodes)
        return keys
    }
}

@Composable
fun rememberTreeState(): TreeState {
    return remember { TreeState() }
}
