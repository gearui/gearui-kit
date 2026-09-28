package com.gearui.sample.examples.contextmenu

import androidx.compose.runtime.Composable
import com.gearui.components.button.Button
import com.gearui.components.contextmenu.ContextMenu
import com.gearui.components.contextmenu.ContextMenuItem
import com.gearui.components.icon.Icons
import com.gearui.components.toast.Toast
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection

/**
 * ContextMenu: a list of actions hanging off its trigger.
 */
@Composable
fun ContextMenuExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val basicItems = listOf(
        ContextMenuItem("复制") { Toast.show("复制") },
        ContextMenuItem("分享") { Toast.show("分享") },
        ContextMenuItem("删除", danger = true) { Toast.show("删除") }
    )
    val iconItems = listOf(
        ContextMenuItem("复制", icon = Icons.copy) { Toast.show("复制") },
        ContextMenuItem("分享", icon = Icons.share_network) { Toast.show("分享") },
        ContextMenuItem("编辑", icon = Icons.pencil_simple, disabled = true) { },
        ContextMenuItem("删除", icon = Icons.trash, danger = true) { Toast.show("删除") }
    )

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "基础用法",
            description = "点击触发元素弹出操作菜单，danger 项为危险色"
        ) {
            ContextMenu(items = basicItems) { onOpen ->
                Button(text = "打开菜单", onClick = onOpen)
            }
        }

        ExampleSection(
            title = "图标与禁用",
            description = "icon 设置前置图标，disabled 项不可点击"
        ) {
            ContextMenu(items = iconItems) { onOpen ->
                Button(text = "打开菜单", onClick = onOpen)
            }
        }
    }
}
