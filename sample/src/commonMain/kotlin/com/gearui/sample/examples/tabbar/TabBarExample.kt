package com.gearui.sample.examples.tabbar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.tabbar.*
import com.gearui.components.toast.Toast
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * TabBar 组件示例
 *
 * 用于在不同功能模块之间进行快速切换，位于页面底部。
 */
@Composable
fun TabBarExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ==================== 组件类型 ====================

        // 纯文本标签栏 - 2个标签
        ExampleSection(
            title = "纯文本标签栏",
            description = "基础的文字标签导航"
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.TEXT,
                tabs = listOf(
                    TabConfig(text = "标签1", onTap = { Toast.show("点击了标签1") }),
                    TabConfig(text = "标签2", onTap = { Toast.show("点击了标签2") })
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // 纯文本标签栏 - 3个标签
        ExampleSection(
            title = "",
            description = ""
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.TEXT,
                tabs = listOf(
                    TabConfig(text = "标签1"),
                    TabConfig(text = "标签2"),
                    TabConfig(text = "标签3")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // 纯文本标签栏 - 4个标签
        ExampleSection(
            title = "",
            description = ""
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.TEXT,
                tabs = listOf(
                    TabConfig(text = "标签1"),
                    TabConfig(text = "标签2"),
                    TabConfig(text = "标签3"),
                    TabConfig(text = "标签4")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // 纯文本标签栏 - 5个标签
        ExampleSection(
            title = "",
            description = ""
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.TEXT,
                tabs = listOf(
                    TabConfig(text = "标签1"),
                    TabConfig(text = "标签2"),
                    TabConfig(text = "标签3"),
                    TabConfig(text = "标签4"),
                    TabConfig(text = "标签5")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // 图标加文本标签栏 - 2个标签
        ExampleSection(
            title = "图标加文本标签栏",
            description = "带图标的文字标签导航"
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.ICON_TEXT,
                tabs = listOf(
                    TabConfig(text = "标签1", selectedIcon = "🏠", unselectedIcon = "🏠"),
                    TabConfig(text = "标签2", selectedIcon = "👤", unselectedIcon = "👤")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // 图标加文本标签栏 - 3个标签
        ExampleSection(
            title = "",
            description = ""
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.ICON_TEXT,
                tabs = listOf(
                    TabConfig(text = "标签1", selectedIcon = "🏠", unselectedIcon = "🏠"),
                    TabConfig(text = "标签2", selectedIcon = "💬", unselectedIcon = "💬"),
                    TabConfig(text = "标签3", selectedIcon = "👤", unselectedIcon = "👤")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // 图标加文本标签栏 - 4个标签
        ExampleSection(
            title = "",
            description = ""
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.ICON_TEXT,
                tabs = listOf(
                    TabConfig(text = "标签1", selectedIcon = "🏠", unselectedIcon = "🏠"),
                    TabConfig(text = "标签2", selectedIcon = "📋", unselectedIcon = "📋"),
                    TabConfig(text = "标签3", selectedIcon = "🛒", unselectedIcon = "🛒"),
                    TabConfig(text = "标签4", selectedIcon = "👤", unselectedIcon = "👤")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // 图标加文本标签栏 - 5个标签
        ExampleSection(
            title = "",
            description = ""
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.ICON_TEXT,
                tabs = listOf(
                    TabConfig(text = "标签1", selectedIcon = "🏠", unselectedIcon = "🏠"),
                    TabConfig(text = "标签2", selectedIcon = "📋", unselectedIcon = "📋"),
                    TabConfig(text = "标签3", selectedIcon = "🔔", unselectedIcon = "🔔"),
                    TabConfig(text = "标签4", selectedIcon = "🛒", unselectedIcon = "🛒"),
                    TabConfig(text = "标签5", selectedIcon = "👤", unselectedIcon = "👤")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // 纯图标标签栏 - 2个标签
        ExampleSection(
            title = "纯图标标签栏",
            description = "仅显示图标的标签导航"
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.ICON,
                componentType = TabBarComponentType.NORMAL,
                useVerticalDivider = true,
                tabs = listOf(
                    TabConfig(selectedIcon = "🏠", unselectedIcon = "🏠"),
                    TabConfig(selectedIcon = "👤", unselectedIcon = "👤")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // 纯图标标签栏 - 3个标签
        ExampleSection(
            title = "",
            description = ""
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.ICON,
                componentType = TabBarComponentType.NORMAL,
                useVerticalDivider = true,
                tabs = listOf(
                    TabConfig(selectedIcon = "🏠", unselectedIcon = "🏠"),
                    TabConfig(selectedIcon = "💬", unselectedIcon = "💬"),
                    TabConfig(selectedIcon = "👤", unselectedIcon = "👤")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // 纯图标标签栏 - 4个标签
        ExampleSection(
            title = "",
            description = ""
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.ICON,
                componentType = TabBarComponentType.NORMAL,
                useVerticalDivider = true,
                tabs = listOf(
                    TabConfig(selectedIcon = "🏠", unselectedIcon = "🏠"),
                    TabConfig(selectedIcon = "📋", unselectedIcon = "📋"),
                    TabConfig(selectedIcon = "🛒", unselectedIcon = "🛒"),
                    TabConfig(selectedIcon = "👤", unselectedIcon = "👤")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // ==================== 组件样式 ====================

        // 弱选中标签栏 - 文本
        ExampleSection(
            title = "弱选中标签栏",
            description = "普通样式，无胶囊背景"
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.TEXT,
                componentType = TabBarComponentType.NORMAL,
                useVerticalDivider = true,
                tabs = listOf(
                    TabConfig(text = "标签", badge = null, showRedPoint = true),
                    TabConfig(text = "标签"),
                    TabConfig(text = "标签")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // 弱选中标签栏 - 图标
        ExampleSection(
            title = "",
            description = ""
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.ICON,
                componentType = TabBarComponentType.NORMAL,
                tabs = listOf(
                    TabConfig(selectedIcon = "🏠", unselectedIcon = "🏠", showRedPoint = true),
                    TabConfig(selectedIcon = "💬", unselectedIcon = "💬"),
                    TabConfig(selectedIcon = "👤", unselectedIcon = "👤")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // 弱选中标签栏 - 图标+文本
        ExampleSection(
            title = "",
            description = ""
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.ICON_TEXT,
                componentType = TabBarComponentType.NORMAL,
                tabs = listOf(
                    TabConfig(text = "标签", selectedIcon = "🏠", unselectedIcon = "🏠", showRedPoint = true),
                    TabConfig(text = "标签", selectedIcon = "💬", unselectedIcon = "💬"),
                    TabConfig(text = "标签", selectedIcon = "👤", unselectedIcon = "👤")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // 悬浮胶囊标签栏
        ExampleSection(
            title = "悬浮胶囊标签栏",
            description = "圆角胶囊外观"
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.ICON_TEXT,
                componentType = TabBarComponentType.LABEL,
                outlineType = TabBarOutlineType.CAPSULE,
                tabs = listOf(
                    TabConfig(text = "标签1", selectedIcon = "🏠", unselectedIcon = "🏠"),
                    TabConfig(text = "标签2", selectedIcon = "💬", unselectedIcon = "💬"),
                    TabConfig(text = "标签3", selectedIcon = "👤", unselectedIcon = "👤")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // ==================== 带徽标 ====================

        // 带徽标示例
        ExampleSection(
            title = "带徽标",
            description = "支持数字徽标和红点提示"
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.ICON_TEXT,
                tabs = listOf(
                    TabConfig(text = "首页", selectedIcon = "🏠", unselectedIcon = "🏠"),
                    TabConfig(text = "消息", selectedIcon = "💬", unselectedIcon = "💬", badge = "99+"),
                    TabConfig(text = "购物车", selectedIcon = "🛒", unselectedIcon = "🛒", badge = "3"),
                    TabConfig(text = "我的", selectedIcon = "👤", unselectedIcon = "👤", showRedPoint = true)
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        // ==================== 自定义颜色 ====================

        // 自定义背景颜色
        ExampleSection(
            title = "自定义颜色",
            description = "可自定义选中和未选中背景色"
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.ICON_TEXT,
                tabs = listOf(
                    TabConfig(text = "标签1", selectedIcon = "🏠", unselectedIcon = "🏠"),
                    TabConfig(text = "标签2", selectedIcon = "💬", unselectedIcon = "💬"),
                    TabConfig(text = "标签3", selectedIcon = "🛒", unselectedIcon = "🛒"),
                    TabConfig(text = "标签4", selectedIcon = "👤", unselectedIcon = "👤"),
                    TabConfig(text = "标签5", selectedIcon = "⚙️", unselectedIcon = "⚙️")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it },
                selectedBgColor = colors.dangerLight,
                unselectedBgColor = colors.surfaceVariant
            )
        }

        // 自定义整体背景
        ExampleSection(
            title = "",
            description = ""
        ) {
            var selected by remember { mutableStateOf(0) }

            TabBar(
                type = TabBarType.TEXT,
                tabs = listOf(
                    TabConfig(text = "标签1"),
                    TabConfig(text = "标签2")
                ),
                selectedIndex = selected,
                onTabSelected = { selected = it },
                backgroundColor = colors.success,
                selectedBgColor = colors.dangerLight,
                unselectedBgColor = colors.primaryLight
            )
        }
    }
}
