package com.gearui.sample.examples.list

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.primitives.DividerFull
import com.gearui.theme.Theme

/**
 * List 列表组件示例
 */
@Composable
fun ListExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础列表
        ExampleSection(
            title = "基础列表",
            description = "简单的列表展示"
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                repeat(3) { index ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "列表项 ${index + 1}", style = Typography.BodyMedium, color = colors.textPrimary)
                        Text(text = "›", style = Typography.BodyLarge, color = colors.textPlaceholder)
                    }
                    if (index < 2) DividerFull()
                }
            }
        }

        // 带图标列表
        ExampleSection(
            title = "带图标列表",
            description = "左侧显示图标"
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                listOf("📧 消息", "⚙️ 设置", "👤 个人信息").forEach { item ->
                    Text(
                        text = item,
                        style = Typography.BodyMedium,
                        color = colors.textPrimary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    DividerFull()
                }
            }
        }

        // 多行列表
        ExampleSection(
            title = "多行列表",
            description = "显示标题和描述"
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "标题文本", style = Typography.BodyLarge, color = colors.textPrimary)
                    Text(text = "这是描述信息，可以显示更多详细内容", style = Typography.BodySmall, color = colors.textSecondary)
                }
            }
        }
    }
}
