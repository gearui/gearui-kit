package com.gearui.sample.examples.drawer

import com.gearui.components.icon.Icons
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.button.ButtonType
import com.gearui.components.drawer.Drawer
import com.gearui.components.drawer.DrawerItem
import com.gearui.components.drawer.DrawerPlacement
import com.gearui.components.toast.Toast
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.typography.IconSizes
import com.gearui.theme.Theme

private class DrawerRow(val title: String, val description: String? = null, val onClick: () -> Unit)

/**
 * Drawer component examples
 */
@Composable
fun DrawerExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // Builds the menu items
    val menuItems = List(15) { index ->
        DrawerItem(title = "菜单${index + 1}")
    }

    // Menu items with icons
    val icons = listOf(
        Icons.house, Icons.user, Icons.gear, Icons.phone, Icons.chat_circle,
        Icons.bell, Icons.clock, Icons.copy, Icons.magnifying_glass,
        Icons.heart, Icons.star, Icons.pencil_simple, Icons.image, Icons.camera,
        Icons.play_fill,
    )
    val iconMenuItems = List(15) { index ->
        DrawerItem(
            title = "菜单${index + 1}",
            icon = {
                Icon(
                    name = icons[index % icons.size],
                    size = IconSizes.Default.xl,
                    tint = colors.foreground
                )
            }
        )
    }

    // State of each example
    var showBaseDrawer by remember { mutableStateOf(false) }
    var showIconDrawer by remember { mutableStateOf(false) }
    var showTitleDrawer by remember { mutableStateOf(false) }
    var showFooterDrawer by remember { mutableStateOf(false) }
    var showCustomDrawer by remember { mutableStateOf(false) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ==================== Component types ====================

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "抽屉类型",
            description = "从右侧滑出的菜单抽屉"
        ) {
            DrawerRows(
                listOf(
                    DrawerRow("基础抽屉", "仅含文本菜单项") { showBaseDrawer = true },
                    DrawerRow("带图标抽屉", "每个菜单项带有图标") { showIconDrawer = true },
                )
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

        // ==================== Component styles ====================

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "抽屉样式",
            description = "标题、底部操作与自定义底色"
        ) {
            DrawerRows(
                listOf(
                    DrawerRow("带标题抽屉", "从左侧滑出，顶部显示 title") { showTitleDrawer = true },
                    DrawerRow("带底部操作抽屉", "footer 插槽放置底部操作按钮") { showFooterDrawer = true },
                    DrawerRow("自定义背景色抽屉", "backgroundColor 自定义抽屉底色") { showCustomDrawer = true },
                )
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

            Drawer(
                visible = showCustomDrawer,
                onDismiss = { showCustomDrawer = false },
                placement = DrawerPlacement.RIGHT,
                title = "菜单",
                backgroundColor = colors.muted,
                items = menuItems,
                onItemClick = { index, item ->
                    Toast.show("点击了: ${item.title}")
                }
            )
        }
    }
}

// One arrow row per trigger (non-anchored overlay entries, see COMPONENT_SPEC §6).
@Composable
private fun DrawerRows(rows: List<DrawerRow>) {
    CellGroup(items = rows) { row ->
        Cell(
            title = row.title,
            description = row.description,
            arrow = true,
            onClick = row.onClick
        )
    }
}
