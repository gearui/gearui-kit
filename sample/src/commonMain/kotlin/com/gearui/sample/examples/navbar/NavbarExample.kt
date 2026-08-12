package com.gearui.sample.examples.navbar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.navbar.NavBar
import com.gearui.components.navbar.NavBarItem
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

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

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ==================== Component types ====================

        // Basic H5 navigation bar
        ExampleSection(
            title = "基础H5导航栏",
            description = "高度48，标题字体加粗，启用默认返回按钮"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            ) {
                NavBar(
                    title = "标题文字",
                    useDefaultBack = true,
                    onBackClick = { Toast.show("返回") }
                )
            }
        }

        // With a trailing action button
        ExampleSection(
            title = "带右侧操作按钮",
            description = "支持右侧添加图标按钮"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            ) {
                NavBar(
                    title = "标题文字",
                    useDefaultBack = true,
                    onBackClick = { Toast.show("返回") },
                    rightItems = listOf(
                        NavBarItem(
                            icon = "⋯",
                            onClick = { Toast.show("更多") }
                        )
                    )
                )
            }
        }

        // With a leading close button
        ExampleSection(
            title = "带左侧关闭按钮",
            description = "可自定义左侧按钮图标"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            ) {
                NavBar(
                    title = "标题文字",
                    useDefaultBack = true,
                    onBackClick = { Toast.show("返回") },
                    leftItems = listOf(
                        NavBarItem(
                            icon = "✕",
                            onClick = { Toast.show("关闭") }
                        )
                    )
                )
            }
        }

        // Several action buttons
        ExampleSection(
            title = "多操作按钮",
            description = "支持左右两侧添加多个图标按钮"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            ) {
                NavBar(
                    title = "标题文字",
                    useDefaultBack = true,
                    onBackClick = { Toast.show("返回") },
                    rightItems = listOf(
                        NavBarItem(
                            icon = "⌂",
                            onClick = { Toast.show("主页") }
                        ),
                        NavBarItem(
                            icon = "⋯",
                            onClick = { Toast.show("更多") }
                        )
                    )
                )
            }
        }

        // With a search field
        ExampleSection(
            title = "带搜索导航栏",
            description = "集成搜索组件的导航栏"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            ) {
                NavBar(
                    useDefaultBack = true,
                    onBackClick = { Toast.show("返回") },
                    titleWidget = {
                        // Search box style
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .background(
                                    colors.muted,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "🔍 搜索",
                                style = Typography.BodyMedium,
                                color = colors.mutedForeground
                            )
                        }
                    }
                )
            }
        }

        // With an image
        ExampleSection(
            title = "带图片导航栏",
            description = "使用图片或Logo作为标题"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            ) {
                NavBar(
                    useDefaultBack = true,
                    onBackClick = { Toast.show("返回") },
                    titleWidget = {
                        // Mock logo image
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(24.dp)
                                .background(colors.primary, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "LOGO",
                                style = Typography.Label,
                                color = colors.primaryForeground
                            )
                        }
                    }
                )
            }
        }

        // ==================== Component styles ====================

        // Centred title
        ExampleSection(
            title = "标题居中",
            description = "默认标题居中显示"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            ) {
                NavBar(
                    title = "标题文字",
                    centerTitle = true,
                    useDefaultBack = true,
                    onBackClick = { Toast.show("返回") },
                    rightItems = listOf(
                        NavBarItem(
                            icon = "⋯",
                            onClick = { Toast.show("更多") }
                        )
                    )
                )
            }
        }

        // Leading title
        ExampleSection(
            title = "标题居左",
            description = "标题左对齐显示"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            ) {
                NavBar(
                    title = "标题文字",
                    centerTitle = false,
                    useDefaultBack = true,
                    onBackClick = { Toast.show("返回") },
                    rightItems = listOf(
                        NavBarItem(
                            icon = "⋯",
                            onClick = { Toast.show("更多") }
                        )
                    )
                )
            }
        }

        // Large title
        ExampleSection(
            title = "大标题尺寸",
            description = "扩展高度(104)，带副标题区域"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            ) {
                NavBar(
                    title = "标题文字",
                    height = 56.dp,
                    useDefaultBack = true,
                    onBackClick = { Toast.show("返回") },
                    rightItems = listOf(
                        NavBarItem(
                            icon = "⋯",
                            onClick = { Toast.show("更多") }
                        )
                    ),
                    belowTitleWidget = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "大标题文字",
                                style = Typography.HeadlineSmall,
                                color = colors.foreground
                            )
                        }
                    }
                )
            }
        }

        // Custom colours
        ExampleSection(
            title = "自定义颜色",
            description = "支持自定义背景色、文字色"
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                // Dark background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    NavBar(
                        title = "深色背景",
                        backgroundColor = colors.primary,
                        titleColor = colors.primaryForeground,
                        useDefaultBack = true,
                        onBackClick = { Toast.show("返回") },
                        rightItems = listOf(
                            NavBarItem(
                                icon = "⋯",
                                iconColor = colors.primaryForeground,
                                onClick = { Toast.show("更多") }
                            )
                        )
                    )
                }

                // Red background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    NavBar(
                        title = "红色背景",
                        backgroundColor = colors.destructive,
                        titleColor = colors.primaryForeground,
                        useDefaultBack = true,
                        onBackClick = { Toast.show("返回") },
                        rightItems = listOf(
                            NavBarItem(
                                icon = "⋯",
                                iconColor = colors.primaryForeground,
                                onClick = { Toast.show("更多") }
                            )
                        )
                    )
                }

                // Mock gradient - a tinted background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                ) {
                    NavBar(
                        title = "淡色背景",
                        backgroundColor = colors.muted,
                        useDefaultBack = true,
                        onBackClick = { Toast.show("返回") },
                        rightItems = listOf(
                            NavBarItem(
                                icon = "⋯",
                                onClick = { Toast.show("更多") }
                            )
                        )
                    )
                }
            }
        }

        // Without a back button
        ExampleSection(
            title = "无返回按钮",
            description = "不显示左侧返回按钮"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            ) {
                NavBar(
                    title = "首页",
                    useDefaultBack = false,
                    rightItems = listOf(
                        NavBarItem(
                            icon = "☰",
                            onClick = { Toast.show("菜单") }
                        )
                    )
                )
            }
        }
    }
}
