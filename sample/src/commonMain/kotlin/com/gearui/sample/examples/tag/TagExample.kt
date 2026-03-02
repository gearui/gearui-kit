package com.gearui.sample.examples.tag

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/**
 * Tag 组件示例
 */
@Composable
fun TagExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础标签
        ExampleSection(
            title = "基础标签",
            description = "不同主题的标签样式"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SimpleTag(text = "默认", colors.surfaceVariant, colors.textPrimary)
                SimpleTag(text = "主要", colors.primary, colors.onPrimary)
                SimpleTag(text = "成功", colors.success, colors.onPrimary)
                SimpleTag(text = "警告", colors.warning, colors.onPrimary)
                SimpleTag(text = "危险", colors.danger, colors.onPrimary)
            }
        }

        // 浅色标签
        ExampleSection(
            title = "浅色标签",
            description = "浅色背景的标签样式"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "浅色标签（临时示例）",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }

        // 标签尺寸
        ExampleSection(
            title = "标签尺寸",
            description = "不同尺寸的标签"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "大尺寸",
                    style = Typography.BodyLarge,
                    color = colors.primary
                )
                Text(
                    text = "中尺寸",
                    style = Typography.BodyMedium,
                    color = colors.primary
                )
                Text(
                    text = "小尺寸",
                    style = Typography.BodySmall,
                    color = colors.primary
                )
            }
        }
    }
}

@Composable
private fun SimpleTag(
    text: String,
    backgroundColor: com.tencent.kuikly.compose.ui.graphics.Color,
    textColor: com.tencent.kuikly.compose.ui.graphics.Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = Typography.BodySmall,
            color = textColor
        )
    }
}
