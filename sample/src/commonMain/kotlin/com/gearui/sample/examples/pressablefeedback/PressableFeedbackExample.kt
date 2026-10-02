package com.gearui.sample.examples.pressablefeedback

import com.gearui.components.icon.*
import androidx.compose.runtime.Composable
import com.gearui.components.icon.Icons
import com.gearui.components.toast.Toast
import com.gearui.foundation.interaction.PressableFeedback
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Icon
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
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

/** Height of the colour swatches standing in for images. */
private val SwatchHeight = 72.dp

/** Height of the tiles in the scale-only / highlight-only row. */
private val TileHeight = 56.dp

@Composable
fun PressableFeedbackExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val card = Theme.shapes.xl

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "卡片按压",
            description = "宽卡片几乎不缩放，高亮叠加在内容之上",
            surface = SectionSurface.Plain
        ) {
            PressableFeedback(shape = card, onClick = { Toast.show("打开订单") }) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.wallet, size = IconSizes.Default.xl, tint = colors.foreground)
                        Column(Modifier.weight(1f)) {
                            Text("我的订单", style = Theme.typography.bodyMedium, color = colors.foreground)
                            Text("查看全部订单", style = Theme.typography.bodySmall, color = colors.mutedForeground)
                        }
                        Icon(Icons.caretRight, size = IconSizes.Default.md, tint = colors.mutedForeground)
                    }
                }
            }
        }

        ExampleSection(
            title = "图片与色块",
            description = "高亮是前景层叠加，彩色背景上同样可见"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                listOf("主色" to colors.primary, "成功色" to colors.success, "危险色" to colors.destructive).forEach { (name, fill) ->
                    PressableFeedback(shape = card, onClick = {}, modifier = Modifier.weight(1f), contentDescription = name) {
                        Box(Modifier.fillMaxWidth().height(SwatchHeight).background(fill))
                    }
                }
            }
        }

        ExampleSection(
            title = "只缩放 / 只高亮",
            description = "scale = false 或 highlight = false"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                PressableFeedback(shape = card, onClick = {}, scale = false, modifier = Modifier.weight(1f)) {
                    Box(
                        Modifier.fillMaxWidth().height(TileHeight).background(colors.muted),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("只高亮", style = Theme.typography.bodyMedium, color = colors.foreground)
                    }
                }
                PressableFeedback(shape = card, onClick = {}, highlight = false, modifier = Modifier.weight(1f)) {
                    Box(
                        Modifier.fillMaxWidth().height(TileHeight).background(colors.muted),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("只缩放", style = Theme.typography.bodyMedium, color = colors.foreground)
                    }
                }
                PressableFeedback(shape = Theme.shapes.full, onClick = {}, contentDescription = "收藏") {
                    Box(Modifier.size(TileHeight).background(colors.muted), contentAlignment = Alignment.Center) {
                        Icon(Icons.heart, size = IconSizes.Default.xl, tint = colors.foreground)
                    }
                }
            }
        }
    }
}
