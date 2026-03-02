package com.gearui.sample.examples.treeselect

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonType
import com.gearui.components.tree.TreeNode
import com.gearui.components.treeselect.TreeSelect
import com.gearui.components.treeselect.TreeSelectMultiple
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * TreeSelect 组件示例
 *
 * - 基础树形选择
 * - 多选树形选择
 * - 三级树形选择
 */
@Composable
fun TreeSelectExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    val basicOptions = remember {
        (1..5).map { i ->
            TreeNode(
                key = "$i",
                title = "选项$i",
                children = (1..5).map { j ->
                    TreeNode(
                        key = "${i}_$j",
                        title = "选项$i.$j"
                    )
                }
            )
        }
    }

    // 三级树节点
    val thirdLevelOptions = remember {
        listOf(
            TreeNode(
                key = "1",
                title = "一级选项1",
                children = listOf(
                    TreeNode(
                        key = "1_1",
                        title = "二级选项1.1",
                        children = listOf(
                            TreeNode(key = "1_1_1", title = "三级选项1.1.1"),
                            TreeNode(key = "1_1_2", title = "三级选项1.1.2"),
                            TreeNode(key = "1_1_3", title = "三级选项1.1.3")
                        )
                    ),
                    TreeNode(
                        key = "1_2",
                        title = "二级选项1.2",
                        children = listOf(
                            TreeNode(key = "1_2_1", title = "三级选项1.2.1"),
                            TreeNode(key = "1_2_2", title = "三级选项1.2.2")
                        )
                    )
                )
            ),
            TreeNode(
                key = "2",
                title = "一级选项2",
                children = listOf(
                    TreeNode(
                        key = "2_1",
                        title = "二级选项2.1",
                        children = listOf(
                            TreeNode(key = "2_1_1", title = "三级选项2.1.1")
                        )
                    )
                )
            ),
            TreeNode(
                key = "3",
                title = "一级选项3",
                children = listOf(
                    TreeNode(key = "3_1", title = "二级选项3.1"),
                    TreeNode(key = "3_2", title = "二级选项3.2")
                )
            )
        )
    }

    // 部门树节点（用于多选演示）
    val departmentNodes = remember {
        listOf(
            TreeNode(
                key = "tech",
                title = "技术部",
                children = listOf(
                    TreeNode(key = "frontend", title = "前端组"),
                    TreeNode(key = "backend", title = "后端组"),
                    TreeNode(key = "mobile", title = "移动端组"),
                    TreeNode(key = "qa", title = "测试组")
                )
            ),
            TreeNode(
                key = "product",
                title = "产品部",
                children = listOf(
                    TreeNode(key = "pm", title = "产品经理"),
                    TreeNode(key = "design", title = "设计组")
                )
            ),
            TreeNode(
                key = "operation",
                title = "运营部",
                children = listOf(
                    TreeNode(key = "market", title = "市场组"),
                    TreeNode(key = "customer", title = "客服组")
                )
            )
        )
    }

    // 状态
    var basicSelectedKey by remember { mutableStateOf<String?>(null) }
    var thirdSelectedKey by remember { mutableStateOf<String?>(null) }
    var multiSelectedKeys by remember { mutableStateOf<Set<String>>(emptySet()) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ========== 组件类型 ==========

        // 基础树形选择
        ExampleSection(
            title = "基础树形选择",
            description = "二级树形结构，点击叶子节点选择"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TreeSelect(
                    nodes = basicOptions,
                    selectedKey = basicSelectedKey,
                    onSelect = { basicSelectedKey = it },
                    placeholder = "请选择"
                )

                if (basicSelectedKey != null) {
                    Text(
                        text = "已选择: $basicSelectedKey",
                        style = Typography.BodySmall,
                        color = colors.success
                    )
                }
            }
        }

        // 多选树形选择
        ExampleSection(
            title = "多选树形选择",
            description = "支持复选框多选模式"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TreeSelectMultiple(
                    nodes = departmentNodes,
                    selectedKeys = multiSelectedKeys,
                    onSelectedChange = { multiSelectedKeys = it },
                    placeholder = "请选择部门"
                )

                if (multiSelectedKeys.isNotEmpty()) {
                    Text(
                        text = "已选择 ${multiSelectedKeys.size} 项: ${multiSelectedKeys.joinToString(", ")}",
                        style = Typography.BodySmall,
                        color = colors.success
                    )
                }

                // 快捷操作按钮
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        text = "全选",
                        onClick = {
                            multiSelectedKeys = setOf(
                                "tech", "frontend", "backend", "mobile", "qa",
                                "product", "pm", "design",
                                "operation", "market", "customer"
                            )
                        },
                        size = ButtonSize.SMALL,
                        type = ButtonType.OUTLINE
                    )
                    Button(
                        text = "清空",
                        onClick = { multiSelectedKeys = emptySet() },
                        size = ButtonSize.SMALL,
                        type = ButtonType.OUTLINE
                    )
                }
            }
        }

        // ========== 组件状态 ==========

        // 三级树形选择
        ExampleSection(
            title = "三级树形选择",
            description = "支持三级嵌套的树形结构"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TreeSelect(
                    nodes = thirdLevelOptions,
                    selectedKey = thirdSelectedKey,
                    onSelect = { thirdSelectedKey = it },
                    placeholder = "请选择",
                    dropdownHeight = 350.dp
                )

                if (thirdSelectedKey != null) {
                    Text(
                        text = "已选择: $thirdSelectedKey",
                        style = Typography.BodySmall,
                        color = colors.success
                    )
                }
            }
        }

        // ========== 快速测试 ==========

        ExampleSection(
            title = "快速测试",
            description = "快速重置各选择器状态"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    text = "重置基础",
                    onClick = { basicSelectedKey = null },
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    text = "重置多选",
                    onClick = { multiSelectedKeys = emptySet() },
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    text = "重置三级",
                    onClick = { thirdSelectedKey = null },
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
