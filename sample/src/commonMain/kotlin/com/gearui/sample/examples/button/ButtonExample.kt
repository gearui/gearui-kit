package com.gearui.sample.examples.button

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.gearui.components.button.*
import com.gearui.components.icon.Icons
import com.gearui.components.toast.Toast
import com.gearui.foundation.layout.Spacing
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.theme.Theme

/**
 * Button component examples
 *
 * Starts a self-contained task, such as deleting an object or buying an item.
 */
@Composable
fun ButtonExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val clicked = { Toast.show("点击了按钮") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "按钮类型",
            description = "填充、描边、文字三种类型"
        ) {
            ButtonRow {
                Button(text = "填充按钮", onClick = clicked, type = ButtonType.FILL)
                Button(text = "描边按钮", onClick = clicked, type = ButtonType.OUTLINE)
                Button(text = "文字按钮", onClick = clicked, type = ButtonType.TEXT)
            }
        }

        ExampleSection(
            title = "按钮主题",
            description = "每种主题下的填充、描边、文字按钮"
        ) {
            listOf(
                ButtonTheme.PRIMARY,
                ButtonTheme.DEFAULT,
                ButtonTheme.LIGHT,
                ButtonTheme.DANGER,
                ButtonTheme.WARNING,
                ButtonTheme.SUCCESS,
            ).forEach { theme ->
                ButtonRow {
                    Button(text = "填充按钮", onClick = {}, theme = theme, type = ButtonType.FILL)
                    Button(text = "描边按钮", onClick = {}, theme = theme, type = ButtonType.OUTLINE)
                    Button(text = "文字按钮", onClick = {}, theme = theme, type = ButtonType.TEXT)
                }
            }
        }

        ExampleSection(
            title = "图标与加载",
            description = "带图标、纯图标与加载中"
        ) {
            ButtonRow {
                Button(text = "拨打电话", onClick = clicked, icon = Icons.phone)
                Button(onClick = clicked, shape = ButtonShape.SQUARE, icon = Icons.phone, contentDescription = "拨打电话")
                Button(text = "加载中", onClick = {}, loading = true)
            }
        }

        ExampleSection(
            title = "按钮尺寸",
            description = "LARGE / MEDIUM / SMALL / EXTRA_SMALL"
        ) {
            ButtonRow {
                Button(text = "大", onClick = clicked, size = ButtonSize.LARGE)
                Button(text = "中", onClick = clicked, size = ButtonSize.MEDIUM)
                Button(text = "小", onClick = clicked, size = ButtonSize.SMALL)
                Button(text = "超小", onClick = clicked, size = ButtonSize.EXTRA_SMALL)
            }
        }

        ExampleSection(
            title = "按钮形状",
            description = "矩形、方形、圆角、圆形、胶囊"
        ) {
            ButtonRow {
                Button(text = "矩形", onClick = clicked, shape = ButtonShape.RECTANGLE)
                Button(onClick = clicked, shape = ButtonShape.SQUARE, icon = Icons.star_fill, contentDescription = "收藏")
                Button(text = "圆角", onClick = clicked, shape = ButtonShape.ROUND)
                Button(onClick = clicked, shape = ButtonShape.CIRCLE, icon = Icons.star_fill, contentDescription = "收藏")
            }
            ButtonRow {
                Button(text = "胶囊", onClick = clicked, shape = ButtonShape.FILLED)
            }
        }

        ExampleSection(
            title = "禁用状态",
            description = "各类型与主题的禁用样式"
        ) {
            ButtonRow {
                Button(text = "填充按钮", onClick = {}, disabled = true)
                Button(text = "浅色按钮", onClick = {}, theme = ButtonTheme.LIGHT, disabled = true)
                Button(text = "默认按钮", onClick = {}, theme = ButtonTheme.DEFAULT, disabled = true)
            }
            ButtonRow {
                Button(text = "描边按钮", onClick = {}, type = ButtonType.OUTLINE, disabled = true)
                Button(text = "文字按钮", onClick = {}, type = ButtonType.TEXT, disabled = true)
            }
        }

        ExampleSection(
            title = "组合与通栏",
            description = "并排等分的按钮组与占满整行的 block 按钮"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    text = "取消",
                    onClick = clicked,
                    theme = ButtonTheme.LIGHT,
                    modifier = Modifier.weight(1f),
                    block = true
                )
                Button(
                    text = "确定",
                    onClick = clicked,
                    modifier = Modifier.weight(1f),
                    block = true
                )
            }
            Button(
                text = "发送",
                onClick = clicked,
                block = true,
                icon = Icons.paper_plane_tilt
            )
        }

        ExampleSection(
            title = "深色背景叠加",
            description = "文字按钮放在深色背景上",
            surface = SectionSurface.Plain
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Theme.shapes.xl)
                    .background(colors.foreground)
                    .padding(Spacing.lg)
            ) {
                Button(text = "文字按钮", onClick = clicked, theme = ButtonTheme.PRIMARY, type = ButtonType.TEXT)
                Button(text = "文字按钮", onClick = clicked, theme = ButtonTheme.DANGER, type = ButtonType.TEXT)
                Button(text = "文字按钮", onClick = clicked, theme = ButtonTheme.DEFAULT, type = ButtonType.TEXT)
            }
        }
    }
}

@Composable
private fun ButtonRow(content: @Composable RowScope.() -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
