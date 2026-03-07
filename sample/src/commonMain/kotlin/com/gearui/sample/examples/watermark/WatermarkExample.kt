package com.gearui.sample.examples.watermark

import androidx.compose.runtime.Composable
import com.gearui.components.watermark.Watermark
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp

@Composable
fun WatermarkExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "基础水印",
            description = "默认旋转角度与平铺间距"
        ) {
            DemoCard {
                Watermark(
                    content = "GearUI",
                    modifier = Modifier.fillMaxSize()
                )
                CenterText("基础文字水印")
            }
        }

        ExampleSection(
            title = "多行水印",
            description = "支持换行文本（常见样式）"
        ) {
            DemoCard {
                Watermark(
                    content = "GearUI Kit\\nConfidential",
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.12f,
                    rotate = -18f
                )
                CenterText("多行文本水印")
            }
        }

        ExampleSection(
            title = "间距与旋转",
            description = "可调节 gapX / gapY / rotate"
        ) {
            DemoCard {
                Watermark(
                    content = "Internal Use",
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.18f,
                    rotate = -30f,
                    gapX = 28.dp,
                    gapY = 20.dp
                )
                CenterText("紧凑排布 / 旋转 -30°")
            }
        }

        ExampleSection(
            title = "偏移与透明度",
            description = "可调节 offsetX / offsetY / alpha"
        ) {
            DemoCard {
                Watermark(
                    content = "DO NOT SHARE",
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.22f,
                    offsetX = 40.dp,
                    offsetY = 8.dp,
                    rows = 4,
                    columns = 2
                )
                CenterText("偏移起点 + 更高透明度")
            }
        }
    }
}

@Composable
private fun DemoCard(content: @Composable () -> Unit) {
    val colors = Theme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            .background(colors.surface)
    ) {
        content()
    }
}

@Composable
private fun CenterText(text: String) {
    val colors = Theme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = Typography.TitleMedium,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Watermark 覆盖在内容层上方",
            style = Typography.BodySmall,
            color = colors.textSecondary
        )
    }
}
