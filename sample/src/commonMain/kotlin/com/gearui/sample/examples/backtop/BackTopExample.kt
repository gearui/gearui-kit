package com.gearui.sample.examples.backtop

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.backtop.BackTop
import com.gearui.components.backtop.BackTopCustom
import com.gearui.components.backtop.BackTopStyle
import com.gearui.components.backtop.BackTopTheme
import com.gearui.components.button.Button
import com.gearui.components.icon.Icons
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.theme.Theme

// Demo content dimension: the stand-in content area the button floats over.
private val StageHeight = 180.dp

/**
 * BackTop component examples.
 */
@Composable
fun BackTopExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        BackTopDemo(
            title = "圆形 · 亮色",
            description = "默认样式：CIRCLE + LIGHT"
        ) { visible, hide ->
            BackTop(
                visible = visible,
                onClick = hide,
                style = BackTopStyle.CIRCLE,
                theme = BackTopTheme.LIGHT
            )
        }

        BackTopDemo(
            title = "圆形 · 暗色",
            description = "theme = DARK，深色底浅色图标"
        ) { visible, hide ->
            BackTop(
                visible = visible,
                onClick = hide,
                style = BackTopStyle.CIRCLE,
                theme = BackTopTheme.DARK
            )
        }

        BackTopDemo(
            title = "圆形 · 带文字",
            description = "showText = true，图标下方显示文字"
        ) { visible, hide ->
            BackTop(
                visible = visible,
                onClick = hide,
                style = BackTopStyle.CIRCLE,
                theme = BackTopTheme.LIGHT,
                showText = true
            )
        }

        BackTopDemo(
            title = "半圆 · 亮色",
            description = "HALF_CIRCLE：贴右边缘显示"
        ) { visible, hide ->
            BackTop(
                visible = visible,
                onClick = hide,
                style = BackTopStyle.HALF_CIRCLE,
                theme = BackTopTheme.LIGHT
            )
        }

        BackTopDemo(
            title = "半圆 · 暗色",
            description = "HALF_CIRCLE + DARK"
        ) { visible, hide ->
            BackTop(
                visible = visible,
                onClick = hide,
                style = BackTopStyle.HALF_CIRCLE,
                theme = BackTopTheme.DARK
            )
        }

        BackTopDemo(
            title = "自定义内容",
            description = "BackTopCustom 自定义按钮内容"
        ) { visible, hide ->
            BackTopCustom(
                visible = visible,
                onClick = hide,
                theme = BackTopTheme.LIGHT
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        name = Icons.caret_up,
                        size = IconSizes.Default.md,
                        tint = colors.primary
                    )
                    Text(
                        text = "顶部",
                        style = Theme.typography.bodyExtraSmall,
                        color = colors.primarySoftForeground
                    )
                }
            }
        }
    }
}

/**
 * One section: a show/hide toggle over a stand-in content area, with the BackTop
 * variant anchored to its bottom-end corner.
 */
@Composable
private fun BackTopDemo(
    title: String,
    description: String,
    backTop: @Composable BoxScope.(visible: Boolean, hide: () -> Unit) -> Unit
) {
    val colors = Theme.colors
    var visible by remember { mutableStateOf(false) }

    ExampleSection(title = title, description = description) {
        Button(
            text = if (visible) "隐藏" else "显示",
            onClick = { visible = !visible }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(StageHeight)
                .clip(Theme.shapes.md)
                .background(colors.muted),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = "内容区域",
                style = Theme.typography.bodyMedium,
                color = colors.mutedForeground,
                modifier = Modifier.align(Alignment.Center)
            )
            backTop(visible) { visible = false }
        }
    }
}
