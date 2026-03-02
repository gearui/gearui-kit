package com.gearui.sample.examples.transfer

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.transfer.Transfer
import com.gearui.components.transfer.TransferItem
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Transfer 组件示例
 *
 * 穿梭框组件，用于双栏数据选择
 */
@Composable
fun TransferExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础穿梭框
        ExampleSection(
            title = "基础穿梭框",
            description = "双栏穿梭选择，支持左右移动数据"
        ) {
            var selectedKeys by remember { mutableStateOf<Set<String>>(emptySet()) }

            val items = remember {
                listOf(
                    TransferItem(key = "1", label = "选项 1"),
                    TransferItem(key = "2", label = "选项 2"),
                    TransferItem(key = "3", label = "选项 3"),
                    TransferItem(key = "4", label = "选项 4"),
                    TransferItem(key = "5", label = "选项 5")
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Transfer(
                    items = items,
                    selectedKeys = selectedKeys,
                    onSelectedKeysChange = { selectedKeys = it },
                    titles = "源列表" to "目标列表",
                    height = 250.dp
                )

                Text(
                    text = "已选择: ${selectedKeys.joinToString(", ").ifEmpty { "无" }}",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }
        }

        // 带搜索的穿梭框
        ExampleSection(
            title = "带搜索功能",
            description = "支持在列表中搜索过滤选项"
        ) {
            var selectedKeys by remember { mutableStateOf<Set<String>>(setOf("2", "4")) }

            val items = remember {
                listOf(
                    TransferItem(key = "1", label = "北京"),
                    TransferItem(key = "2", label = "上海"),
                    TransferItem(key = "3", label = "广州"),
                    TransferItem(key = "4", label = "深圳"),
                    TransferItem(key = "5", label = "杭州"),
                    TransferItem(key = "6", label = "南京"),
                    TransferItem(key = "7", label = "成都"),
                    TransferItem(key = "8", label = "武汉")
                )
            }

            Transfer(
                items = items,
                selectedKeys = selectedKeys,
                onSelectedKeysChange = { selectedKeys = it },
                titles = "可选城市" to "已选城市",
                searchable = true,
                height = 300.dp
            )
        }

        // 禁用项
        ExampleSection(
            title = "禁用项",
            description = "部分选项可设置为禁用状态"
        ) {
            var selectedKeys by remember { mutableStateOf<Set<String>>(emptySet()) }

            val items = remember {
                listOf(
                    TransferItem(key = "1", label = "可选项 1"),
                    TransferItem(key = "2", label = "禁用项", disabled = true),
                    TransferItem(key = "3", label = "可选项 2"),
                    TransferItem(key = "4", label = "可选项 3"),
                    TransferItem(key = "5", label = "禁用项 2", disabled = true)
                )
            }

            Transfer(
                items = items,
                selectedKeys = selectedKeys,
                onSelectedKeysChange = { selectedKeys = it },
                titles = "源列表" to "目标列表",
                searchable = false,
                height = 200.dp
            )
        }

        // 自定义标题
        ExampleSection(
            title = "自定义标题",
            description = "可自定义左右列表的标题"
        ) {
            var selectedKeys by remember { mutableStateOf<Set<String>>(setOf("admin", "editor")) }

            val items = remember {
                listOf(
                    TransferItem(key = "admin", label = "管理员"),
                    TransferItem(key = "editor", label = "编辑"),
                    TransferItem(key = "viewer", label = "查看者"),
                    TransferItem(key = "guest", label = "访客")
                )
            }

            Transfer(
                items = items,
                selectedKeys = selectedKeys,
                onSelectedKeysChange = { selectedKeys = it },
                titles = "可分配角色" to "已分配角色",
                searchable = false,
                height = 200.dp
            )
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Transfer 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. items: 所有可选项列表",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. selectedKeys: 已选中项的 key 集合",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. titles: 左右列表的标题",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. searchable: 是否显示搜索框",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "5. disabled: 禁用某些选项",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
