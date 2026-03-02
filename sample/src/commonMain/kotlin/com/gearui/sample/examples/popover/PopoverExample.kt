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
import com.gearui.components.popover.Tooltip
import com.gearui.components.popover.rememberPopoverState
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Popover 组件示例
 */
@Composable
fun PopoverExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 基础 Popover 状态
    val basicPopoverState = rememberPopoverState()
    val noArrowPopoverState = rememberPopoverState()

    // 不同主题的 Popover 状态
    val darkPopoverState = rememberPopoverState()
    val lightPopoverState = rememberPopoverState()
    val brandPopoverState = rememberPopoverState()
    val successPopoverState = rememberPopoverState()
    val warningPopoverState = rememberPopoverState()
    val errorPopoverState = rememberPopoverState()

    // 位置演示
    val topPopoverState = rememberPopoverState()
    val bottomPopoverState = rememberPopoverState()
    val leftPopoverState = rememberPopoverState()
    val rightPopoverState = rememberPopoverState()

    // 自定义内容
    val customPopoverState = rememberPopoverState()

    // PopoverMenu 菜单
    val menuState = rememberPopoverState()
    var menuResult by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 带箭头/不带箭头
        ExampleSection(
            title = "带箭头/不带箭头",
            description = "控制气泡是否显示箭头指示器"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Popover(
                    state = basicPopoverState,
                    placement = PopoverPlacement.BOTTOM,
                    showArrow = true,
                    content = {
                        Text(
                            text = "这是带箭头的气泡",
                            style = Typography.BodyMedium,
                            color = LocalPopoverTextColor.current
                        )
                    }
                ) { onClick ->
                    Button(
                        text = "带箭头",
                        onClick = onClick,
                        size = ButtonSize.MEDIUM
                    )
                }

                Popover(
                    state = noArrowPopoverState,
                    placement = PopoverPlacement.BOTTOM,
                    showArrow = false,
                    content = {
                        Text(
                            text = "这是不带箭头的气泡",
                            style = Typography.BodyMedium,
                            color = LocalPopoverTextColor.current
                        )
                    }
                ) { onClick ->
                    Button(
                        text = "不带箭头",
                        onClick = onClick,
                        size = ButtonSize.MEDIUM,
                        type = ButtonType.OUTLINE
                    )
                }
            }
        }

        // 主题风格
        ExampleSection(
            title = "主题风格",
            description = "支持深色、浅色、品牌色、成功、警告、错误六种主题"
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 第一行：深色、浅色、品牌色
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        Popover(
                            state = darkPopoverState,
                            placement = PopoverPlacement.BOTTOM,
                            theme = PopoverTheme.DARK,
                            content = {
                                Text(
                                    text = "深色主题",
                                    style = Typography.BodySmall,
                                    color = LocalPopoverTextColor.current
                                )
                            }
                        ) { onClick ->
                            Button(
                                text = "深色",
                                onClick = onClick,
                                size = ButtonSize.SMALL,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        Popover(
                            state = lightPopoverState,
                            placement = PopoverPlacement.BOTTOM,
                            theme = PopoverTheme.LIGHT,
                            content = {
                                Text(
                                    text = "浅色主题",
                                    style = Typography.BodySmall,
                                    color = LocalPopoverTextColor.current
                                )
                            }
                        ) { onClick ->
                            Button(
                                text = "浅色",
                                onClick = onClick,
                                size = ButtonSize.SMALL,
                                type = ButtonType.OUTLINE,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        Popover(
                            state = brandPopoverState,
                            placement = PopoverPlacement.BOTTOM,
                            theme = PopoverTheme.BRAND,
                            content = {
                                Text(
                                    text = "品牌色主题",
                                    style = Typography.BodySmall,
                                    color = LocalPopoverTextColor.current
                                )
                            }
                        ) { onClick ->
                            Button(
                                text = "品牌色",
                                onClick = onClick,
                                size = ButtonSize.SMALL,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // 第二行：成功、警告、错误
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        Popover(
                            state = successPopoverState,
                            placement = PopoverPlacement.BOTTOM,
                            theme = PopoverTheme.SUCCESS,
                            content = {
                                Text(
                                    text = "成功主题",
                                    style = Typography.BodySmall,
                                    color = LocalPopoverTextColor.current
                                )
                            }
                        ) { onClick ->
                            Button(
                                text = "成功",
                                onClick = onClick,
                                size = ButtonSize.SMALL,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        Popover(
                            state = warningPopoverState,
                            placement = PopoverPlacement.BOTTOM,
                            theme = PopoverTheme.WARNING,
                            content = {
                                Text(
                                    text = "警告主题",
                                    style = Typography.BodySmall,
                                    color = LocalPopoverTextColor.current
                                )
                            }
                        ) { onClick ->
                            Button(
                                text = "警告",
                                onClick = onClick,
                                size = ButtonSize.SMALL,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        Popover(
                            state = errorPopoverState,
                            placement = PopoverPlacement.BOTTOM,
                            theme = PopoverTheme.ERROR,
                            content = {
                                Text(
                                    text = "错误主题",
                                    style = Typography.BodySmall,
                                    color = LocalPopoverTextColor.current
                                )
                            }
                        ) { onClick ->
                            Button(
                                text = "错误",
                                onClick = onClick,
                                size = ButtonSize.SMALL,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // 弹出位置
        ExampleSection(
            title = "弹出位置",
            description = "支持上、下、左、右四个基本方向，共12种位置"
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 顶部弹出
                Popover(
                    state = topPopoverState,
                    placement = PopoverPlacement.TOP,
                    content = {
                        Text(
                            text = "顶部弹出的气泡",
                            style = Typography.BodyMedium,
                            color = LocalPopoverTextColor.current
                        )
                    }
                ) { onClick ->
                    Button(
                        text = "顶部弹出",
                        onClick = onClick,
                        size = ButtonSize.MEDIUM
                    )
                }

                // 左右弹出
                Row(
                    horizontalArrangement = Arrangement.spacedBy(48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Popover(
                        state = leftPopoverState,
                        placement = PopoverPlacement.LEFT,
                        content = {
                            Text(
                                text = "左侧气泡",
                                style = Typography.BodySmall,
                                color = LocalPopoverTextColor.current
                            )
                        }
                    ) { onClick ->
                        Button(
                            text = "左侧",
                            onClick = onClick,
                            size = ButtonSize.SMALL
                        )
                    }

                    Popover(
                        state = rightPopoverState,
                        placement = PopoverPlacement.RIGHT,
                        content = {
                            Text(
                                text = "右侧气泡",
                                style = Typography.BodySmall,
                                color = LocalPopoverTextColor.current
                            )
                        }
                    ) { onClick ->
                        Button(
                            text = "右侧",
                            onClick = onClick,
                            size = ButtonSize.SMALL
                        )
                    }
                }

                // 底部弹出
                Popover(
                    state = bottomPopoverState,
                    placement = PopoverPlacement.BOTTOM,
                    content = {
                        Text(
                            text = "底部弹出的气泡",
                            style = Typography.BodyMedium,
                            color = LocalPopoverTextColor.current
                        )
                    }
                ) { onClick ->
                    Button(
                        text = "底部弹出",
                        onClick = onClick,
                        size = ButtonSize.MEDIUM
                    )
                }
            }
        }

        // 自定义内容
        ExampleSection(
            title = "自定义内容",
            description = "气泡内容支持完全自定义"
        ) {
            Popover(
                state = customPopoverState,
                placement = PopoverPlacement.BOTTOM,
                theme = PopoverTheme.LIGHT,
                content = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text(
                            text = "自定义气泡内容",
                            style = Typography.TitleSmall,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "这里可以放置任意自定义内容，包括图片、按钮、列表等各种组件。",
                            style = Typography.BodySmall,
                            color = colors.textSecondary
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
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
                Button(
                    text = "自定义内容",
                    onClick = onClick,
                    size = ButtonSize.MEDIUM
                )
            }
        }

        // PopoverMenu 菜单
        ExampleSection(
            title = "菜单式气泡",
            description = "带菜单项的气泡弹出，支持图标、禁用、危险操作"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PopoverMenu(
                    state = menuState,
                    placement = PopoverPlacement.BOTTOM,
                    theme = PopoverTheme.LIGHT,
                    items = listOf(
                        PopoverMenuItem(
                            label = "编辑",
                            onClick = { menuResult = "点击了编辑" }
                        ),
                        PopoverMenuItem(
                            label = "复制",
                            onClick = { menuResult = "点击了复制" }
                        ),
                        PopoverMenuItem(
                            label = "分享",
                            onClick = { menuResult = "点击了分享" }
                        ),
                        PopoverMenuItem(
                            label = "禁用项",
                            disabled = true,
                            onClick = { }
                        ),
                        PopoverMenuItem(
                            label = "删除",
                            danger = true,
                            onClick = { menuResult = "点击了删除" }
                        )
                    )
                ) { onClick ->
                    Button(
                        text = "显示菜单",
                        onClick = onClick,
                        size = ButtonSize.MEDIUM
                    )
                }

                if (menuResult.isNotEmpty()) {
                    Text(
                        text = "操作结果: $menuResult",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Popover 组件特性介绍"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "主题支持:",
                    style = Typography.Label,
                    color = colors.textPrimary
                )
                Text(
                    text = "DARK / LIGHT / BRAND / SUCCESS / WARNING / ERROR",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "位置支持 (12种):",
                    style = Typography.Label,
                    color = colors.textPrimary
                )
                Text(
                    text = "TOP_LEFT / TOP / TOP_RIGHT\nRIGHT_TOP / RIGHT / RIGHT_BOTTOM\nBOTTOM_RIGHT / BOTTOM / BOTTOM_LEFT\nLEFT_BOTTOM / LEFT / LEFT_TOP",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "组件类型:",
                    style = Typography.Label,
                    color = colors.textPrimary
                )
                Text(
                    text = "Popover - 基础气泡\nTooltip - 文本提示\nPopoverMenu - 菜单气泡",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }
        }
    }
}
