package com.gearui.sample.examples.card

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.CardSpecs
import com.gearui.primitives.composite.Card
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Card 卡片组件示例
 */
@Composable
fun CardExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础卡片
        ExampleSection(
            title = "基础卡片",
            description = "简单的卡片容器",
            useCardContainer = false
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "卡片标题", style = Typography.TitleMedium, color = colors.textPrimary)
                    Text(text = "这是卡片的内容区域，可以放置任意内容", style = Typography.BodyMedium, color = colors.textSecondary)
                }
            }
        }

        // 带封面卡片
        ExampleSection(
            title = "带封面卡片",
            description = "顶部显示图片",
            useCardContainer = false
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                padding = PaddingValues(0.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().background(colors.surface)) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(120.dp).background(colors.surfaceVariant)
                    )
                    Column(
                        modifier = Modifier.padding(CardSpecs.padding),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "图文卡片", style = Typography.TitleMedium, color = colors.textPrimary)
                        Text(text = "带有封面图的卡片样式", style = Typography.BodyMedium, color = colors.textSecondary)
                    }
                }
            }
        }

        // 卡片操作
        ExampleSection(
            title = "卡片操作",
            description = "底部操作按钮",
            useCardContainer = false
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                padding = PaddingValues(0.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().background(colors.surface)) {
                    Column(
                        modifier = Modifier.padding(CardSpecs.padding),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "操作卡片", style = Typography.TitleMedium, color = colors.textPrimary)
                        Text(text = "卡片内容描述", style = Typography.BodyMedium, color = colors.textSecondary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = CardSpecs.padding, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                    ) {
                        Text(text = "[取消]", style = Typography.BodyMedium, color = colors.textSecondary)
                        Text(text = "[确定]", style = Typography.BodyMedium, color = colors.primary)
                    }
                }
            }
        }
    }
}
