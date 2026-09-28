package com.gearui.sample.examples.closebutton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.button.Button
import com.gearui.components.closebutton.CloseButton
import com.gearui.components.toast.Toast
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.primitives.composite.Card
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp

/** The custom, smaller button size shown in the first section. */
private val CompactSize = 28.dp

/** Height of the media placeholder the button floats over. */
private val MediaHeight = 120.dp

/** Alpha of the translucent container on media. */
private const val MediaContainerAlpha = 0.2f

@Composable
fun CloseButtonExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    var bannerVisible by remember { mutableStateOf(true) }

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "基础",
            description = "默认尺寸、自定义尺寸与禁用态"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CloseButton(onClick = { Toast.show("关闭") })
                CloseButton(onClick = {}, size = CompactSize, iconSize = IconSizes.Default.md)
                CloseButton(onClick = {}, enabled = false)
            }
        }

        ExampleSection(
            title = "在内容中使用",
            description = "卡片右侧的关闭按钮，点击后隐藏卡片",
            surface = SectionSurface.Plain
        ) {
            if (bannerVisible) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("新版本可用", style = Theme.typography.bodyMedium, color = colors.foreground)
                            Text("更新后体验更流畅", style = Theme.typography.bodySmall, color = colors.mutedForeground)
                        }
                        CloseButton(onClick = { bannerVisible = false })
                    }
                }
            } else {
                Button(text = "重新显示", onClick = { bannerVisible = true })
            }
        }

        ExampleSection(
            title = "媒体表面",
            description = "图片上用半透明底色与反色图标",
            surface = SectionSurface.Plain
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MediaHeight)
                    .clip(Theme.shapes.xl)
                    .background(colors.primary),
                contentAlignment = Alignment.TopEnd,
            ) {
                CloseButton(
                    onClick = { Toast.show("关闭预览") },
                    modifier = Modifier.padding(Spacing.md),
                    containerColor = colors.primaryForeground.copy(alpha = MediaContainerAlpha),
                    iconColor = colors.primaryForeground,
                )
            }
        }
    }
}
