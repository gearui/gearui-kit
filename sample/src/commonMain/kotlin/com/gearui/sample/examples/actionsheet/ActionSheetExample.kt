package com.gearui.sample.examples.actionsheet

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.actionsheet.*
import com.gearui.components.button.*
import com.gearui.components.toast.Toast
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * ActionSheet component examples
 *
 * A modal panel raised by a user action, offering two or more options relevant to the current context.
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
                title = "列表型动作面板",
                description = "常规列表、带描述、带图标、带徽标"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Plain list
                    Button(
                        text = "常规列表",
                        onClick = { showBasicList = true },
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE,
                        block = true
                    )

                    // List with descriptions
                    Button(
                        text = "带描述列表",
                        onClick = { showDescList = true },
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE,
                        block = true
                    )

                    // List with icons
                    Button(
                        text = "带图标列表",
                        onClick = { showIconList = true },
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE,
                        block = true
                    )

                    // List with badges
                    Button(
                        text = "带徽标列表",
                        onClick = { showBadgeList = true },
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE,
                        block = true
                    )

                    // List with Cell descriptions
                    Button(
                        text = "带Cell描述列表",
                        onClick = { showItemDescList = true },
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE,
                        block = true
                    )
                }
            }

            // Grid-style action sheet
            ExampleSection(
                title = "宫格型动作面板",
                description = "常规宫格、带描述、带徽标"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Plain grid
                    Button(
                        text = "常规宫格",
                        onClick = { showBasicGrid = true },
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE,
                        block = true
                    )

                    // Grid with descriptions
                    Button(
                        text = "带描述宫格",
                        onClick = { showDescGrid = true },
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE,
                        block = true
                    )

                    // Grid with badges
                    Button(
                        text = "带徽标宫格",
                        onClick = { showBadgeGrid = true },
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE,
                        block = true
                    )
                }
            }

            // ==================== Component states ====================

            // List option states
            ExampleSection(
                title = "列表型选项状态",
                description = "默认、自定义颜色、禁用、警告"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // List option states
                    Button(
                        text = "列表型选项状态",
                        onClick = { showStateList = true },
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE,
                        block = true
                    )

                    // List option states with icons
                    Button(
                        text = "列表型带图标状态",
                        onClick = { showIconStateList = true },
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE,
                        block = true
                    )
                }
            }

            // ==================== Component styles ====================

            // List alignment
            ExampleSection(
                title = "列表型对齐方式",
                description = "居中对齐、左对齐"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Centred list with badges
                    Button(
                        text = "居中带徽标列表",
                        onClick = { showCenterBadgeList = true },
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE,
                        block = true
                    )

                    // Centred list with icons
                    Button(
                        text = "居中带图标列表",
                        onClick = { showCenterIconList = true },
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE,
                        block = true
                    )

                    // Leading-aligned list with badges
                    Button(
                        text = "左对齐带徽标列表",
                        onClick = { showLeftBadgeList = true },
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE,
                        block = true
                    )

                    // Leading-aligned list with icons
                    Button(
                        text = "左对齐带图标列表",
                        onClick = { showLeftIconList = true },
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE,
                        block = true
                    )
                }
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
                    ActionSheetItem(label = "选项一", icon = "📱"),
                    ActionSheetItem(label = "选项二", icon = "📱"),
                    ActionSheetItem(label = "选项三", icon = "📱"),
                    ActionSheetItem(label = "选项四", icon = "📱")
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
                    ActionSheetItem(label = "微信", icon = "💬"),
                    ActionSheetItem(label = "朋友圈", icon = "🌐"),
                    ActionSheetItem(label = "QQ", icon = "🐧"),
                    ActionSheetItem(label = "企业微信", icon = "💼"),
                    ActionSheetItem(label = "收藏", icon = "⭐"),
                    ActionSheetItem(label = "刷新", icon = "🔄"),
                    ActionSheetItem(label = "下载", icon = "📥"),
                    ActionSheetItem(label = "复制", icon = "📋")
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
                    ActionSheetItem(label = "微信", icon = "💬"),
                    ActionSheetItem(label = "朋友圈", icon = "🌐"),
                    ActionSheetItem(label = "QQ", icon = "🐧"),
                    ActionSheetItem(label = "企业微信", icon = "💼"),
                    ActionSheetItem(label = "收藏", icon = "⭐"),
                    ActionSheetItem(label = "刷新", icon = "🔄"),
                    ActionSheetItem(label = "下载", icon = "📥"),
                    ActionSheetItem(label = "复制", icon = "📋")
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
                    ActionSheetItem(label = "微信", icon = "💬", badge = "NEW"),
                    ActionSheetItem(label = "朋友圈", icon = "🌐"),
                    ActionSheetItem(label = "QQ", icon = "🐧"),
                    ActionSheetItem(label = "企业微信", icon = "💼"),
                    ActionSheetItem(label = "收藏", icon = "⭐", showRedPoint = true),
                    ActionSheetItem(label = "刷新", icon = "🔄"),
                    ActionSheetItem(label = "下载", icon = "📥", badge = "8"),
                    ActionSheetItem(label = "复制", icon = "📋")
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
                    ActionSheetItem(label = "自定义选项", textColor = colors.primary),
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
                    ActionSheetItem(label = "默认选项", icon = "📱"),
                    ActionSheetItem(label = "自定义选项", icon = "📱", textColor = colors.primary),
                    ActionSheetItem(label = "失效选项", icon = "📱", disabled = true),
                    ActionSheetItem(label = "警告选项", icon = "📱", textColor = colors.destructive)
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
                    ActionSheetItem(label = "选项一", icon = "📱"),
                    ActionSheetItem(label = "选项二", icon = "📱"),
                    ActionSheetItem(label = "选项三", icon = "📱"),
                    ActionSheetItem(label = "选项四", icon = "📱")
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
                    ActionSheetItem(label = "选项一", icon = "📱"),
                    ActionSheetItem(label = "选项二", icon = "📱"),
                    ActionSheetItem(label = "选项三", icon = "📱"),
                    ActionSheetItem(label = "选项四", icon = "📱")
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
