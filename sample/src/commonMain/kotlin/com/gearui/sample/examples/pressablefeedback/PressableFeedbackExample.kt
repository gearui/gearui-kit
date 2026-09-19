package com.gearui.sample.examples.pressablefeedback

import androidx.compose.runtime.Composable
import com.gearui.components.icon.Icons
import com.gearui.components.toast.Toast
import com.gearui.foundation.interaction.PressableFeedback
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.unit.dp

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
            description = "缩放按宽度补偿：宽卡片几乎不动，小目标明显收缩；高亮叠加在内容之上"
        ) {
            Pressable(shape = card, onClick = { Toast.show("打开订单") }) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(colors.card).padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(name = Icons.wallet, size = 24.dp, tint = colors.foreground)
                    Column(Modifier.weight(1f)) {
                        Text("我的订单", style = Theme.typography.bodyMedium, color = colors.foreground)
                        Text("查看全部订单", style = Theme.typography.bodySmall, color = colors.mutedForeground)
                    }
                    Icon(name = Icons.caret_right, size = 16.dp, tint = colors.mutedForeground)
                }
            }
        }

        ExampleSection(
            title = "图片与色块",
            description = "高亮是前景层叠加，彩色背景上同样可见"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(colors.primary, colors.success, Color(0xFF3F3F46)).forEach { fill ->
                    Pressable(shape = card, onClick = {}, modifier = Modifier.weight(1f)) {
                        Box(Modifier.fillMaxWidth().height(72.dp).background(fill))
                    }
                }
            }
        }

        ExampleSection(
            title = "只缩放 / 只高亮",
            description = "scale = false 或 highlight = false"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Pressable(shape = card, onClick = {}, scale = false, modifier = Modifier.weight(1f)) {
                    Box(Modifier.fillMaxWidth().height(56.dp).background(colors.muted), contentAlignment = Alignment.Center) {
                        Text("只高亮", color = colors.foreground)
                    }
                }
                Pressable(shape = card, onClick = {}, highlight = false, modifier = Modifier.weight(1f)) {
                    Box(Modifier.fillMaxWidth().height(56.dp).background(colors.muted), contentAlignment = Alignment.Center) {
                        Text("只缩放", color = colors.foreground)
                    }
                }
                Pressable(shape = Theme.shapes.full, onClick = {}) {
                    Box(Modifier.size(56.dp).background(colors.muted), contentAlignment = Alignment.Center) {
                        Icon(name = Icons.heart, size = 22.dp, tint = colors.foreground)
                    }
                }
            }
        }
    }
}

@Composable
private fun Pressable(
    shape: Shape,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    scale: Boolean = true,
    highlight: Boolean = true,
    content: @Composable () -> Unit,
) {
    PressableFeedback(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        scale = scale,
        highlight = highlight,
    ) { content() }
}
