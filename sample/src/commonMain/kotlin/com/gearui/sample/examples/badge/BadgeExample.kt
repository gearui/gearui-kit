package com.gearui.sample.examples.badge

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.Dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonShape
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.icon.Icons
import com.gearui.foundation.avatar.AvatarSizeTokens
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.components.avatar.Avatar
import com.gearui.primitives.Badge
import com.gearui.primitives.BadgeBorder
import com.gearui.primitives.BadgeSize
import com.gearui.primitives.BadgeTheme
import com.gearui.primitives.BadgeType
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.theme.Theme

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
    var messageCount by remember { mutableStateOf(8) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "红点徽标",
            description = "RedPoint：只提示有新内容，不显示数量"
        ) {
            BadgeRow {
                Badge(type = BadgeType.RedPoint) {
                    Text(text = "消息", style = Theme.typography.bodyLarge, color = colors.foreground)
                }
                Badge(type = BadgeType.RedPoint) { BellIcon() }
                Badge(type = BadgeType.RedPoint) {
                    Button(text = "按钮", size = ButtonSize.SMALL, onClick = {})
                }
            }
        }

        ExampleSection(
            title = "数字徽标",
            description = "Message：显示数量，超过 maxCount 显示 max+"
        ) {
            BadgeRow {
                Badge(type = BadgeType.Message, count = messageCount) {
                    Text(text = "消息", style = Theme.typography.bodyLarge, color = colors.foreground)
                }
                Badge(type = BadgeType.Message, count = 16) { BellIcon() }
                Badge(type = BadgeType.Message, count = 128, maxCount = 99) { BellIcon() }
                Badge(type = BadgeType.Message, count = messageCount) {
                    Button(text = "按钮", size = ButtonSize.SMALL, onClick = {})
                }
            }
        }

        ExampleSection(
            title = "零值",
            description = "showZero = true 显示 0，false 时隐藏"
        ) {
            BadgeRow {
                Labeled("显示 0") {
                    Badge(type = BadgeType.Message, count = 0, showZero = true) { Anchor() }
                }
                Labeled("隐藏 0") {
                    Badge(type = BadgeType.Message, count = 0, showZero = false) { Anchor() }
                }
            }
        }

        ExampleSection(
            title = "方形与气泡",
            description = "Square 的两种圆角，Bubble 的文字气泡"
        ) {
            BadgeRow {
                Labeled("大圆角") {
                    Badge(type = BadgeType.Square, count = messageCount, border = BadgeBorder.Large) { Anchor() }
                }
                Labeled("小圆角") {
                    Badge(type = BadgeType.Square, count = messageCount, border = BadgeBorder.Small) { Anchor() }
                }
            }
            BadgeRow {
                Badge(type = BadgeType.Bubble, message = "领积分") { Anchor() }
                Badge(type = BadgeType.Bubble, message = "NEW", theme = BadgeTheme.Primary) { Anchor() }
                Badge(type = BadgeType.Bubble, message = "HOT", theme = BadgeTheme.Warning) { Anchor() }
            }
        }

        ExampleSection(
            title = "角标",
            description = "Subscript：列表行右上角的斜角标签",
            surface = SectionSurface.Plain
        ) {
            val rows = listOf(
                Triple("单行标题", null, "NEW" to BadgeTheme.Error),
                Triple("单行标题", "带描述的列表项", "HOT" to BadgeTheme.Warning),
            )
            CellGroup(items = rows) { (title, description, badge) ->
                Box {
                    Cell(title = title, description = description, arrow = true, onClick = {})
                    Badge(
                        modifier = Modifier.align(Alignment.TopEnd).padding(end = Spacing.xxxl),
                        type = BadgeType.Subscript,
                        message = badge.first,
                        theme = badge.second
                    )
                }
            }
        }

        ExampleSection(
            title = "尺寸",
            description = "BadgeSize.Large 与 BadgeSize.Small（默认）"
        ) {
            BadgeRow {
                Labeled("Large") {
                    Badge(type = BadgeType.Message, count = messageCount, size = BadgeSize.Large) {
                        Anchor(size = AvatarSizeTokens.Large.size)
                    }
                }
                Labeled("Small") {
                    Badge(type = BadgeType.Message, count = messageCount, size = BadgeSize.Small) {
                        Anchor(size = AvatarSizeTokens.Medium.size)
                    }
                }
                Labeled("方形 · 大") {
                    Badge(
                        type = BadgeType.Square,
                        count = 8888,
                        maxCount = 9000,
                        size = BadgeSize.Large
                    ) { Anchor(size = AvatarSizeTokens.Medium.size) }
                }
            }
        }

        ExampleSection(
            title = "颜色主题",
            description = "Error / Primary / Success / Warning / Neutral"
        ) {
            BadgeRow {
                listOf(
                    BadgeTheme.Error,
                    BadgeTheme.Primary,
                    BadgeTheme.Success,
                    BadgeTheme.Warning,
                    BadgeTheme.Neutral,
                ).forEach { theme ->
                    Badge(type = BadgeType.Message, count = 8, theme = theme) { BellIcon() }
                }
            }
        }

        ExampleSection(
            title = "独立徽标",
            description = "不传 content 时单独显示"
        ) {
            BadgeRow {
                Badge(type = BadgeType.Message, count = 1)
                Badge(type = BadgeType.Message, count = 12, theme = BadgeTheme.Primary)
                Badge(type = BadgeType.Square, count = 99, theme = BadgeTheme.Success)
                Badge(type = BadgeType.Message, count = 100, maxCount = 99)
                Badge(type = BadgeType.RedPoint)
                Badge(type = BadgeType.Bubble, message = "气泡", theme = BadgeTheme.Warning)
            }
        }

        ExampleSection(
            title = "动态计数",
            description = "点击按钮改变徽标数字"
        ) {
            BadgeRow {
                Badge(type = BadgeType.Message, count = messageCount, maxCount = 99) { Anchor() }
                Button(
                    onClick = { if (messageCount > 0) messageCount-- },
                    size = ButtonSize.SMALL,
                    theme = ButtonTheme.LIGHT,
                    shape = ButtonShape.SQUARE,
                    icon = Icons.minus,
                    contentDescription = "减少"
                )
                Text(text = "$messageCount", style = Theme.typography.titleMedium, color = colors.foreground)
                Button(
                    onClick = { messageCount++ },
                    size = ButtonSize.SMALL,
                    shape = ButtonShape.SQUARE,
                    icon = Icons.plus,
                    contentDescription = "增加"
                )
            }
        }
    }
}

@Composable
private fun BadgeRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xl),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

/** A badged element with a caption under it. */
@Composable
private fun Labeled(label: String, content: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        content()
        Text(text = label, style = Theme.typography.bodySmall, color = Theme.colors.mutedForeground)
    }
}

/** A small anchor for inline badges. */
@Composable
private fun BellIcon() {
    Icon(name = Icons.bell, size = IconSizes.Default.xl, tint = Theme.colors.foreground)
}

/** A larger anchor: a real Avatar, as badges usually sit on one. */
@Composable
private fun Anchor(size: Dp = AvatarSizeTokens.Medium.size) {
    Avatar(fallback = "张", size = size)
}
