package com.gearui.sample.examples.noticebar

import androidx.compose.runtime.*
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonType
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
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp

@Composable
fun NoticeBarExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    var closeableVisible by remember { mutableStateOf(true) }
    val colors = Theme.colors

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(title = "组件类型", description = "纯文字、图标、关闭、入口、自定义样式") {
            NoticeBarItem(content = "这是一条普通的通知信息")
            NoticeBarItem(content = "这是一条普通的通知信息", prefixIcon = Icons.info)
            if (closeableVisible) {
                NoticeBarItem(
                    content = "这是一条普通的通知信息",
                    prefixIcon = Icons.info,
                    suffixIcon = Icons.close,
                    onSuffixClick = {
                        closeableVisible = false
                        Toast.show("已关闭公告")
                    }
                )
            } else {
                Button(
                    text = "恢复带关闭公告栏",
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    onClick = { closeableVisible = true }
                )
            }
            NoticeBarItem(
                content = "这是一条普通的通知信息",
                prefixIcon = Icons.info,
                suffixIcon = Icons.chevron_right,
                onSuffixClick = { Toast.show("点击了入口") }
            )
            NoticeBarItem(
                content = "这是一条自定义样式的通知信息",
                prefixIcon = Icons.notifications,
                suffixIcon = Icons.chevron_right,
                backgroundColor = colors.surface,
                borderColor = colors.border
            )
        }

        ExampleSection(title = "组件状态", description = "普通、成功、警示、错误") {
            NoticeBarItem(content = "这是一条普通的通知信息", prefixIcon = Icons.info, tone = NoticeTone.INFO)
            NoticeBarItem(content = "这是一条成功的通知信息", prefixIcon = Icons.check, tone = NoticeTone.SUCCESS)
            NoticeBarItem(content = "这是一条警示的通知信息", prefixIcon = Icons.warning, tone = NoticeTone.WARNING)
            NoticeBarItem(content = "这是一条错误的通知信息", prefixIcon = Icons.error, tone = NoticeTone.ERROR)
        }

        ExampleSection(title = "组件样式", description = "卡片顶部公告栏") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface, RoundedCornerShape(12.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NoticeBarItem(
                    content = "这是一条普通的通知信息",
                    prefixIcon = Icons.info,
                    suffixIcon = Icons.chevron_right
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(colors.background, RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

private enum class NoticeTone { INFO, SUCCESS, WARNING, ERROR }

@Composable
private fun NoticeBarItem(
    content: String,
    modifier: Modifier = Modifier,
    prefixIcon: String? = null,
    suffixIcon: String? = null,
    tone: NoticeTone = NoticeTone.INFO,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    onSuffixClick: (() -> Unit)? = null
) {
    val colors = Theme.colors
    val (toneBackground, toneForeground) = when (tone) {
        NoticeTone.INFO -> colors.primaryLight to colors.primary
        NoticeTone.SUCCESS -> colors.successLight to colors.success
        NoticeTone.WARNING -> colors.warningLight to colors.warning
        NoticeTone.ERROR -> colors.dangerLight to colors.danger
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor ?: toneBackground, RoundedCornerShape(8.dp))
            .then(
                if (borderColor != null) Modifier.border(1.dp, borderColor, RoundedCornerShape(8.dp))
                else Modifier
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (prefixIcon != null) {
            Icon(name = prefixIcon, size = 16.dp, tint = toneForeground)
        }
        Text(
            text = content,
            style = Typography.BodySmall,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        if (suffixIcon != null) {
            Icon(
                name = suffixIcon,
                size = 16.dp,
                tint = colors.textSecondary,
                modifier = Modifier.clickable(enabled = onSuffixClick != null) {
                    onSuffixClick?.invoke()
                }
            )
        }
    }
}
