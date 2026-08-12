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
import com.gearui.foundation.layout.Spacing

/**
 * Badge component examples
 *
 * Tells the user about a status change in an area, or how many items are waiting.
 */
@Composable
fun BadgeExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // Dynamic count
    var messageCount by remember { mutableStateOf(8) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ========== Component types ==========

        // Dot badge
        ExampleSection(
            title = "红点徽标",
            description = "用于消息提醒，无具体数值"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Text + dot
                Badge(
                    type = BadgeType.RedPoint,
                    theme = BadgeTheme.Error
                ) {
                    Text(
                        text = "消息",
                        style = Typography.BodyLarge,
                        color = colors.foreground
                    )
                }

                // Icon + dot
                Badge(
                    type = BadgeType.RedPoint,
                    theme = BadgeTheme.Error
                ) {
                    IconBox()
                }

                // Button + dot
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

        // Number badge
        ExampleSection(
            title = "数字徽标",
            description = "显示具体消息数量"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Text + number
                Badge(
                    type = BadgeType.Message,
                    count = messageCount,
                    theme = BadgeTheme.Error
                ) {
                    Text(
                        text = "消息",
                        style = Typography.BodyLarge,
                        color = colors.foreground
                    )
                }

                // Icon + number
                Badge(
                    type = BadgeType.Message,
                    count = messageCount,
                    theme = BadgeTheme.Error
                ) {
                    IconBox()
                }

                // Button + number
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

        // Custom badge
        ExampleSection(
            title = "自定义徽标",
            description = "自定义显示内容和样式"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Showing a number
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
                        color = colors.mutedForeground
                    )
                }

                // Showing zero
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
                        color = colors.mutedForeground
                    )
                }

                // Hiding zero
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
                        color = colors.mutedForeground
                    )
                }
            }
        }

        // ========== Component styles ==========

        // Round badge (Message type)
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

        // Square badge (Square type)
        ExampleSection(
            title = "方形徽标",
            description = "Square 类型，带圆角"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Large radius
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
                        color = colors.mutedForeground
                    )
                }

                // Small radius
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
                        color = colors.mutedForeground
                    )
                }
            }
        }

        // Bubble badge (Bubble type)
        ExampleSection(
            title = "气泡徽标",
            description = "Bubble 类型，左下角小尖角"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Claim points
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

        // Corner badge (Subscript type)
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
                    // Corner badge
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
                        .padding(horizontal = Spacing.lg)
                        .height(1.dp)
                        .background(colors.border)
                )

                Box {
                    Cell(
                        title = "单行标题",
                        description = "带描述的列表项",
                        arrow = true,
                        onClick = {}
                    )
                    // Corner badge
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

        // ========== Component sizes ==========

        // Size comparison
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
                        color = colors.mutedForeground
                    )
                }

                // Small (default)
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
                        color = colors.mutedForeground
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
                        color = colors.mutedForeground
                    )
                }
            }
        }

        // ========== Badge colours ==========

        // Badge themes
        ExampleSection(
            title = "徽标颜色",
            description = "不同语义的徽标颜色主题"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Error - red
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
                        color = colors.mutedForeground
                    )
                }

                // Primary - theme colour
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
                        color = colors.mutedForeground
                    )
                }

                // Success - green
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
                        color = colors.mutedForeground
                    )
                }

                // Warning - orange
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
                        color = colors.mutedForeground
                    )
                }

                // Neutral - grey
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
                        color = colors.mutedForeground
                    )
                }
            }
        }

        // ========== Maximum count ==========

        // Overflowing the maximum
        ExampleSection(
            title = "数量上限",
            description = "超过最大数量显示 max+"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Under the limit
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
                        color = colors.mutedForeground
                    )
                }

                // Exactly 99
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
                        color = colors.mutedForeground
                    )
                }

                // Over 99
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
                        color = colors.mutedForeground
                    )
                }

                // Custom limit of 999
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
                        color = colors.mutedForeground
                    )
                }
            }
        }

        // ========== Standalone badge ==========

        // Standalone usage
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

        // ========== Interactive demo ==========

        // Dynamic count
        ExampleSection(
            title = "动态计数",
            description = "点击按钮改变徽标数字"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge display
                Badge(
                    type = BadgeType.Message,
                    count = messageCount,
                    maxCount = 99,
                    theme = BadgeTheme.Error
                ) {
                    LargeIconBox()
                }

                // Action buttons
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
                        color = colors.foreground
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
 * Small icon box
 */
@Composable
private fun IconBox() {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(colors.muted),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🔔",
            style = Typography.BodySmall
        )
    }
}

/**
 * Large icon box
 */
@Composable
private fun LargeIconBox(showShopIcon: Boolean = false) {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.muted),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (showShopIcon) "🛒" else "🔔",
            style = Typography.TitleMedium
        )
    }
}

/**
 * Avatar box
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
