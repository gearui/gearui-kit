package com.gearui.sample.examples.backtop

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.backtop.BackTop
import com.gearui.components.backtop.BackTopCustom
import com.gearui.components.backtop.BackTopStyle
import com.gearui.components.backtop.BackTopTheme
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * BackTop 组件示例
 *
 */
@Composable
fun BackTopExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 各个示例的显示状态
    var showCircleLight by remember { mutableStateOf(false) }
    var showCircleDark by remember { mutableStateOf(false) }
    var showCircleWithText by remember { mutableStateOf(false) }
    var showHalfCircleLight by remember { mutableStateOf(false) }
    var showHalfCircleDark by remember { mutableStateOf(false) }
    var showCustom by remember { mutableStateOf(false) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 圆形样式 - 亮色主题
        ExampleSection(
            title = "圆形-亮色主题",
            description = "默认样式，白底黑字，48dp 圆形按钮"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        text = "显示",
                        onClick = { showCircleLight = true },
                        size = ButtonSize.SMALL
                    )
                    Button(
                        text = "隐藏",
                        onClick = { showCircleLight = false },
                        size = ButtonSize.SMALL
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(colors.surfaceVariant),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        text = "内容区域",
                        style = Typography.BodyMedium,
                        color = colors.textSecondary,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    BackTop(
                        visible = showCircleLight,
                        onClick = { showCircleLight = false },
                        style = BackTopStyle.CIRCLE,
                        theme = BackTopTheme.LIGHT
                    )
                }
            }
        }

        // 圆形样式 - 暗色主题
        ExampleSection(
            title = "圆形-暗色主题",
            description = "黑底白字，适合浅色背景"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        text = "显示",
                        onClick = { showCircleDark = true },
                        size = ButtonSize.SMALL
                    )
                    Button(
                        text = "隐藏",
                        onClick = { showCircleDark = false },
                        size = ButtonSize.SMALL
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(colors.surfaceVariant),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        text = "内容区域",
                        style = Typography.BodyMedium,
                        color = colors.textSecondary,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    BackTop(
                        visible = showCircleDark,
                        onClick = { showCircleDark = false },
                        style = BackTopStyle.CIRCLE,
                        theme = BackTopTheme.DARK
                    )
                }
            }
        }

        // 圆形样式 - 带文字
        ExampleSection(
            title = "圆形-带文字",
            description = "显示图标和文字，使用 showText = true"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        text = "显示",
                        onClick = { showCircleWithText = true },
                        size = ButtonSize.SMALL
                    )
                    Button(
                        text = "隐藏",
                        onClick = { showCircleWithText = false },
                        size = ButtonSize.SMALL
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(colors.surfaceVariant),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        text = "内容区域",
                        style = Typography.BodyMedium,
                        color = colors.textSecondary,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    BackTop(
                        visible = showCircleWithText,
                        onClick = { showCircleWithText = false },
                        style = BackTopStyle.CIRCLE,
                        theme = BackTopTheme.LIGHT,
                        showText = true
                    )
                }
            }
        }

        // 半圆形样式 - 亮色主题
        ExampleSection(
            title = "半圆形-亮色主题",
            description = "贴边显示的半圆形按钮"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        text = "显示",
                        onClick = { showHalfCircleLight = true },
                        size = ButtonSize.SMALL
                    )
                    Button(
                        text = "隐藏",
                        onClick = { showHalfCircleLight = false },
                        size = ButtonSize.SMALL
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(colors.surfaceVariant),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        text = "内容区域",
                        style = Typography.BodyMedium,
                        color = colors.textSecondary,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    BackTop(
                        visible = showHalfCircleLight,
                        onClick = { showHalfCircleLight = false },
                        style = BackTopStyle.HALF_CIRCLE,
                        theme = BackTopTheme.LIGHT
                    )
                }
            }
        }

        // 半圆形样式 - 暗色主题
        ExampleSection(
            title = "半圆形-暗色主题",
            description = "暗色主题的半圆形按钮"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        text = "显示",
                        onClick = { showHalfCircleDark = true },
                        size = ButtonSize.SMALL
                    )
                    Button(
                        text = "隐藏",
                        onClick = { showHalfCircleDark = false },
                        size = ButtonSize.SMALL
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(colors.surfaceVariant),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        text = "内容区域",
                        style = Typography.BodyMedium,
                        color = colors.textSecondary,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    BackTop(
                        visible = showHalfCircleDark,
                        onClick = { showHalfCircleDark = false },
                        style = BackTopStyle.HALF_CIRCLE,
                        theme = BackTopTheme.DARK
                    )
                }
            }
        }

        // 自定义内容
        ExampleSection(
            title = "自定义内容",
            description = "使用 BackTopCustom 完全自定义按钮内容"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        text = "显示",
                        onClick = { showCustom = true },
                        size = ButtonSize.SMALL
                    )
                    Button(
                        text = "隐藏",
                        onClick = { showCustom = false },
                        size = ButtonSize.SMALL
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(colors.surfaceVariant),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        text = "内容区域",
                        style = Typography.BodyMedium,
                        color = colors.textSecondary,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    BackTopCustom(
                        visible = showCustom,
                        onClick = { showCustom = false },
                        theme = BackTopTheme.LIGHT
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "回到",
                                style = Typography.BodyExtraSmall,
                                color = colors.primary
                            )
                            Text(
                                text = "顶部",
                                style = Typography.BodyExtraSmall,
                                color = colors.primary
                            )
                        }
                    }
                }
            }
        }

        // API 说明
        ExampleSection(
            title = "API 说明",
            description = "BackTop 组件参数"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "style: 样式类型",
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )
                Text(
                    text = "  - CIRCLE: 圆形 (48dp)",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
                Text(
                    text = "  - HALF_CIRCLE: 半圆形 (贴边)",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "theme: 主题颜色",
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )
                Text(
                    text = "  - LIGHT: 亮色 (白底黑字)",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
                Text(
                    text = "  - DARK: 暗色 (黑底白字)",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "showText: 是否显示文字 (默认 false)",
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "offset: 位置偏移 (right, bottom)",
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )
            }
        }
    }
}
