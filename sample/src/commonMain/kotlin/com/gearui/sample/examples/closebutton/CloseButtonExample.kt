package com.gearui.sample.examples.closebutton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.button.Button
import com.gearui.components.closebutton.CloseButton
import com.gearui.components.toast.Toast
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
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp

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
            description = "32 圆形中性底 + 18 灰色 ×，按压缩放与高亮"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CloseButton(onClick = { Toast.show("关闭") })
                CloseButton(onClick = {}, size = 28.dp, iconSize = 16.dp)
                CloseButton(onClick = {}, enabled = false)
            }
        }

        ExampleSection(
            title = "在内容中使用",
            description = "横幅右上角的关闭按钮"
        ) {
            if (bannerVisible) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Theme.shapes.xl)
                        .background(colors.muted)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("新版本可用", style = Theme.typography.bodyMedium, color = colors.foreground)
                        Text("更新后体验更流畅", style = Theme.typography.bodySmall, color = colors.mutedForeground)
                    }
                    CloseButton(onClick = { bannerVisible = false }, containerColor = colors.background)
                }
            } else {
                Button(text = "重新显示", onClick = { bannerVisible = true })
            }
        }

        ExampleSection(
            title = "媒体表面",
            description = "图片上用半透明白底 + 白色图标"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(Theme.shapes.xl)
                    .background(Color(0xFF3F3F46)),
                contentAlignment = Alignment.TopEnd,
            ) {
                CloseButton(
                    onClick = { Toast.show("关闭预览") },
                    modifier = Modifier.padding(12.dp),
                    containerColor = Color.White.copy(alpha = 0.2f),
                    iconColor = Color.White,
                )
            }
        }
    }
}
