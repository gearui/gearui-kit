package com.gearui.sample.examples.actionsheet

import com.gearui.components.icon.*
import com.gearui.components.icon.Icons
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.components.actionsheet.*
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.toast.Toast
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.theme.Theme

private class SheetRow(val title: String, val description: String? = null, val onClick: () -> Unit)

/**
 * ActionSheet: a modal panel raised by a user action, offering two or more options relevant to the current context.
 */
@Composable
fun ActionSheetExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // ActionSheet state
    var showBasicList by remember { mutableStateOf(false) }
    var showDescList by remember { mutableStateOf(false) }
    var showIconList by remember { mutableStateOf(false) }
    var showBadgeList by remember { mutableStateOf(false) }
    var showItemDescList by remember { mutableStateOf(false) }
    var showBasicGrid by remember { mutableStateOf(false) }
    var showDescGrid by remember { mutableStateOf(false) }
    var showBadgeGrid by remember { mutableStateOf(false) }
    var showStateList by remember { mutableStateOf(false) }
    var showIconStateList by remember { mutableStateOf(false) }
    var showCenterBadgeList by remember { mutableStateOf(false) }
    var showCenterIconList by remember { mutableStateOf(false) }
    var showLeftBadgeList by remember { mutableStateOf(false) }
    var showLeftIconList by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        ExamplePage(
            component = component,
            onBack = onBack
        ) {
            // ==================== Component types ====================

            // List-style action sheet
            ExampleSection(
                surface = SectionSurface.Plain,
                title = "列表型",
                description = "常规、带面板描述、带图标、带徽标、带选项描述"
            ) {
                SheetRows(
                    listOf(
                        SheetRow("常规列表") { showBasicList = true },
                        SheetRow("带描述列表", "面板顶部带描述文字") { showDescList = true },
                        SheetRow("带图标列表") { showIconList = true },
                        SheetRow("带徽标列表") { showBadgeList = true },
                        SheetRow("带选项描述列表", "每个选项下方带描述") { showItemDescList = true },
                    )
                )
            }

            // Grid-style action sheet
            ExampleSection(
                surface = SectionSurface.Plain,
                title = "宫格型",
                description = "ActionSheetTheme.GRID：常规、带面板描述、带徽标"
            ) {
                SheetRows(
                    listOf(
                        SheetRow("常规宫格") { showBasicGrid = true },
                        SheetRow("带描述宫格", "面板顶部带描述文字") { showDescGrid = true },
                        SheetRow("带徽标宫格") { showBadgeGrid = true },
                    )
                )
            }

            // ==================== Component states ====================

            // List option states
            ExampleSection(
                surface = SectionSurface.Plain,
                title = "选项状态",
                description = "默认、自定义颜色、禁用、警告"
            ) {
                SheetRows(
                    listOf(
                        SheetRow("列表型选项状态") { showStateList = true },
                        SheetRow("列表型带图标状态") { showIconStateList = true },
                    )
                )
            }

            // ==================== Component styles ====================

            // List alignment
            ExampleSection(
                surface = SectionSurface.Plain,
                title = "对齐方式",
                description = "ActionSheetAlign：CENTER 居中、LEFT 左对齐"
            ) {
                SheetRows(
                    listOf(
                        SheetRow("居中带徽标列表", "ActionSheetAlign.CENTER") { showCenterBadgeList = true },
                        SheetRow("居中带图标列表", "ActionSheetAlign.CENTER") { showCenterIconList = true },
                        SheetRow("左对齐带徽标列表", "ActionSheetAlign.LEFT") { showLeftBadgeList = true },
                        SheetRow("左对齐带图标列表", "ActionSheetAlign.LEFT") { showLeftIconList = true },
                    )
                )
            }
        }

        // ==================== ActionSheet dialogs ====================

        // Plain list
        if (showBasicList) {
            ActionSheetContent(
                visible = true,
                items = listOf(
                    ActionSheetItem(label = "选项一"),
                    ActionSheetItem(label = "选项二"),
                    ActionSheetItem(label = "选项三"),
                    ActionSheetItem(label = "选项四")
                ),
                onSelected = { item, _ ->
                    Toast.show("选中了：${item.label}")
                    showBasicList = false
                },
                onCancel = { showBasicList = false },
                onDismiss = { showBasicList = false }
            )
        }

        // List with descriptions
        if (showDescList) {
            ActionSheetContent(
                visible = true,
                description = "动作面板描述文字",
                items = listOf(
                    ActionSheetItem(label = "选项一"),
                    ActionSheetItem(label = "选项二"),
                    ActionSheetItem(label = "选项三"),
                    ActionSheetItem(label = "选项四")
                ),
                onSelected = { item, _ ->
                    Toast.show("选中了：${item.label}")
                    showDescList = false
                },
                onCancel = { showDescList = false },
                onDismiss = { showDescList = false }
            )
        }

        // List with icons
        if (showIconList) {
            ActionSheetContent(
                visible = true,
                items = listOf(
                    ActionSheetItem(label = "选项一", icon = Icons.gear),
                    ActionSheetItem(label = "选项二", icon = Icons.gear),
                    ActionSheetItem(label = "选项三", icon = Icons.gear),
                    ActionSheetItem(label = "选项四", icon = Icons.gear)
                ),
                onSelected = { item, _ ->
                    Toast.show("选中了：${item.label}")
                    showIconList = false
                },
                onCancel = { showIconList = false },
                onDismiss = { showIconList = false }
            )
        }

        // List with badges
        if (showBadgeList) {
            ActionSheetContent(
                visible = true,
                items = listOf(
                    ActionSheetItem(label = "选项一", showRedPoint = true),
                    ActionSheetItem(label = "选项二", badge = "8"),
                    ActionSheetItem(label = "选项三", badge = "99"),
                    ActionSheetItem(label = "选项四", badge = "99+")
                ),
                onSelected = { item, _ ->
                    Toast.show("选中了：${item.label}")
                    showBadgeList = false
                },
                onCancel = { showBadgeList = false },
                onDismiss = { showBadgeList = false }
            )
        }

        // List with Cell descriptions
        if (showItemDescList) {
            ActionSheetContent(
                visible = true,
                items = listOf(
                    ActionSheetItem(label = "选项一", description = "描述一"),
                    ActionSheetItem(label = "选项二", description = "描述二"),
                    ActionSheetItem(label = "选项三", description = "描述三"),
                    ActionSheetItem(label = "选项四", description = "描述四")
                ),
                onSelected = { item, _ ->
                    Toast.show("选中了：${item.label}")
                    showItemDescList = false
                },
                onCancel = { showItemDescList = false },
                onDismiss = { showItemDescList = false }
            )
        }

        // Plain grid
        if (showBasicGrid) {
            ActionSheetContent(
                visible = true,
                theme = ActionSheetTheme.GRID,
                items = listOf(
                    ActionSheetItem(label = "微信", icon = Icons.chatCircle),
                    ActionSheetItem(label = "朋友圈", icon = Icons.shareNetwork),
                    ActionSheetItem(label = "QQ", icon = Icons.chats),
                    ActionSheetItem(label = "企业微信", icon = Icons.usersThree),
                    ActionSheetItem(label = "收藏", icon = Icons.star),
                    ActionSheetItem(label = "刷新", icon = Icons.arrowClockwise),
                    ActionSheetItem(label = "下载", icon = Icons.downloadSimple),
                    ActionSheetItem(label = "复制", icon = Icons.copy)
                ),
                onSelected = { item, _ ->
                    Toast.show("选中了：${item.label}")
                    showBasicGrid = false
                },
                onCancel = { showBasicGrid = false },
                onDismiss = { showBasicGrid = false }
            )
        }

        // Grid with descriptions
        if (showDescGrid) {
            ActionSheetContent(
                visible = true,
                theme = ActionSheetTheme.GRID,
                description = "动作面板描述文字",
                items = listOf(
                    ActionSheetItem(label = "微信", icon = Icons.chatCircle),
                    ActionSheetItem(label = "朋友圈", icon = Icons.shareNetwork),
                    ActionSheetItem(label = "QQ", icon = Icons.chats),
                    ActionSheetItem(label = "企业微信", icon = Icons.usersThree),
                    ActionSheetItem(label = "收藏", icon = Icons.star),
                    ActionSheetItem(label = "刷新", icon = Icons.arrowClockwise),
                    ActionSheetItem(label = "下载", icon = Icons.downloadSimple),
                    ActionSheetItem(label = "复制", icon = Icons.copy)
                ),
                onSelected = { item, _ ->
                    Toast.show("选中了：${item.label}")
                    showDescGrid = false
                },
                onCancel = { showDescGrid = false },
                onDismiss = { showDescGrid = false }
            )
        }

        // Grid with badges
        if (showBadgeGrid) {
            ActionSheetContent(
                visible = true,
                theme = ActionSheetTheme.GRID,
                items = listOf(
                    ActionSheetItem(label = "微信", icon = Icons.chatCircle, badge = "NEW"),
                    ActionSheetItem(label = "朋友圈", icon = Icons.shareNetwork),
                    ActionSheetItem(label = "QQ", icon = Icons.chats),
                    ActionSheetItem(label = "企业微信", icon = Icons.usersThree),
                    ActionSheetItem(label = "收藏", icon = Icons.star, showRedPoint = true),
                    ActionSheetItem(label = "刷新", icon = Icons.arrowClockwise),
                    ActionSheetItem(label = "下载", icon = Icons.downloadSimple, badge = "8"),
                    ActionSheetItem(label = "复制", icon = Icons.copy)
                ),
                onSelected = { item, _ ->
                    Toast.show("选中了：${item.label}")
                    showBadgeGrid = false
                },
                onCancel = { showBadgeGrid = false },
                onDismiss = { showBadgeGrid = false }
            )
        }

        // List option states
        if (showStateList) {
            ActionSheetContent(
                visible = true,
                items = listOf(
                    ActionSheetItem(label = "默认选项"),
                    ActionSheetItem(label = "自定义选项", textColor = colors.primarySoftForeground),
                    ActionSheetItem(label = "失效选项", disabled = true),
                    ActionSheetItem(label = "警告选项", textColor = colors.destructive)
                ),
                onSelected = { item, _ ->
                    Toast.show("选中了：${item.label}")
                    showStateList = false
                },
                onCancel = { showStateList = false },
                onDismiss = { showStateList = false }
            )
        }

        // List option states with icons
        if (showIconStateList) {
            ActionSheetContent(
                visible = true,
                items = listOf(
                    ActionSheetItem(label = "默认选项", icon = Icons.gear),
                    ActionSheetItem(label = "自定义选项", icon = Icons.gear, textColor = colors.primarySoftForeground),
                    ActionSheetItem(label = "失效选项", icon = Icons.gear, disabled = true),
                    ActionSheetItem(label = "警告选项", icon = Icons.gear, textColor = colors.destructive)
                ),
                onSelected = { item, _ ->
                    Toast.show("选中了：${item.label}")
                    showIconStateList = false
                },
                onCancel = { showIconStateList = false },
                onDismiss = { showIconStateList = false }
            )
        }

        // Centred list with badges
        if (showCenterBadgeList) {
            ActionSheetContent(
                visible = true,
                description = "动作面板描述文字",
                align = ActionSheetAlign.CENTER,
                items = listOf(
                    ActionSheetItem(label = "选项一", showRedPoint = true),
                    ActionSheetItem(label = "选项二", badge = "8"),
                    ActionSheetItem(label = "选项三", badge = "99")
                ),
                onSelected = { item, _ ->
                    Toast.show("选中了：${item.label}")
                    showCenterBadgeList = false
                },
                onCancel = { showCenterBadgeList = false },
                onDismiss = { showCenterBadgeList = false }
            )
        }

        // Centred list with icons
        if (showCenterIconList) {
            ActionSheetContent(
                visible = true,
                description = "动作面板描述文字",
                align = ActionSheetAlign.CENTER,
                items = listOf(
                    ActionSheetItem(label = "选项一", icon = Icons.gear),
                    ActionSheetItem(label = "选项二", icon = Icons.gear),
                    ActionSheetItem(label = "选项三", icon = Icons.gear),
                    ActionSheetItem(label = "选项四", icon = Icons.gear)
                ),
                onSelected = { item, _ ->
                    Toast.show("选中了：${item.label}")
                    showCenterIconList = false
                },
                onCancel = { showCenterIconList = false },
                onDismiss = { showCenterIconList = false }
            )
        }

        // Leading-aligned list with badges
        if (showLeftBadgeList) {
            ActionSheetContent(
                visible = true,
                description = "动作面板描述文字",
                align = ActionSheetAlign.LEFT,
                items = listOf(
                    ActionSheetItem(label = "选项一", showRedPoint = true),
                    ActionSheetItem(label = "选项二", showRedPoint = true),
                    ActionSheetItem(label = "选项三", showRedPoint = true),
                    ActionSheetItem(label = "选项四", showRedPoint = true)
                ),
                onSelected = { item, _ ->
                    Toast.show("选中了：${item.label}")
                    showLeftBadgeList = false
                },
                onCancel = { showLeftBadgeList = false },
                onDismiss = { showLeftBadgeList = false }
            )
        }

        // Leading-aligned list with icons
        if (showLeftIconList) {
            ActionSheetContent(
                visible = true,
                description = "动作面板描述文字",
                align = ActionSheetAlign.LEFT,
                items = listOf(
                    ActionSheetItem(label = "选项一", icon = Icons.gear),
                    ActionSheetItem(label = "选项二", icon = Icons.gear),
                    ActionSheetItem(label = "选项三", icon = Icons.gear),
                    ActionSheetItem(label = "选项四", icon = Icons.gear)
                ),
                onSelected = { item, _ ->
                    Toast.show("选中了：${item.label}")
                    showLeftIconList = false
                },
                onCancel = { showLeftIconList = false },
                onDismiss = { showLeftIconList = false }
            )
        }
    }
}

// One arrow row per trigger (non-anchored overlay entries, see COMPONENT_SPEC §6).
@Composable
private fun SheetRows(rows: List<SheetRow>) {
    CellGroup(items = rows) { row ->
        Cell(
            title = row.title,
            description = row.description,
            arrow = true,
            onClick = row.onClick
        )
    }
}
