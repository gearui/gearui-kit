package com.gearui.sample.examples.navbar

import com.gearui.components.icon.*
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.typography.IconSizes
import com.gearui.components.icon.Icons
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.navbar.NavBar
import com.gearui.components.navbar.NavBarItem
import com.gearui.components.searchbar.SearchBar
import com.gearui.components.searchbar.SearchBarCancel
import com.gearui.foundation.layout.Spacing
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.theme.Theme

/** Taller bar for the large-title demo: the height is what is being shown. */
private val LargeTitleBarHeight = 56.dp

/**
 * NavBar component examples
 *
 * Used to move between pages. Sits above the content area and below the system status bar.
 */
@Composable
fun NavbarExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    var keyword by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ==================== Component types ====================

        // Basic navigation bar
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "基础导航栏",
            description = "标题加默认返回按钮"
        ) {
            NavBar(
                title = "标题文字",
                useDefaultBack = true,
                onBackClick = { Toast.show("返回") }
            )
        }

        // With a trailing action button
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "超长标题",
            description = "标题必须单行省略，不能在固定高度的导航栏里换行"
        ) {
            NavBar(
                title = "这是一个非常非常长的页面标题用来验证截断行为",
                useDefaultBack = true,
                onBackClick = { Toast.show("返回") }
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "带右侧操作按钮",
            description = "支持右侧添加图标按钮"
        ) {
            NavBar(
                title = "标题文字",
                useDefaultBack = true,
                onBackClick = { Toast.show("返回") },
                rightItems = listOf(
                    NavBarItem(
                        icon = Icons.dotsThree,
                        onClick = { Toast.show("更多") },
                        contentDescription = "更多"
                    )
                )
            )
        }

        // With a leading close button
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "带左侧关闭按钮",
            description = "可自定义左侧按钮图标"
        ) {
            NavBar(
                title = "标题文字",
                useDefaultBack = true,
                onBackClick = { Toast.show("返回") },
                leftItems = listOf(
                    NavBarItem(
                        icon = Icons.x,
                        onClick = { Toast.show("关闭") },
                        contentDescription = "关闭"
                    )
                )
            )
        }

        // Several action buttons
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "多操作按钮",
            description = "支持左右两侧添加多个图标按钮"
        ) {
            NavBar(
                title = "标题文字",
                useDefaultBack = true,
                onBackClick = { Toast.show("返回") },
                rightItems = listOf(
                    NavBarItem(
                        icon = Icons.house,
                        onClick = { Toast.show("主页") },
                        contentDescription = "主页"
                    ),
                    NavBarItem(
                        icon = Icons.dotsThree,
                        onClick = { Toast.show("更多") },
                        contentDescription = "更多"
                    )
                )
            )
        }

        // With a search field
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "带搜索导航栏",
            description = "集成搜索组件的导航栏"
        ) {
            NavBar(
                useDefaultBack = true,
                onBackClick = { Toast.show("返回") },
                titleWidget = {
                    SearchBar(
                        value = keyword,
                        onValueChange = { keyword = it },
                        placeholder = "搜索",
                        cancel = SearchBarCancel.Never,
                        onSearch = { Toast.show("搜索:$it") }
                    )
                }
            )
        }

        // With an image
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "带图片导航栏",
            description = "使用图片或Logo作为标题"
        ) {
            NavBar(
                useDefaultBack = true,
                onBackClick = { Toast.show("返回") },
                titleWidget = {
                    // A brand mark: icon plus product name.
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(Icons.star.filled, size = IconSizes.Default.xl, tint = colors.primary)
                        Text(text = "GearUI", style = Theme.typography.titleMedium, color = colors.foreground)
                    }
                }
            )
        }

        // ==================== Component styles ====================

        // Title alignment
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "标题对齐",
            description = "centerTitle 控制标题居中(默认)或居左"
        ) {
            NavBar(
                title = "标题居中",
                centerTitle = true,
                useDefaultBack = true,
                onBackClick = { Toast.show("返回") },
                rightItems = listOf(
                    NavBarItem(
                        icon = Icons.dotsThree,
                        onClick = { Toast.show("更多") },
                        contentDescription = "更多"
                    )
                )
            )
            NavBar(
                title = "标题居左",
                centerTitle = false,
                useDefaultBack = true,
                onBackClick = { Toast.show("返回") },
                rightItems = listOf(
                    NavBarItem(
                        icon = Icons.dotsThree,
                        onClick = { Toast.show("更多") },
                        contentDescription = "更多"
                    )
                )
            )
        }

        // Large title
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "大标题尺寸",
            description = "belowTitleWidget 在导航栏下方放一行大标题"
        ) {
            NavBar(
                title = "标题文字",
                height = LargeTitleBarHeight,
                useDefaultBack = true,
                onBackClick = { Toast.show("返回") },
                rightItems = listOf(
                    NavBarItem(
                        icon = Icons.dotsThree,
                        onClick = { Toast.show("更多") },
                        contentDescription = "更多"
                    )
                ),
                belowTitleWidget = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                    ) {
                        Text(
                            text = "大标题文字",
                            style = Theme.typography.headlineSmall,
                            color = colors.foreground
                        )
                    }
                }
            )
        }

        // Custom colours
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "自定义颜色",
            description = "支持自定义背景色、文字色"
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Dark background
                NavBar(
                    title = "深色背景",
                    backgroundColor = colors.primary,
                    titleColor = colors.primaryForeground,
                    useDefaultBack = true,
                    onBackClick = { Toast.show("返回") },
                    rightItems = listOf(
                        NavBarItem(
                            icon = Icons.dotsThree,
                            iconColor = colors.primaryForeground,
                            onClick = { Toast.show("更多") },
                            contentDescription = "更多"
                        )
                    )
                )

                // Red background
                NavBar(
                    title = "红色背景",
                    backgroundColor = colors.destructive,
                    titleColor = colors.primaryForeground,
                    useDefaultBack = true,
                    onBackClick = { Toast.show("返回") },
                    rightItems = listOf(
                        NavBarItem(
                            icon = Icons.dotsThree,
                            iconColor = colors.primaryForeground,
                            onClick = { Toast.show("更多") },
                            contentDescription = "更多"
                        )
                    )
                )

                // Muted background
                NavBar(
                    title = "淡色背景",
                    backgroundColor = colors.muted,
                    useDefaultBack = true,
                    onBackClick = { Toast.show("返回") },
                    rightItems = listOf(
                        NavBarItem(
                            icon = Icons.dotsThree,
                            onClick = { Toast.show("更多") },
                            contentDescription = "更多"
                        )
                    )
                )
            }
        }

        // Without a back button
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "无返回按钮",
            description = "不显示左侧返回按钮"
        ) {
            NavBar(
                title = "首页",
                useDefaultBack = false,
                rightItems = listOf(
                    NavBarItem(
                        icon = Icons.list,
                        onClick = { Toast.show("菜单") },
                        contentDescription = "菜单"
                    )
                )
            )
        }
    }
}
