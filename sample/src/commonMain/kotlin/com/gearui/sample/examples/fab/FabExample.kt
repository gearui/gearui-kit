package com.gearui.sample.examples.fab

import androidx.compose.runtime.Composable
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonShape
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.icon.Icons
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

@Composable
fun FabExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(title = "组件类型", description = "纯图标悬浮按钮、图标加文字悬浮按钮") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    text = "",
                    icon = Icons.add,
                    shape = ButtonShape.CIRCLE,
                    size = ButtonSize.LARGE,
                    onClick = { Toast.show("点击纯图标 FAB") }
                )
                Button(
                    text = "Floating",
                    icon = Icons.add,
                    shape = ButtonShape.FILLED,
                    size = ButtonSize.LARGE,
                    onClick = { Toast.show("点击图文 FAB") }
                )
            }
        }

        ExampleSection(title = "组件状态", description = "主题、形状、尺寸") {
            Text(text = "Fab Theme", style = Typography.BodySmall, color = colors.textSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(text = "", icon = Icons.add, shape = ButtonShape.CIRCLE, theme = ButtonTheme.PRIMARY, modifier = Modifier.weight(1f), onClick = {})
                Button(text = "", icon = Icons.add, shape = ButtonShape.CIRCLE, theme = ButtonTheme.DEFAULT, modifier = Modifier.weight(1f), onClick = {})
                Button(text = "", icon = Icons.add, shape = ButtonShape.CIRCLE, theme = ButtonTheme.LIGHT, modifier = Modifier.weight(1f), onClick = {})
                Button(text = "", icon = Icons.add, shape = ButtonShape.CIRCLE, theme = ButtonTheme.DANGER, modifier = Modifier.weight(1f), onClick = {})
            }

            Text(text = "Fab Shape", style = Typography.BodySmall, color = colors.textSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(text = "", icon = Icons.add, shape = ButtonShape.CIRCLE, onClick = {})
                Button(text = "", icon = Icons.add, shape = ButtonShape.SQUARE, onClick = {})
            }

            Text(text = "Fab Size", style = Typography.BodySmall, color = colors.textSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(text = "", icon = Icons.add, shape = ButtonShape.CIRCLE, size = ButtonSize.LARGE, modifier = Modifier.weight(1f), onClick = {})
                Button(text = "", icon = Icons.add, shape = ButtonShape.CIRCLE, size = ButtonSize.MEDIUM, modifier = Modifier.weight(1f), onClick = {})
                Button(text = "", icon = Icons.add, shape = ButtonShape.CIRCLE, size = ButtonSize.SMALL, modifier = Modifier.weight(1f), onClick = {})
                Button(text = "", icon = Icons.add, shape = ButtonShape.CIRCLE, size = ButtonSize.EXTRA_SMALL, modifier = Modifier.weight(1f), onClick = {})
            }
        }

        ExampleSection(title = "悬浮效果演示", description = "固定在右下角的使用建议（演示）") {
            Button(
                text = "演示：点击此处可在页面右下角放置 FAB",
                type = ButtonType.OUTLINE,
                block = true,
                onClick = { Toast.show("建议在业务页使用 Box + Alignment.BottomEnd 定位") }
            )
        }
    }
}
