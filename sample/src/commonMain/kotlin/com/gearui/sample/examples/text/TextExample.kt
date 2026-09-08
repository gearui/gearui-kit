package com.gearui.sample.examples.text

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.text.style.TextDecoration
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.tencent.kuikly.compose.ui.unit.dp
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
    val exampleText = "文本Text"

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ==================== Examples ====================

        // Plain text
        ExampleSection(
            title = "普通文本",
            description = "基础文本展示"
        ) {
            Text(
                text = exampleText,
                style = Theme.typography.bodyMedium,
                color = colors.foreground
            )
        }

        // Common properties
        ExampleSection(
            title = "指定常用属性",
            description = "自定义字体大小和颜色"
        ) {
            Box(
                modifier = Modifier
                    .background(colors.muted)
                    .padding(8.dp)
            ) {
                Text(
                    text = exampleText,
                    style = Theme.typography.headlineLarge,
                    color = colors.primary
                )
            }
        }

        // Type scale
        ExampleSection(
            title = "字体层级",
            description = "系统提供的标准字体层级"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Headline Large 大标题",
                    style = Theme.typography.headlineLarge,
                    color = colors.foreground
                )
                Text(
                    text = "Headline Medium 中标题",
                    style = Theme.typography.headlineMedium,
                    color = colors.foreground
                )
                Text(
                    text = "Title Large 大标题",
                    style = Theme.typography.titleLarge,
                    color = colors.foreground
                )
                Text(
                    text = "Title Medium 中等标题",
                    style = Theme.typography.titleMedium,
                    color = colors.foreground
                )
                Text(
                    text = "Title Small 小标题",
                    style = Theme.typography.titleSmall,
                    color = colors.foreground
                )
                Text(
                    text = "Body Large 大正文",
                    style = Theme.typography.bodyLarge,
                    color = colors.foreground
                )
                Text(
                    text = "Body Medium 中正文",
                    style = Theme.typography.bodyMedium,
                    color = colors.foreground
                )
                Text(
                    text = "Body Small 小正文",
                    style = Theme.typography.bodySmall,
                    color = colors.foreground
                )
            }
        }

        // Text colours
        ExampleSection(
            title = "文字颜色",
            description = "语义化的文字颜色"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "主要文字 textPrimary",
                    style = Theme.typography.bodyLarge,
                    color = colors.foreground
                )
                Text(
                    text = "次要文字 textSecondary",
                    style = Theme.typography.bodyLarge,
                    color = colors.mutedForeground
                )
                Text(
                    text = "占位文字 textPlaceholder",
                    style = Theme.typography.bodyLarge,
                    color = colors.mutedForeground
                )
                Text(
                    text = "禁用文字 textDisabled",
                    style = Theme.typography.bodyLarge,
                    color = colors.mutedForeground
                )
                // Inverted text
                Box(
                    modifier = Modifier
                        .background(colors.foreground)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "反色文字 textAnti",
                        style = Theme.typography.bodyLarge,
                        color = colors.primaryForeground
                    )
                }
            }
        }

        // Semantic colours
        ExampleSection(
            title = "语义颜色",
            description = "表达不同含义的文字颜色"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "品牌色文字 Primary",
                    style = Theme.typography.bodyLarge,
                    color = colors.primary
                )
                Text(
                    text = "成功文字 Success",
                    style = Theme.typography.bodyLarge,
                    color = colors.success
                )
                Text(
                    text = "警告文字 Warning",
                    style = Theme.typography.bodyLarge,
                    color = colors.warning
                )
                Text(
                    text = "危险文字 Danger/Error",
                    style = Theme.typography.bodyLarge,
                    color = colors.destructive
                )
            }
        }

        // Text on a background colour
        ExampleSection(
            title = "带背景色的文字",
            description = "文字配合背景色使用"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .background(colors.muted)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "品牌色背景",
                        style = Theme.typography.bodyMedium,
                        color = colors.primary
                    )
                }
                Box(
                    modifier = Modifier
                        .background(colors.success.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "成功背景",
                        style = Theme.typography.bodyMedium,
                        color = colors.success
                    )
                }
                Box(
                    modifier = Modifier
                        .background(colors.warning.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "警告背景",
                        style = Theme.typography.bodyMedium,
                        color = colors.warning
                    )
                }
                Box(
                    modifier = Modifier
                        .background(colors.destructive.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "危险背景",
                        style = Theme.typography.bodyMedium,
                        color = colors.destructive
                    )
                }
            }
        }

        // Truncation
        ExampleSection(
            title = "文字截断",
            description = "超长文字的省略处理"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "这是一段很长的文字，用于测试文字截断效果，当文字超出容器宽度时会显示省略号",
                    style = Theme.typography.bodyMedium,
                    color = colors.foreground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "这是一段很长的文字，用于测试文字截断效果，当文字超出容器宽度时会显示省略号。这里设置最多显示两行。",
                    style = Theme.typography.bodyMedium,
                    color = colors.foreground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Mixed Chinese and Latin text
        ExampleSection(
            title = "中英文混排",
            description = "中英文混合显示"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .background(colors.muted)
                        .padding(4.dp)
                ) {
                    Text(
                        text = "中华人民共和国 China",
                        style = Theme.typography.bodyLarge,
                        color = colors.foreground
                    )
                }
                Box(
                    modifier = Modifier
                        .background(colors.muted)
                        .padding(4.dp)
                ) {
                    Text(
                        text = "腾讯科技 Tencent fgjpqy",
                        style = Theme.typography.titleMedium,
                        color = colors.foreground
                    )
                }
            }
        }

        // Secondary and tertiary text
        ExampleSection(
            title = "语义化文本",
            description = "使用 secondary 和 tertiary 参数"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "主要文本（默认）",
                    style = Theme.typography.bodyLarge
                )
                Text(
                    text = "次要文本 secondary=true",
                    style = Theme.typography.bodyLarge,
                    secondary = true
                )
                Text(
                    text = "三级文本 tertiary=true",
                    style = Theme.typography.bodyLarge,
                    tertiary = true
                )
            }
        }
    }
}
