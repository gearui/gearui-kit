package com.gearui.sample.examples.tab

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
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
 * Tab 选项卡组件示例
 */
@Composable
fun TabExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础选项卡
        ExampleSection(
            title = "基础选项卡",
            description = "横向选项卡切换"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Tab 栏
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("选项一", "选项二", "选项三").forEachIndexed { index, title ->
                        Text(
                            text = title,
                            style = Typography.BodyMedium,
                            color = if (selectedTab == index) colors.primary else colors.textSecondary,
                            modifier = Modifier.clickable { selectedTab = index }
                        )
                    }
                }

                // Tab 内容
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp).background(colors.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "这是选项${selectedTab + 1}的内容",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                }
            }
        }

        // 带图标选项卡
        ExampleSection(
            title = "带图标选项卡",
            description = "图标 + 文字"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("🏠 首页", "📂 分类", "👤 我的").forEach { tab ->
                    Text(text = tab, style = Typography.BodyMedium, color = colors.textSecondary)
                }
            }
        }

        // 滚动选项卡
        ExampleSection(
            title = "滚动选项卡",
            description = "选项较多时可滚动"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(5) { index ->
                    Text(
                        text = "Tab ${index + 1}",
                        style = Typography.BodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}
