package com.gearui.sample.examples.empty

import androidx.compose.runtime.Composable
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.empty.EmptyState
import com.gearui.components.icon.Icons
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Icon
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp

@Composable
fun EmptyExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(title = "图标空状态", description = "默认图标 + 描述文案") {
            EmptyState(message = "描述文字")
        }

        ExampleSection(title = "自定义图标空状态", description = "自定义 icon 形态") {
            EmptyState(
                message = "描述文字",
                icon = {
                    Icon(
                        name = Icons.hourglass_empty,
                        size = 36.dp,
                        tint = colors.textPlaceholder
                    )
                }
            )
        }

        ExampleSection(title = "自定义图片空状态", description = "自定义 image 区域") {
            EmptyState(
                message = "描述文字",
                icon = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surfaceVariant)
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            name = Icons.image,
                            size = 48.dp,
                            tint = colors.textPlaceholder
                        )
                    }
                }
            )
        }

        ExampleSection(title = "带操作空状态", description = "默认操作按钮") {
            EmptyState(
                message = "描述文字",
                actionText = "操作按钮",
                onAction = {
                    Toast.show("点击了操作按钮")
                }
            )
        }

        ExampleSection(title = "自定义带操作空状态", description = "使用 customAction 自定义操作区") {
            EmptyState(
                message = "描述文字",
                customAction = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(
                            text = "自定义操作按钮",
                            theme = ButtonTheme.DANGER,
                            size = ButtonSize.MEDIUM,
                            onClick = { Toast.show("点击了自定义操作按钮") }
                        )
                    }
                }
            )
        }
    }
}
