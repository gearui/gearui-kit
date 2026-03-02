package com.gearui.sample.examples.drawer

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.drawer.Drawer
import com.gearui.components.drawer.DrawerItem
import com.gearui.components.drawer.DrawerPlacement
import com.gearui.components.drawer.DrawerWithHeader
import com.gearui.components.toast.Toast
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Drawer 组件示例
 *
 */
@Composable
fun DrawerExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 生成菜单项
    val menuItems = List(15) { index ->
        DrawerItem(title = "菜单${index + 1}")
    }

    // 带图标的菜单项
    val icons = listOf("🏠", "👤", "⚙️", "📱", "💬", "🔔", "📊", "📁", "🔍", "❤️", "⭐", "📝", "🎵", "📷", "🎮")
    val iconMenuItems = List(15) { index ->
        DrawerItem(
            title = "菜单${index + 1}",
            icon = {
                Text(
                    text = icons[index % icons.size],
                    style = Typography.TitleMedium
                )
            }
        )
    }

    // 各示例的状态
    var showBaseDrawer by remember { mutableStateOf(false) }
    var showIconDrawer by remember { mutableStateOf(false) }
    var showTitleDrawer by remember { mutableStateOf(false) }
    var showFooterDrawer by remember { mutableStateOf(false) }
    var showCustomDrawer by remember { mutableStateOf(false) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ==================== 组件类型 ====================

        // 基础抽屉
        ExampleSection(
            title = "基础抽屉",
            description = "最基本的抽屉，仅包含文本菜单项"
        ) {
            Button(
                text = "基础抽屉",
                onClick = { showBaseDrawer = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                theme = ButtonTheme.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )

            Drawer(
                visible = showBaseDrawer,
                onDismiss = { showBaseDrawer = false },
                placement = DrawerPlacement.RIGHT,
                items = menuItems,
                onItemClick = { index, item ->
                    Toast.show("点击了: ${item.title}")
                }
            )
        }

        // 带图标抽屉
        ExampleSection(
            title = "带图标抽屉",
            description = "每个菜单项带有图标"
        ) {
            Button(
                text = "带图标抽屉",
                onClick = { showIconDrawer = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                theme = ButtonTheme.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )

            Drawer(
                visible = showIconDrawer,
                onDismiss = { showIconDrawer = false },
                placement = DrawerPlacement.RIGHT,
                items = iconMenuItems,
                onItemClick = { index, item ->
                    Toast.show("点击了: ${item.title}")
                }
            )
        }

        // ==================== 组件样式 ====================

        // 带标题抽屉
        ExampleSection(
            title = "带标题抽屉",
            description = "抽屉顶部显示标题"
        ) {
            Button(
                text = "带标题抽屉",
                onClick = { showTitleDrawer = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                theme = ButtonTheme.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )

            Drawer(
                visible = showTitleDrawer,
                onDismiss = { showTitleDrawer = false },
                placement = DrawerPlacement.LEFT,
                title = "菜单",
                items = menuItems,
                onItemClick = { index, item ->
                    Toast.show("点击了: ${item.title}")
                }
            )
        }

        // 带底部操作抽屉
        ExampleSection(
            title = "带底部操作抽屉",
            description = "抽屉底部显示操作按钮"
        ) {
            Button(
                text = "带底部操作抽屉",
                onClick = { showFooterDrawer = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                theme = ButtonTheme.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )

            Drawer(
                visible = showFooterDrawer,
                onDismiss = { showFooterDrawer = false },
                placement = DrawerPlacement.LEFT,
                title = "菜单",
                items = menuItems,
                footer = {
                    Button(
                        text = "操作",
                        onClick = {
                            Toast.show("点击了操作按钮")
                            showFooterDrawer = false
                        },
                        size = ButtonSize.LARGE,
                        type = ButtonType.OUTLINE,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                onItemClick = { index, item ->
                    Toast.show("点击了: ${item.title}")
                }
            )
        }

        // 自定义样式抽屉
        ExampleSection(
            title = "自定义背景色抽屉",
            description = "可自定义抽屉的背景颜色"
        ) {
            Button(
                text = "自定义背景色抽屉",
                onClick = { showCustomDrawer = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                theme = ButtonTheme.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )

            Drawer(
                visible = showCustomDrawer,
                onDismiss = { showCustomDrawer = false },
                placement = DrawerPlacement.RIGHT,
                title = "菜单",
                backgroundColor = colors.surfaceVariant,
                items = menuItems,
                onItemClick = { index, item ->
                    Toast.show("点击了: ${item.title}")
                }
            )
        }

        // ==================== 使用说明 ====================

        ExampleSection(
            title = "API 说明",
            description = "Drawer 组件的主要属性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "placement: 抽屉方向 (LEFT / RIGHT)",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "title: 抽屉标题",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "items: 列表项集合 (List<DrawerItem>)",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "footer: 底部内容插槽",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "content: 完全自定义内容",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "showOverlay: 是否显示遮罩层",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "closeOnOverlayClick: 点击遮罩是否关闭",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "width: 抽屉宽度 (默认 280.dp)",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "backgroundColor: 背景颜色",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "bordered: 是否显示分割线",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "onItemClick: 列表项点击回调",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }

        // DrawerItem 说明
        ExampleSection(
            title = "DrawerItem 说明",
            description = "列表项数据结构"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "title: 菜单项标题",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "icon: 菜单项图标 (Composable)",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "content: 完全自定义内容 (Composable)",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
