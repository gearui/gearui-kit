package com.gearui.sample.examples.sidebar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.Spacing

/**
 * Sidebar 侧边栏组件示例 - 主入口页面
 *
 * 用于内容分类后的展示切换。
 */
@Composable
fun SidebarExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    // 子页面导航状态
    var currentSubPage by remember { mutableStateOf<SidebarSubPage?>(null) }

    // 如果有子页面，显示子页面
    currentSubPage?.let { subPage ->
        when (subPage) {
            SidebarSubPage.ANCHOR -> SidebarAnchorPage(onBack = { currentSubPage = null })
            SidebarSubPage.PAGINATION -> SidebarPaginationPage(onBack = { currentSubPage = null })
            SidebarSubPage.ICON -> SidebarIconPage(onBack = { currentSubPage = null })
            SidebarSubPage.OUTLINE -> SidebarOutlinePage(onBack = { currentSubPage = null })
            SidebarSubPage.CUSTOM -> SidebarCustomPage(onBack = { currentSubPage = null })
            SidebarSubPage.LOADING -> SidebarLoadingPage(onBack = { currentSubPage = null })
            SidebarSubPage.UNSELECTED_COLOR -> SidebarUnselectedColorPage(onBack = { currentSubPage = null })
        }
        return
    }

    // 主入口页面
    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 组件类型
        ExampleSection(
            title = "组件类型",
            description = "侧边导航用法"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp)) {
                Button(
                    text = "锚点用法",
                    onClick = { currentSubPage = SidebarSubPage.ANCHOR },
                    type = ButtonType.OUTLINE,
                    theme = ButtonTheme.PRIMARY,
                    size = ButtonSize.LARGE,
                    block = true
                )

                Button(
                    text = "切页用法",
                    onClick = { currentSubPage = SidebarSubPage.PAGINATION },
                    type = ButtonType.OUTLINE,
                    theme = ButtonTheme.PRIMARY,
                    size = ButtonSize.LARGE,
                    block = true
                )
            }
        }

        ExampleSection(
            title = "",
            description = "图标侧边导航"
        ) {
            Button(
                text = "带图标侧边导航",
                onClick = { currentSubPage = SidebarSubPage.ICON },
                type = ButtonType.OUTLINE,
                theme = ButtonTheme.PRIMARY,
                size = ButtonSize.LARGE,
                block = true
            )
        }

        // 组件样式
        ExampleSection(
            title = "组件样式",
            description = "侧边导航样式"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp)) {
                Button(
                    text = "非通栏选项样式",
                    onClick = { currentSubPage = SidebarSubPage.OUTLINE },
                    type = ButtonType.OUTLINE,
                    theme = ButtonTheme.PRIMARY,
                    size = ButtonSize.LARGE,
                    block = true
                )

                Button(
                    text = "自定义样式",
                    onClick = { currentSubPage = SidebarSubPage.CUSTOM },
                    type = ButtonType.OUTLINE,
                    theme = ButtonTheme.PRIMARY,
                    size = ButtonSize.LARGE,
                    block = true
                )
            }
        }

        // 高级用法
        ExampleSection(
            title = "高级用法",
            description = "延迟加载与自定义颜色"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp)) {
                Button(
                    text = "延迟加载",
                    onClick = { currentSubPage = SidebarSubPage.LOADING },
                    type = ButtonType.OUTLINE,
                    theme = ButtonTheme.PRIMARY,
                    size = ButtonSize.LARGE,
                    block = true
                )

                Button(
                    text = "未选中颜色自定义",
                    onClick = { currentSubPage = SidebarSubPage.UNSELECTED_COLOR },
                    type = ButtonType.OUTLINE,
                    theme = ButtonTheme.PRIMARY,
                    size = ButtonSize.LARGE,
                    block = true
                )
            }
        }
    }
}

/**
 * Sidebar 子页面枚举
 */
enum class SidebarSubPage {
    ANCHOR,           // 锚点用法
    PAGINATION,       // 切页用法
    ICON,             // 带图标
    OUTLINE,          // 非通栏选项样式
    CUSTOM,           // 自定义样式
    LOADING,          // 延迟加载
    UNSELECTED_COLOR  // 未选中颜色自定义
}
