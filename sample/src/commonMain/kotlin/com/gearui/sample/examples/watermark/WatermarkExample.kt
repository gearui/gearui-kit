package com.gearui.sample.examples.watermark

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.watermark.Watermark
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Watermark 组件示例
 *
 * 水印组件用于在页面或容器上添加水印
 */
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
        // 基础水印
        ExampleSection(
            title = "基础水印",
            description = "在内容区域添加水印效果"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                    .background(colors.surface)
            ) {
                // 水印组件
                Watermark(
                    content = "GearUI",
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.15f
                )

                // 内容
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "带水印的内容区域",
                        style = Typography.TitleMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "水印会覆盖在内容之上",
                        style = Typography.BodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // 自定义水印文字
        ExampleSection(
            title = "自定义水印文字",
            description = "可以自定义水印显示的文字内容"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                    .background(colors.surface)
            ) {
                Watermark(
                    content = "机密文件",
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.1f
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "机密文档区域",
                        style = Typography.TitleMedium,
                        color = colors.textPrimary
                    )
                }
            }
        }

        // 不同透明度
        ExampleSection(
            title = "透明度设置",
            description = "通过 alpha 参数调整水印透明度"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // 低透明度
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                            .background(colors.surface)
                    ) {
                        Watermark(
                            content = "alpha=0.1",
                            modifier = Modifier.fillMaxSize(),
                            alpha = 0.1f
                        )
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "10%",
                                style = Typography.BodyMedium,
                                color = colors.textSecondary
                            )
                        }
                    }

                    // 中等透明度
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                            .background(colors.surface)
                    ) {
                        Watermark(
                            content = "alpha=0.2",
                            modifier = Modifier.fillMaxSize(),
                            alpha = 0.2f
                        )
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "20%",
                                style = Typography.BodyMedium,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Watermark 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. content: 水印显示的文字内容",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. alpha: 水印透明度，默认 0.15",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. modifier: 可自定义水印区域大小",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. 水印会平铺覆盖整个容器区域",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
