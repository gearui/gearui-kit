package com.gearui.components

import com.gearui.components.tree.*
import kotlin.test.*

class TreeModelTest {
    private val first = TreeNode("a", "First")
    private val disabled = TreeNode("disabled", "Disabled", disabled = true)
    private val parent = TreeNode("root", "Parent", listOf(first, disabled))
    private val nodes = listOf(parent)

    @Test fun collapsedTreeContainsOnlyRoots() {
        assertEquals(listOf("root"), visibleTreeNodes(nodes, emptySet()).map { it.node.key })
    }
    @Test fun expandedTreeKeepsStableOrderAndDepth() {
        val rows = visibleTreeNodes(nodes, setOf("root"))
        assertEquals(listOf("root", "a", "disabled"), rows.map { it.node.key })
        assertEquals(listOf(0, 1, 1), rows.map { it.level })
    }
    @Test fun selectedLeafReopensItsAncestors() {
        assertEquals(setOf("root"), treeSelectionAncestors(nodes, setOf("a", "missing")))
    }
    @Test fun checkingParentSkipsDisabledDescendants() {
        assertEquals(setOf("root", "a"), updateTreeChecked(nodes, parent, true, emptySet()))
    }
    @Test fun uncheckingParentPreservesDisabledSelection() {
        assertEquals(setOf("disabled"), updateTreeChecked(nodes, parent, false, setOf("root", "a", "disabled")))
    }
    @Test fun disabledTargetCannotChangeSelection() {
        assertEquals(setOf("a"), updateTreeChecked(nodes, disabled, true, setOf("a")))
    }
    @Test fun enabledChildrenDetermineParentCheck() {
        assertEquals(setOf("root", "a"), updateTreeChecked(nodes, first, true, emptySet()))
    }
    @Test fun disabledBranchStopsPropagation() {
        val branch = TreeNode("blocked", "Blocked", listOf(first), disabled = true)
        val root = TreeNode("root", "Root", listOf(branch))
        assertEquals(setOf("root"), updateTreeChecked(listOf(root), root, true, emptySet()))
        assertEquals(setOf("a"), updateTreeChecked(listOf(root), first, true, emptySet()))
    }
}
