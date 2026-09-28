package com.gearui.sample.examples.popover

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonType
import com.gearui.components.popover.LocalPopoverTextColor
import com.gearui.components.popover.Popover
import com.gearui.components.popover.PopoverMenu
import com.gearui.components.popover.PopoverMenuItem
import com.gearui.components.popover.PopoverPlacement
import com.gearui.components.popover.PopoverTheme
import com.gearui.components.popover.rememberPopoverState
import com.gearui.foundation.layout.Spacing
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme

// Demo content dimension: the width of the custom-content bubble.
private val CustomContentWidth = 200.dp

/**
 * Popover component examples.
 */
@Composable
fun PopoverExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    val customPopoverState = rememberPopoverState()
    val menuState = rememberPopoverState()
    var menuResult by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "箭头",
            description = "showArrow 控制气泡是否带箭头"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                TextPopover(label = "带箭头", message = "这是带箭头的气泡", showArrow = true)
                TextPopover(label = "不带箭头", message = "这是不带箭头的气泡", showArrow = false)
            }
        }

        ExampleSection(
            title = "主题风格",
            description = "DARK / LIGHT / BRAND / SUCCESS / WARNING / ERROR"
        ) {
            val themes = listOf(
                Triple(PopoverTheme.DARK, "深色", "深色主题"),
                Triple(PopoverTheme.LIGHT, "浅色", "浅色主题"),
                Triple(PopoverTheme.BRAND, "品牌色", "品牌色主题"),
                Triple(PopoverTheme.SUCCESS, "成功", "成功主题"),
                Triple(PopoverTheme.WARNING, "警告", "警告主题"),
                Triple(PopoverTheme.ERROR, "错误", "错误主题"),
            )
            // Anchored triggers stay at the Button's default width
            themes.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    row.forEach { (theme, label, message) ->
                        TextPopover(label = label, message = message, theme = theme)
                    }
                }
            }
        }

        ExampleSection(
            title = "弹出位置",
            description = "TOP / BOTTOM / LEFT / RIGHT 四个方向，共 12 种 placement"
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextPopover(label = "顶部", message = "顶部弹出的气泡", placement = PopoverPlacement.TOP)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.huge),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextPopover(label = "左侧", message = "左侧气泡", placement = PopoverPlacement.LEFT)
                    TextPopover(label = "右侧", message = "右侧气泡", placement = PopoverPlacement.RIGHT)
                }
                TextPopover(label = "底部", message = "底部弹出的气泡", placement = PopoverPlacement.BOTTOM)
            }
        }

        ExampleSection(
            title = "自定义内容",
            description = "content 可放置任意组件"
        ) {
            Popover(
                state = customPopoverState,
                placement = PopoverPlacement.BOTTOM,
                theme = PopoverTheme.LIGHT,
                content = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                        modifier = Modifier.width(CustomContentWidth)
                    ) {
                        Text(
                            text = "自定义气泡内容",
                            style = Theme.typography.titleSmall,
                            color = colors.foreground
                        )
                        Text(
                            text = "这里可以放置任意自定义内容，包括图片、按钮、列表等各种组件。",
                            style = Theme.typography.bodySmall,
                            color = colors.mutedForeground
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            Button(
                                text = "取消",
                                onClick = { customPopoverState.hide() },
                                size = ButtonSize.EXTRA_SMALL,
                                type = ButtonType.OUTLINE
                            )
                            Button(
                                text = "确定",
                                onClick = { customPopoverState.hide() },
                                size = ButtonSize.EXTRA_SMALL
                            )
                        }
                    }
                }
            ) { onClick ->
                Button(text = "自定义内容", onClick = onClick)
            }
        }

        ExampleSection(
            title = "菜单式气泡",
            description = "PopoverMenu：菜单项支持禁用与危险操作"
        ) {
            PopoverMenu(
                state = menuState,
                placement = PopoverPlacement.BOTTOM,
                theme = PopoverTheme.LIGHT,
                items = listOf(
                    PopoverMenuItem(label = "编辑", onClick = { menuResult = "点击了编辑" }),
                    PopoverMenuItem(label = "复制", onClick = { menuResult = "点击了复制" }),
                    PopoverMenuItem(label = "分享", onClick = { menuResult = "点击了分享" }),
                    PopoverMenuItem(label = "禁用项", disabled = true, onClick = { }),
                    PopoverMenuItem(label = "删除", danger = true, onClick = { menuResult = "点击了删除" })
                )
            ) { onClick ->
                Button(text = "显示菜单", onClick = onClick)
            }

            if (menuResult.isNotEmpty()) {
                Text(
                    text = "操作结果：$menuResult",
                    style = Theme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
        }
    }
}

/** A Button that opens a Popover holding a single line of text. */
@Composable
private fun TextPopover(
    label: String,
    message: String,
    placement: PopoverPlacement = PopoverPlacement.BOTTOM,
    theme: PopoverTheme = PopoverTheme.LIGHT,
    showArrow: Boolean = false
) {
    val state = rememberPopoverState()
    Popover(
        state = state,
        placement = placement,
        theme = theme,
        showArrow = showArrow,
        content = {
            Text(
                text = message,
                style = Theme.typography.bodyMedium,
                color = LocalPopoverTextColor.current
            )
        }
    ) { onClick ->
        Button(
            text = label,
            onClick = onClick
        )
    }
}
