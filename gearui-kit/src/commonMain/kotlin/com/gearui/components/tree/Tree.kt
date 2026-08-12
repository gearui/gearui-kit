package com.gearui.components.tree

import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.checkbox.Checkbox
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography

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
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes

    var internalExpanded by remember { mutableStateOf(expandedKeys) }
    val expanded = if (onExpandedChange != null) expandedKeys else internalExpanded

    // Build the node relationship maps
    val nodeMap = remember(nodes) { buildNodeMap(nodes) }
    val parentMap = remember(nodes) { buildParentMap(nodes) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        nodes.forEach { node ->
            TreeNodeView(
                node = node,
                level = 0,
                checkable = checkable,
                checkedKeys = checkedKeys,
                onCheckedChange = { newKeys ->
                    onCheckedChange?.invoke(newKeys)
                },
                onNodeCheckedChange = { targetNode, checked ->
                    // Parent/child linkage
                    val newCheckedKeys = handleNodeCheck(
                        targetNode = targetNode,
                        checked = checked,
                        currentCheckedKeys = checkedKeys,
                        nodeMap = nodeMap,
                        parentMap = parentMap
                    )
                    onCheckedChange?.invoke(newCheckedKeys)
                },
                expanded = expanded,
                onExpandedChange = { key, isExpanded ->
                    val newExpanded = if (isExpanded) {
                        expanded + key
                    } else {
                        expanded - key
                    }
                    if (onExpandedChange != null) {
                        onExpandedChange(newExpanded)
                    } else {
                        internalExpanded = newExpanded
                    }
                },
                onNodeClick = onNodeClick,
                allNodes = nodes
            )
        }
    }
}

/**
 * Builds the node map key -> TreeNode
 */
private fun buildNodeMap(nodes: List<TreeNode>): Map<String, TreeNode> {
    val map = mutableMapOf<String, TreeNode>()
    fun traverse(nodes: List<TreeNode>) {
        nodes.forEach { node ->
            map[node.key] = node
            traverse(node.children)
        }
    }
    traverse(nodes)
    return map
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
            keys.add(child.key)
            traverse(child)
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
    return node.children.all { child ->
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
    nodeMap: Map<String, TreeNode>,
    parentMap: Map<String, TreeNode>
): Set<String> {
    val newKeys = currentCheckedKeys.toMutableSet()

    if (checked) {
        // Checking: add this node and every descendant
        newKeys.add(targetNode.key)
        newKeys.addAll(getAllDescendantKeys(targetNode))

        // Walk up and update the ancestors
        val ancestors = getAncestors(targetNode.key, parentMap)
        for (ancestor in ancestors) {
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
            newKeys.remove(ancestor.key)
        }
    }

    return newKeys
}

@Composable
private fun TreeNodeView(
    node: TreeNode,
    level: Int,
    checkable: Boolean,
    checkedKeys: Set<String>,
    onCheckedChange: ((Set<String>) -> Unit)?,
    onNodeCheckedChange: ((TreeNode, Boolean) -> Unit)?,
    expanded: Set<String>,
    onExpandedChange: (String, Boolean) -> Unit,
    onNodeClick: ((TreeNode) -> Unit)?,
    allNodes: List<TreeNode>
) {
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes

    val isExpanded = node.key in expanded
    val hasChildren = node.children.isNotEmpty()
    val isChecked = node.key in checkedKeys

    // Indeterminate state: some children checked, but not all
    val isIndeterminate = if (hasChildren) {
        val hasAnyChecked = hasAnyChildChecked(node, checkedKeys)
        val allChecked = areAllChildrenChecked(node, checkedKeys)
        hasAnyChecked && !allChecked && !isChecked
    } else {
        false
    }

    Column {
        // Node content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !node.disabled) {
                    if (hasChildren) {
                        onExpandedChange(node.key, !isExpanded)
                    }
                    onNodeClick?.invoke(node)
                }
                .padding(
                    start = (level * 24).dp + 8.dp,
                    top = Spacing.xs,
                    bottom = Spacing.xs,
                    end = Spacing.sm
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Expand/collapse icon
            if (hasChildren) {
                Icon(
                    name = if (isExpanded) Icons.keyboard_arrow_down else Icons.chevron_right,
                    size = IconSizes.Default.md,
                    tint = if (node.disabled) colors.mutedForeground else colors.mutedForeground,
                    modifier = Modifier.width(16.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(Spacing.lg))
            }

            // Checkbox
            if (checkable) {
                Checkbox(
                    checked = isChecked,
                    indeterminate = isIndeterminate,
                    onCheckedChange = { checked ->
                        // Use the linkage-aware callback
                        onNodeCheckedChange?.invoke(node, checked)
                    },
                    enabled = !node.disabled
                )
            }

            // Icon
            node.icon?.let { icon ->
                Text(
                    text = icon,
                    style = Typography.BodyMedium,
                    color = if (node.disabled) colors.mutedForeground else colors.mutedForeground
                )
            }

            // Title
            Text(
                text = node.title,
                style = Typography.BodyMedium,
                color = if (node.disabled) colors.mutedForeground else colors.foreground
            )
        }

        // Children
        if (isExpanded && hasChildren) {
            Column {
                node.children.forEach { child ->
                    TreeNodeView(
                        node = child,
                        level = level + 1,
                        checkable = checkable,
                        checkedKeys = checkedKeys,
                        onCheckedChange = onCheckedChange,
                        onNodeCheckedChange = onNodeCheckedChange,
                        expanded = expanded,
                        onExpandedChange = onExpandedChange,
                        onNodeClick = onNodeClick,
                        allNodes = allNodes
                    )
                }
            }
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
