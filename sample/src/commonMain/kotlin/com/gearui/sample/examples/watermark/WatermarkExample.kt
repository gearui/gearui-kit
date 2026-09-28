package com.gearui.sample.examples.watermark

import androidx.compose.runtime.Composable
import com.gearui.components.watermark.Watermark
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

// Demo dimensions: the watermarked area and the custom tiling being shown off.
private val DemoAreaHeight = 180.dp
private val CompactGapX = 28.dp
private val CompactGapY = 20.dp
private val ShiftedOffsetX = 40.dp
private val ShiftedOffsetY = 8.dp

@Composable
fun WatermarkExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "基础水印",
            description = "默认旋转角度与平铺间距"
        ) {
            DemoArea("基础文字水印") {
                Watermark(content = "GearUI", modifier = Modifier.fillMaxSize())
            }
        }

        ExampleSection(
            title = "多行水印",
            description = "content 中的换行拆成多行"
        ) {
            DemoArea("多行文本水印") {
                Watermark(
                    content = "GearUI Kit\nConfidential",
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.12f,
                    rotate = -18f
                )
            }
        }

        ExampleSection(
            title = "间距与旋转",
            description = "gapX / gapY 调节平铺间距，rotate 调节角度"
        ) {
            DemoArea("紧凑排布 / 旋转 -30°") {
                Watermark(
                    content = "Internal Use",
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.18f,
                    rotate = -30f,
                    gapX = CompactGapX,
                    gapY = CompactGapY
                )
            }
        }

        ExampleSection(
            title = "偏移与透明度",
            description = "offsetX / offsetY 调节起点，alpha 调节浓淡"
        ) {
            DemoArea("偏移起点 + 更高透明度") {
                Watermark(
                    content = "DO NOT SHARE",
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.22f,
                    offsetX = ShiftedOffsetX,
                    offsetY = ShiftedOffsetY,
                    rows = 4,
                    columns = 2
                )
            }
        }
    }
}

/** A fixed-height content area with a watermark layered over it. */
@Composable
private fun DemoArea(title: String, watermark: @Composable () -> Unit) {
    val colors = Theme.colors

    Box(modifier = Modifier.fillMaxWidth().height(DemoAreaHeight)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = Theme.typography.titleMedium,
                color = colors.foreground
            )
            Text(
                text = "Watermark 覆盖在内容层上方",
                style = Theme.typography.bodySmall,
                color = colors.mutedForeground
            )
        }
        watermark()
    }
}
