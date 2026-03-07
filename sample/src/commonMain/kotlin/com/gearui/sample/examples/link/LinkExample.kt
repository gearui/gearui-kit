package com.gearui.sample.examples.link

import androidx.compose.runtime.Composable
import com.gearui.components.icon.Icons
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

@Composable
fun LinkExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "组件类型",
            description = "基础链接、前后图标链接"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LinkItem("跳转链接", color = colors.primary) { Toast.show("点击基础链接") }
                LinkItem("下划线链接", color = colors.primary, underline = true) { Toast.show("点击下划线链接") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LinkItem("前置图标链接", color = colors.primary, prefixIcon = Icons.link) { Toast.show("点击前置图标链接") }
                LinkItem("后置图标链接", color = colors.primary, suffixIcon = Icons.open_in_new) { Toast.show("点击后置图标链接") }
            }
        }

        ExampleSection(
            title = "组件状态",
            description = "主题色与禁用态"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LinkItem("Primary", color = colors.primary) { Toast.show("Primary") }
                LinkItem("Default", color = colors.textPrimary) { Toast.show("Default") }
                LinkItem("Danger", color = colors.danger) { Toast.show("Danger") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LinkItem("Warning", color = colors.warning) { Toast.show("Warning") }
                LinkItem("Success", color = colors.success) { Toast.show("Success") }
                LinkItem("禁用态", color = colors.textPlaceholder, enabled = false) {}
            }
        }
    }
}

@Composable
private fun LinkItem(
    text: String,
    color: com.tencent.kuikly.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    underline: Boolean = false,
    prefixIcon: String? = null,
    suffixIcon: String? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable(enabled = enabled, onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (prefixIcon != null) {
                Icon(name = prefixIcon, size = 14.dp, tint = color)
            }
            Text(text = text, style = Typography.BodySmall, color = color)
            if (suffixIcon != null) {
                Icon(name = suffixIcon, size = 14.dp, tint = color)
            }
        }
        if (underline) {
            Spacer(
                modifier = Modifier
                    .width(56.dp)
                    .height(1.dp)
                    .background(color)
            )
        }
    }
}
