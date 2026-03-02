package com.gearui.sample.examples.tree

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.tree.Tree
import com.gearui.components.tree.TreeNode
import com.gearui.components.tree.rememberTreeState
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Tree 组件示例
 */
@Composable
fun TreeExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 基础树形数据
    val basicTreeData = listOf(
        TreeNode(
            key = "1",
            title = "一级节点 1",
            children = listOf(
                TreeNode(key = "1-1", title = "二级节点 1-1"),
                TreeNode(key = "1-2", title = "二级节点 1-2"),
                TreeNode(
                    key = "1-3",
                    title = "二级节点 1-3",
                    children = listOf(
                        TreeNode(key = "1-3-1", title = "三级节点 1-3-1"),
                        TreeNode(key = "1-3-2", title = "三级节点 1-3-2")
                    )
                )
            )
        ),
        TreeNode(
            key = "2",
            title = "一级节点 2",
            children = listOf(
                TreeNode(key = "2-1", title = "二级节点 2-1"),
                TreeNode(key = "2-2", title = "二级节点 2-2")
            )
        ),
        TreeNode(
            key = "3",
            title = "一级节点 3"
        )
    )

    // 带图标的树形数据
    val iconTreeData = listOf(
        TreeNode(
            key = "folder-1",
            title = "文档",
            icon = "📁",
            children = listOf(
                TreeNode(key = "file-1", title = "README.md", icon = "📄"),
                TreeNode(key = "file-2", title = "LICENSE", icon = "📄"),
                TreeNode(
                    key = "folder-2",
                    title = "src",
                    icon = "📁",
                    children = listOf(
                        TreeNode(key = "file-3", title = "index.kt", icon = "📄"),
                        TreeNode(key = "file-4", title = "utils.kt", icon = "📄")
                    )
                )
            )
        ),
        TreeNode(
            key = "folder-3",
            title = "图片",
            icon = "📁",
            children = listOf(
                TreeNode(key = "img-1", title = "logo.png", icon = "🖼️"),
                TreeNode(key = "img-2", title = "banner.jpg", icon = "🖼️")
            )
        )
    )

    // 带禁用节点的数据
    val disabledTreeData = listOf(
        TreeNode(
            key = "d1",
            title = "可选节点",
            children = listOf(
                TreeNode(key = "d1-1", title = "子节点 1"),
                TreeNode(key = "d1-2", title = "禁用节点", disabled = true),
                TreeNode(key = "d1-3", title = "子节点 3")
            )
        ),
        TreeNode(
            key = "d2",
            title = "禁用的父节点",
            disabled = true,
            children = listOf(
                TreeNode(key = "d2-1", title = "子节点也不可点击")
            )
        )
    )

    // 基础展开状态
    var basicExpanded by remember { mutableStateOf(setOf("1")) }

    // 可选中树的状态
    val checkableTreeState = rememberTreeState()

    // 点击反馈
    var clickedNode by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础用法
        ExampleSection(
            title = "基础用法",
            description = "点击节点展开/收起子节点"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Tree(
                    nodes = basicTreeData,
                    expandedKeys = basicExpanded,
                    onExpandedChange = { basicExpanded = it },
                    onNodeClick = { node ->
                        clickedNode = "点击了: ${node.title}"
                    }
                )
            }

            if (clickedNode.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = clickedNode,
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }
        }

        // 带复选框
        ExampleSection(
            title = "带复选框",
            description = "设置 checkable=true 支持节点选中"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Tree(
                        nodes = basicTreeData,
                        checkable = true,
                        checkedKeys = checkableTreeState.checkedKeys,
                        onCheckedChange = { checkableTreeState.checkedKeys = it },
                        expandedKeys = checkableTreeState.expandedKeys,
                        onExpandedChange = { checkableTreeState.expandedKeys = it }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        text = "全选",
                        onClick = { checkableTreeState.checkAll(basicTreeData) },
                        size = ButtonSize.SMALL
                    )
                    Button(
                        text = "取消全选",
                        onClick = { checkableTreeState.uncheckAll() },
                        size = ButtonSize.SMALL
                    )
                    Button(
                        text = "展开全部",
                        onClick = { checkableTreeState.expandAll(basicTreeData) },
                        size = ButtonSize.SMALL
                    )
                    Button(
                        text = "收起全部",
                        onClick = { checkableTreeState.collapseAll() },
                        size = ButtonSize.SMALL
                    )
                }

                if (checkableTreeState.checkedKeys.isNotEmpty()) {
                    Text(
                        text = "已选中: ${checkableTreeState.checkedKeys.joinToString(", ")}",
                        style = Typography.BodySmall,
                        color = colors.primary
                    )
                }
            }
        }

        // 带图标
        ExampleSection(
            title = "自定义图标",
            description = "通过 icon 属性设置节点图标"
        ) {
            var iconExpanded by remember { mutableStateOf(setOf("folder-1", "folder-2")) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Tree(
                    nodes = iconTreeData,
                    expandedKeys = iconExpanded,
                    onExpandedChange = { iconExpanded = it }
                )
            }
        }

        // 禁用节点
        ExampleSection(
            title = "禁用节点",
            description = "设置 disabled=true 禁用指定节点"
        ) {
            var disabledExpanded by remember { mutableStateOf(setOf("d1")) }
            var disabledChecked by remember { mutableStateOf<Set<String>>(emptySet()) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Tree(
                    nodes = disabledTreeData,
                    checkable = true,
                    checkedKeys = disabledChecked,
                    onCheckedChange = { disabledChecked = it },
                    expandedKeys = disabledExpanded,
                    onExpandedChange = { disabledExpanded = it }
                )
            }
        }

        // 默认展开全部
        ExampleSection(
            title = "默认展开全部",
            description = "初始化时展开所有节点"
        ) {
            val allExpandedState = rememberTreeState()

            // 初始展开全部
            LaunchedEffect(Unit) {
                allExpandedState.expandAll(basicTreeData)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Tree(
                    nodes = basicTreeData,
                    expandedKeys = allExpandedState.expandedKeys,
                    onExpandedChange = { allExpandedState.expandedKeys = it }
                )
            }
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Tree 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. 支持多级嵌套节点",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. 支持节点展开/收起",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. 支持复选框选择模式",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. 支持自定义节点图标",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "5. 支持禁用指定节点",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "6. 提供 TreeState 便捷管理状态",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
