package com.gearui.sample.examples.link

import androidx.compose.runtime.Composable
import com.gearui.components.icon.Icons
import com.gearui.components.link.Link
import com.gearui.components.link.LinkButton
import com.gearui.components.link.LinkSize
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.unit.dp

@Composable
fun LinkExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "Link",
            description = "行内跳转：前景色 + 分隔线色下划线，按压缩放"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Link("查看详情", onClick = { Toast.show("查看详情") })
                Link("帮助中心", onClick = { Toast.show("帮助中心") }, endIcon = Icons.arrow_square_out)
                Link("复制链接", onClick = { Toast.show("复制链接") }, startIcon = Icons.link, underline = false)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("注册即表示同意", style = Theme.typography.bodySmall, color = colors.mutedForeground)
                Link("《用户协议》", onClick = { Toast.show("用户协议") }, size = LinkSize.SMALL)
            }
        }

        ExampleSection(
            title = "尺寸与状态",
            description = "SMALL / MEDIUM / LARGE，禁用态"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Link("Small", onClick = {}, size = LinkSize.SMALL)
                Link("Medium", onClick = {})
                Link("Large", onClick = {}, size = LinkSize.LARGE)
                Link("禁用", onClick = {}, enabled = false)
            }
        }

        ExampleSection(
            title = "LinkButton",
            description = "无内边距、无高亮的 ghost 按钮，用于「忘记密码」「服务条款」等"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LinkButton("忘记密码？", onClick = { Toast.show("忘记密码") })
                LinkButton("立即注册", onClick = { Toast.show("立即注册") }, color = colors.primary)
                LinkButton("删除", onClick = { Toast.show("删除") }, color = colors.destructive)
                LinkButton("禁用", onClick = {}, enabled = false)
            }
        }
    }
}
