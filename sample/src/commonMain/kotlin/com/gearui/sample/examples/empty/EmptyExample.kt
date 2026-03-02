package com.gearui.sample.examples.empty

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Empty 空状态组件示例
 */
@Composable
fun EmptyExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础空状态
        ExampleSection(
            title = "基础空状态",
            description = "暂无数据提示"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "📭", style = Typography.HeadlineLarge, color = colors.textPlaceholder)
                Text(text = "暂无数据", style = Typography.BodyMedium, color = colors.textSecondary)
            }
        }

        // 搜索无结果
        ExampleSection(
            title = "搜索无结果",
            description = "搜索结果为空"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🔍", style = Typography.HeadlineLarge, color = colors.textPlaceholder)
                Text(text = "未找到相关内容", style = Typography.BodyMedium, color = colors.textSecondary)
                Text(text = "换个关键词试试", style = Typography.BodySmall, color = colors.textPlaceholder)
            }
        }

        // 网络错误
        ExampleSection(
            title = "网络错误",
            description = "网络连接失败"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "📡", style = Typography.HeadlineLarge, color = colors.textPlaceholder)
                Text(text = "网络连接失败", style = Typography.BodyMedium, color = colors.textSecondary)
                Text(text = "点击重试", style = Typography.BodySmall, color = colors.primary)
            }
        }

        // 自定义图标和文字
        ExampleSection(
            title = "自定义内容",
            description = "自定义图标和提示文字"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🎉", style = Typography.HeadlineLarge, color = colors.textPlaceholder)
                Text(text = "太棒了！", style = Typography.BodyMedium, color = colors.textPrimary)
                Text(text = "你已完成所有任务", style = Typography.BodySmall, color = colors.textSecondary)
            }
        }
    }
}
