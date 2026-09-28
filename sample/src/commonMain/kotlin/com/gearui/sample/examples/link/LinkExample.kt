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
import com.gearui.foundation.layout.Spacing

@Composable
fun LinkExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "Link",
            description = "行内跳转链接，可带图标、可去掉下划线"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
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
            description = "SMALL / MEDIUM / LARGE 与禁用态"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Link("小号", onClick = {}, size = LinkSize.SMALL)
                Link("中号", onClick = {})
                Link("大号", onClick = {}, size = LinkSize.LARGE)
                Link("禁用", onClick = {}, enabled = false)
            }
        }

        ExampleSection(
            title = "LinkButton",
            description = "无内边距的文字按钮，用于「忘记密码」等次要操作"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
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
