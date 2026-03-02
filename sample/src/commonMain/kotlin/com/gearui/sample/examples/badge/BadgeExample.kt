package com.gearui.sample.examples.badge

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.primitives.Badge
import com.gearui.primitives.BadgeType
import com.gearui.primitives.BadgeTheme
import com.gearui.primitives.BadgeSize
import com.gearui.primitives.BadgeBorder
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.cell.Cell
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.gearui.Spacing

/**
 * Badge 徽标组件示例
 *
 * 用于告知用户，该区域的状态变化或者待处理任务的数量。
 */
@Composable
fun BadgeExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 动态计数
    var messageCount by remember { mutableStateOf(8) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ========== 组件类型 ==========

        // 红点徽标
        ExampleSection(
            title = "红点徽标",
            description = "用于消息提醒，无具体数值"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // 文字 + 红点
                Badge(
                    type = BadgeType.RedPoint,
                    theme = BadgeTheme.Error
                ) {
                    Text(
                        text = "消息",
                        style = Typography.BodyLarge,
                        color = colors.textPrimary
                    )
                }

                // 图标 + 红点
                Badge(
                    type = BadgeType.RedPoint,
                    theme = BadgeTheme.Error
                ) {
                    IconBox()
                }

                // 按钮 + 红点
                Badge(
                    type = BadgeType.RedPoint,
                    theme = BadgeTheme.Error
                ) {
                    Button(
                        text = "按钮",
                        size = ButtonSize.SMALL,
                        onClick = {}
                    )
                }
            }
        }

        // 数字徽标
        ExampleSection(
            title = "数字徽标",
            description = "显示具体消息数量"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // 文字 + 数字
                Badge(
                    type = BadgeType.Message,
                    count = messageCount,
                    theme = BadgeTheme.Error
                ) {
                    Text(
                        text = "消息",
                        style = Typography.BodyLarge,
                        color = colors.textPrimary
                    )
                }

                // 图标 + 数字
                Badge(
                    type = BadgeType.Message,
                    count = messageCount,
                    theme = BadgeTheme.Error
                ) {
                    IconBox()
                }

                // 按钮 + 数字
                Badge(
                    type = BadgeType.Message,
                    count = messageCount,
                    theme = BadgeTheme.Error
                ) {
                    Button(
                        text = "按钮",
                        size = ButtonSize.SMALL,
                        onClick = {}
                    )
                }
            }
        }

        // 自定义徽标
        ExampleSection(
            title = "自定义徽标",
            description = "自定义显示内容和样式"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 显示数字
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Badge(
                        type = BadgeType.Message,
                        count = messageCount,
                        theme = BadgeTheme.Error
                    ) {
                        LargeIconBox()
                    }
                    Text(
                        text = "数字$messageCount",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                // 显示数字0
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Badge(
                        type = BadgeType.Message,
                        count = 0,
                        theme = BadgeTheme.Error,
                        showZero = true
                    ) {
                        LargeIconBox()
                    }
                    Text(
                        text = "显示0",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                // 不显示数字0
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Badge(
                        type = BadgeType.Message,
                        count = 0,
                        theme = BadgeTheme.Error,
                        showZero = false
                    ) {
                        LargeIconBox()
                    }
                    Text(
                        text = "隐藏0",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // ========== 组件样式 ==========

        // 圆形徽标（Message 类型）
        ExampleSection(
            title = "圆形徽标",
            description = "Message 类型，默认圆形样式"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Badge(
                    type = BadgeType.Message,
                    count = 1,
                    theme = BadgeTheme.Error
                ) {
                    IconBox()
                }
                Badge(
                    type = BadgeType.Message,
                    count = 16,
                    theme = BadgeTheme.Error
                ) {
                    IconBox()
                }
                Badge(
                    type = BadgeType.Message,
                    count = 128,
                    maxCount = 99,
                    theme = BadgeTheme.Error
                ) {
                    IconBox()
                }
            }
        }

        // 方形徽标（Square 类型）
        ExampleSection(
            title = "方形徽标",
            description = "Square 类型，带圆角"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 大圆角
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Badge(
                        type = BadgeType.Square,
                        count = messageCount,
                        border = BadgeBorder.Large,
                        theme = BadgeTheme.Error
                    ) {
                        IconBox()
                    }
                    Text(
                        text = "大圆角",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                // 小圆角
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Badge(
                        type = BadgeType.Square,
                        count = messageCount,
                        border = BadgeBorder.Small,
                        theme = BadgeTheme.Error
                    ) {
                        IconBox()
                    }
                    Text(
                        text = "小圆角",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // 气泡徽标（Bubble 类型）
        ExampleSection(
            title = "气泡徽标",
            description = "Bubble 类型，左下角小尖角"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 领积分
                Badge(
                    type = BadgeType.Bubble,
                    message = "领积分",
                    theme = BadgeTheme.Error
                ) {
                    LargeIconBox(showShopIcon = true)
                }

                // NEW
                Badge(
                    type = BadgeType.Bubble,
                    message = "NEW",
                    theme = BadgeTheme.Primary
                ) {
                    LargeIconBox()
                }

                // HOT
                Badge(
                    type = BadgeType.Bubble,
                    message = "HOT",
                    theme = BadgeTheme.Warning
                ) {
                    LargeIconBox()
                }
            }
        }

        // 角标（Subscript 类型）
        ExampleSection(
            title = "角标",
            description = "Subscript 类型，用于列表项"
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surface)
            ) {
                Box {
                    Cell(
                        title = "单行标题",
                        arrow = true,
                        onClick = {}
                    )
                    // 角标
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 40.dp)
                    ) {
                        Badge(
                            type = BadgeType.Subscript,
                            message = "NEW",
                            theme = BadgeTheme.Error
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.spacer16.dp)
                        .height(1.dp)
                        .background(colors.divider)
                )

                Box {
                    Cell(
                        title = "单行标题",
                        description = "带描述的列表项",
                        arrow = true,
                        onClick = {}
                    )
                    // 角标
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 40.dp)
                    ) {
                        Badge(
                            type = BadgeType.Subscript,
                            message = "HOT",
                            theme = BadgeTheme.Warning
                        )
                    }
                }
            }
        }

        // ========== 组件尺寸 ==========

        // 尺寸对比
        ExampleSection(
            title = "组件尺寸",
            description = "Large / Small 两种尺寸"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Large
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Badge(
                        type = BadgeType.Message,
                        count = messageCount,
                        size = BadgeSize.Large,
                        theme = BadgeTheme.Error
                    ) {
                        AvatarBox(size = 56)
                    }
                    Text(
                        text = "Large",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                // Small (默认)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Badge(
                        type = BadgeType.Message,
                        count = messageCount,
                        size = BadgeSize.Small,
                        theme = BadgeTheme.Error
                    ) {
                        AvatarBox(size = 48)
                    }
                    Text(
                        text = "Small",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                // Red Point
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Badge(
                        type = BadgeType.RedPoint,
                        theme = BadgeTheme.Error
                    ) {
                        AvatarBox(size = 40)
                    }
                    Text(
                        text = "RedPoint",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // ========== 徽标颜色 ==========

        // 徽标主题色
        ExampleSection(
            title = "徽标颜色",
            description = "不同语义的徽标颜色主题"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Error - 红色
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Badge(
                        type = BadgeType.Message,
                        count = 8,
                        theme = BadgeTheme.Error
                    ) {
                        IconBox()
                    }
                    Text(
                        text = "Error",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                // Primary - 主题色
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Badge(
                        type = BadgeType.Message,
                        count = 8,
                        theme = BadgeTheme.Primary
                    ) {
                        IconBox()
                    }
                    Text(
                        text = "Primary",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                // Success - 绿色
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Badge(
                        type = BadgeType.Message,
                        count = 8,
                        theme = BadgeTheme.Success
                    ) {
                        IconBox()
                    }
                    Text(
                        text = "Success",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                // Warning - 橙色
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Badge(
                        type = BadgeType.Message,
                        count = 8,
                        theme = BadgeTheme.Warning
                    ) {
                        IconBox()
                    }
                    Text(
                        text = "Warning",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                // Neutral - 灰色
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Badge(
                        type = BadgeType.Message,
                        count = 8,
                        theme = BadgeTheme.Neutral
                    ) {
                        IconBox()
                    }
                    Text(
                        text = "Neutral",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // ========== 数量上限 ==========

        // 数量超限显示
        ExampleSection(
            title = "数量上限",
            description = "超过最大数量显示 max+"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 未超限
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Badge(
                        type = BadgeType.Message,
                        count = 50,
                        maxCount = 99,
                        theme = BadgeTheme.Error
                    ) {
                        IconBox()
                    }
                    Text(
                        text = "50",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                // 刚好99
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Badge(
                        type = BadgeType.Message,
                        count = 99,
                        maxCount = 99,
                        theme = BadgeTheme.Error
                    ) {
                        IconBox()
                    }
                    Text(
                        text = "99",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                // 超过99
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Badge(
                        type = BadgeType.Message,
                        count = 100,
                        maxCount = 99,
                        theme = BadgeTheme.Error
                    ) {
                        IconBox()
                    }
                    Text(
                        text = "99+",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                // 自定义上限999
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Badge(
                        type = BadgeType.Square,
                        count = 8888,
                        maxCount = 9000,
                        size = BadgeSize.Large,
                        border = BadgeBorder.Large,
                        theme = BadgeTheme.Error
                    ) {
                        IconBox()
                    }
                    Text(
                        text = "8888",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // ========== 独立徽标 ==========

        // 独立使用
        ExampleSection(
            title = "独立徽标",
            description = "不依附于其他元素的徽标"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Badge(type = BadgeType.Message, count = 1, theme = BadgeTheme.Error)
                Badge(type = BadgeType.Message, count = 12, theme = BadgeTheme.Primary)
                Badge(type = BadgeType.Square, count = 99, theme = BadgeTheme.Success)
                Badge(type = BadgeType.Message, count = 100, maxCount = 99, theme = BadgeTheme.Error)
                Badge(type = BadgeType.RedPoint, theme = BadgeTheme.Error)
                Badge(type = BadgeType.Bubble, message = "气泡", theme = BadgeTheme.Warning)
            }
        }

        // ========== 交互演示 ==========

        // 动态计数
        ExampleSection(
            title = "动态计数",
            description = "点击按钮改变徽标数字"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 徽标显示
                Badge(
                    type = BadgeType.Message,
                    count = messageCount,
                    maxCount = 99,
                    theme = BadgeTheme.Error
                ) {
                    LargeIconBox()
                }

                // 操作按钮
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        text = "-",
                        size = ButtonSize.SMALL,
                        onClick = {
                            if (messageCount > 0) messageCount--
                        }
                    )
                    Text(
                        text = "$messageCount",
                        style = Typography.TitleMedium,
                        color = colors.textPrimary
                    )
                    Button(
                        text = "+",
                        size = ButtonSize.SMALL,
                        theme = ButtonTheme.PRIMARY,
                        onClick = {
                            messageCount++
                        }
                    )
                }
            }
        }
    }
}

/**
 * 小图标盒子
 */
@Composable
private fun IconBox() {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(colors.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🔔",
            style = Typography.BodySmall
        )
    }
}

/**
 * 大图标盒子
 */
@Composable
private fun LargeIconBox(showShopIcon: Boolean = false) {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (showShopIcon) "🛒" else "🔔",
            style = Typography.TitleMedium
        )
    }
}

/**
 * 头像盒子
 */
@Composable
private fun AvatarBox(size: Int) {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(colors.primary.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "👤",
            style = if (size >= 48) Typography.TitleLarge else Typography.TitleMedium
        )
    }
}
