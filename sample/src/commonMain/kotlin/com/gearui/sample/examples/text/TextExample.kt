package com.gearui.sample.examples.text

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.foundation.typography.TextStyle
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/**
 * Text component examples
 *
 * Displays text
 */
@Composable
fun TextExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val typography = Theme.typography

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "字体层级",
            description = "Theme.typography 提供的标准层级"
        ) {
            listOf<Pair<String, TextStyle>>(
                "headlineLarge 大标题" to typography.headlineLarge,
                "headlineMedium 中标题" to typography.headlineMedium,
                "titleLarge 大标题" to typography.titleLarge,
                "titleMedium 中标题" to typography.titleMedium,
                "titleSmall 小标题" to typography.titleSmall,
                "bodyLarge 大正文" to typography.bodyLarge,
                "bodyMedium 中正文" to typography.bodyMedium,
                "bodySmall 小正文" to typography.bodySmall,
                "caption 说明文字" to typography.caption,
            ).forEach { (label, style) ->
                Text(text = label, style = style, color = colors.foreground)
            }
        }

        ExampleSection(
            title = "层级颜色",
            description = "默认 foreground，次要文字用 mutedForeground"
        ) {
            Text(text = "主要文本（默认）", style = typography.bodyLarge)
            Text(text = "次要文本 mutedForeground", style = typography.bodyLarge, color = colors.mutedForeground)
        }

        ExampleSection(
            title = "语义颜色",
            description = "通过 color 传入主题语义色"
        ) {
            Text(text = "品牌色 primary", style = typography.bodyLarge, color = colors.primarySoftForeground)
            Text(text = "成功 success", style = typography.bodyLarge, color = colors.successSoftForeground)
            Text(text = "警告 warning", style = typography.bodyLarge, color = colors.warningSoftForeground)
            Text(text = "危险 destructive", style = typography.bodyLarge, color = colors.destructiveSoftForeground)
        }

        ExampleSection(
            title = "文字截断",
            description = "maxLines + TextOverflow.Ellipsis"
        ) {
            Text(
                text = "这是一段很长的文字，用于测试文字截断效果，当文字超出容器宽度时会显示省略号",
                style = typography.bodyMedium,
                color = colors.foreground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "这是一段很长的文字，用于测试文字截断效果，当文字超出容器宽度时会显示省略号。这里设置最多显示两行。",
                style = typography.bodyMedium,
                color = colors.foreground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        ExampleSection(
            title = "中英文混排",
            description = "灰底标出行框，看中英文基线与上下留白是否一致"
        ) {
            listOf(
                "中华人民共和国 China" to typography.bodyLarge,
                "腾讯科技 Tencent fgjpqy" to typography.titleMedium,
            ).forEach { (text, style) ->
                Box(modifier = Modifier.background(colors.muted).padding(Spacing.xs)) {
                    Text(text = text, style = style, color = colors.foreground)
                }
            }
        }
    }
}
