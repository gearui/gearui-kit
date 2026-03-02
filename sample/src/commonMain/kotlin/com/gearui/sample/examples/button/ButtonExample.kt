package com.gearui.sample.examples.button

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.*
import com.gearui.components.icon.Icons
import com.gearui.components.toast.Toast
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/**
 * Button 组件示例
 *
 * 用于开启一个闭环的操作任务，如"删除"对象、"购买"商品等。
 */
@Composable
fun ButtonExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ==================== 组件类型 ====================

        // 基础按钮
        ExampleSection(
            title = "基础按钮",
            description = "填充、浅色填充、默认、描边、文字按钮"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(start = 16.dp)
            ) {
                // 填充按钮 - Primary
                Button(
                    text = "填充按钮",
                    onClick = { Toast.show("点击了按钮") },
                    theme = ButtonTheme.PRIMARY,
                    type = ButtonType.FILL
                )
                // 填充按钮 - Light
                Button(
                    text = "填充按钮",
                    onClick = { Toast.show("点击了按钮") },
                    theme = ButtonTheme.LIGHT,
                    type = ButtonType.FILL
                )
                // 填充按钮 - Default
                Button(
                    text = "填充按钮",
                    onClick = { Toast.show("点击了按钮") },
                    theme = ButtonTheme.DEFAULT,
                    type = ButtonType.FILL
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(start = 16.dp)
            ) {
                // 描边按钮
                Button(
                    text = "描边按钮",
                    onClick = { Toast.show("点击了按钮") },
                    theme = ButtonTheme.PRIMARY,
                    type = ButtonType.OUTLINE
                )
                // 文字按钮
                Button(
                    text = "文字按钮",
                    onClick = { Toast.show("点击了按钮") },
                    theme = ButtonTheme.PRIMARY,
                    type = ButtonType.TEXT
                )
            }
        }

        // 图标按钮
        ExampleSection(
            title = "图标按钮",
            description = "带图标的按钮、纯图标按钮、加载按钮"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(start = 16.dp)
            ) {
                // 带图标的填充按钮
                Button(
                    text = "填充按钮",
                    onClick = { Toast.show("点击了按钮") },
                    theme = ButtonTheme.PRIMARY,
                    icon = Icons.call
                )
                // 纯图标方形按钮
                Button(
                    text = "",
                    onClick = { Toast.show("点击了按钮") },
                    theme = ButtonTheme.PRIMARY,
                    shape = ButtonShape.SQUARE,
                    icon = Icons.call
                )
                // 加载中按钮
                Button(
                    text = "加载中",
                    onClick = {},
                    theme = ButtonTheme.PRIMARY,
                    loading = true
                )
            }
        }

        // 幽灵按钮
        ExampleSection(
            title = "幽灵按钮",
            description = "透明背景的按钮，用于深色背景"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.textPrimary)
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        text = "幽灵按钮",
                        onClick = { Toast.show("点击了按钮") },
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.GHOST
                    )
                    Button(
                        text = "幽灵按钮",
                        onClick = { Toast.show("点击了按钮") },
                        theme = ButtonTheme.DANGER,
                        type = ButtonType.GHOST
                    )
                    Button(
                        text = "幽灵按钮",
                        onClick = { Toast.show("点击了按钮") },
                        theme = ButtonTheme.DEFAULT,
                        type = ButtonType.GHOST
                    )
                }
            }
        }

        // 组合按钮
        ExampleSection(
            title = "组合按钮",
            description = "多个按钮并排使用"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    text = "填充按钮",
                    onClick = { Toast.show("点击了按钮") },
                    theme = ButtonTheme.LIGHT,
                    modifier = Modifier.weight(1f),
                    block = true
                )
                Button(
                    text = "填充按钮",
                    onClick = { Toast.show("点击了按钮") },
                    theme = ButtonTheme.PRIMARY,
                    modifier = Modifier.weight(1f),
                    block = true
                )
            }
        }

        // 通栏按钮
        ExampleSection(
            title = "通栏按钮",
            description = "占据整行宽度的按钮"
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Button(
                    text = "填充按钮",
                    onClick = { Toast.show("点击了按钮") },
                    theme = ButtonTheme.PRIMARY,
                    block = true,
                    icon = Icons.send
                )
            }
        }

        // ==================== 组件状态 ====================

        // 按钮禁用状态
        ExampleSection(
            title = "按钮禁用状态",
            description = "不可点击的按钮"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Button(
                        text = "填充按钮",
                        onClick = {},
                        theme = ButtonTheme.PRIMARY,
                        disabled = true
                    )
                    Button(
                        text = "填充按钮",
                        onClick = {},
                        theme = ButtonTheme.LIGHT,
                        disabled = true
                    )
                    Button(
                        text = "填充按钮",
                        onClick = {},
                        theme = ButtonTheme.DEFAULT,
                        disabled = true
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Button(
                        text = "描边按钮",
                        onClick = {},
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE,
                        disabled = true
                    )
                    Button(
                        text = "文字按钮",
                        onClick = {},
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.TEXT,
                        disabled = true
                    )
                }
            }
        }

        // ==================== 组件主题 ====================

        // 按钮尺寸
        ExampleSection(
            title = "按钮尺寸",
            description = "Large(48)、Medium(40)、Small(32)、ExtraSmall(28)"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Button(
                    text = "按钮48",
                    onClick = { Toast.show("Large") },
                    theme = ButtonTheme.PRIMARY,
                    size = ButtonSize.LARGE
                )
                Button(
                    text = "按钮40",
                    onClick = { Toast.show("Medium") },
                    theme = ButtonTheme.PRIMARY,
                    size = ButtonSize.MEDIUM
                )
                Button(
                    text = "按钮32",
                    onClick = { Toast.show("Small") },
                    theme = ButtonTheme.PRIMARY,
                    size = ButtonSize.SMALL
                )
                Button(
                    text = "按钮28",
                    onClick = { Toast.show("ExtraSmall") },
                    theme = ButtonTheme.PRIMARY,
                    size = ButtonSize.EXTRA_SMALL
                )
            }
        }

        // 按钮形状
        ExampleSection(
            title = "按钮形状",
            description = "矩形、方形、圆角、圆形、胶囊"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                // 矩形
                Button(
                    text = "填充按钮",
                    onClick = { Toast.show("Rectangle") },
                    theme = ButtonTheme.PRIMARY,
                    shape = ButtonShape.RECTANGLE
                )
                // 方形
                Button(
                    text = "",
                    onClick = { Toast.show("Square") },
                    theme = ButtonTheme.PRIMARY,
                    shape = ButtonShape.SQUARE,
                    icon = Icons.star
                )
                // 圆角
                Button(
                    text = "填充按钮",
                    onClick = { Toast.show("Round") },
                    theme = ButtonTheme.PRIMARY,
                    shape = ButtonShape.ROUND
                )
                // 圆形
                Button(
                    text = "",
                    onClick = { Toast.show("Circle") },
                    theme = ButtonTheme.PRIMARY,
                    shape = ButtonShape.CIRCLE,
                    icon = Icons.star
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                // 胶囊/填充圆角
                Button(
                    text = "填充按钮",
                    onClick = { Toast.show("Filled") },
                    theme = ButtonTheme.PRIMARY,
                    shape = ButtonShape.FILLED
                )
            }
        }

        // 按钮主题
        ExampleSection(
            title = "按钮主题",
            description = "Default、Primary、Danger、Light 主题"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Default 主题
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Button(
                        text = "填充按钮",
                        onClick = {},
                        theme = ButtonTheme.DEFAULT,
                        type = ButtonType.FILL
                    )
                    Button(
                        text = "描边按钮",
                        onClick = {},
                        theme = ButtonTheme.DEFAULT,
                        type = ButtonType.OUTLINE
                    )
                    Button(
                        text = "文字按钮",
                        onClick = {},
                        theme = ButtonTheme.DEFAULT,
                        type = ButtonType.TEXT
                    )
                }

                // Primary 主题
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Button(
                        text = "填充按钮",
                        onClick = {},
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.FILL
                    )
                    Button(
                        text = "描边按钮",
                        onClick = {},
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.OUTLINE
                    )
                    Button(
                        text = "文字按钮",
                        onClick = {},
                        theme = ButtonTheme.PRIMARY,
                        type = ButtonType.TEXT
                    )
                }

                // Danger 主题
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Button(
                        text = "填充按钮",
                        onClick = {},
                        theme = ButtonTheme.DANGER,
                        type = ButtonType.FILL
                    )
                    Button(
                        text = "描边按钮",
                        onClick = {},
                        theme = ButtonTheme.DANGER,
                        type = ButtonType.OUTLINE
                    )
                    Button(
                        text = "文字按钮",
                        onClick = {},
                        theme = ButtonTheme.DANGER,
                        type = ButtonType.TEXT
                    )
                }

                // Light 主题
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Button(
                        text = "填充按钮",
                        onClick = {},
                        theme = ButtonTheme.LIGHT,
                        type = ButtonType.FILL
                    )
                    Button(
                        text = "描边按钮",
                        onClick = {},
                        theme = ButtonTheme.LIGHT,
                        type = ButtonType.OUTLINE
                    )
                    Button(
                        text = "文字按钮",
                        onClick = {},
                        theme = ButtonTheme.LIGHT,
                        type = ButtonType.TEXT
                    )
                }
            }
        }
    }
}
