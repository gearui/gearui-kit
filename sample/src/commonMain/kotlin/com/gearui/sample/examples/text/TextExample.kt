package com.gearui.sample.examples.text

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.text.style.TextDecoration
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/**
 * Text 组件示例
 *
 * 用于展示文本信息
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
        // ==================== 使用示例 ====================

        // 普通文本
        ExampleSection(
            title = "普通文本",
            description = "基础文本展示"
        ) {
            Text(
                text = exampleText,
                style = Typography.BodyMedium,
                color = colors.textPrimary
            )
        }

        // 指定常用属性
        ExampleSection(
            title = "指定常用属性",
            description = "自定义字体大小和颜色"
        ) {
            Box(
                modifier = Modifier
                    .background(colors.primaryLight)
                    .padding(8.dp)
            ) {
                Text(
                    text = exampleText,
                    style = Typography.HeadlineLarge,
                    color = colors.primary
                )
            }
        }

        // 字体层级
        ExampleSection(
            title = "字体层级",
            description = "系统提供的标准字体层级"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Headline Large 大标题",
                    style = Typography.HeadlineLarge,
                    color = colors.textPrimary
                )
                Text(
                    text = "Headline Medium 中标题",
                    style = Typography.HeadlineMedium,
                    color = colors.textPrimary
                )
                Text(
                    text = "Title Large 大标题",
                    style = Typography.TitleLarge,
                    color = colors.textPrimary
                )
                Text(
                    text = "Title Medium 中等标题",
                    style = Typography.TitleMedium,
                    color = colors.textPrimary
                )
                Text(
                    text = "Title Small 小标题",
                    style = Typography.TitleSmall,
                    color = colors.textPrimary
                )
                Text(
                    text = "Body Large 大正文",
                    style = Typography.BodyLarge,
                    color = colors.textPrimary
                )
                Text(
                    text = "Body Medium 中正文",
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )
                Text(
                    text = "Body Small 小正文",
                    style = Typography.BodySmall,
                    color = colors.textPrimary
                )
            }
        }

        // 文字颜色
        ExampleSection(
            title = "文字颜色",
            description = "语义化的文字颜色"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "主要文字 textPrimary",
                    style = Typography.BodyLarge,
                    color = colors.textPrimary
                )
                Text(
                    text = "次要文字 textSecondary",
                    style = Typography.BodyLarge,
                    color = colors.textSecondary
                )
                Text(
                    text = "占位文字 textPlaceholder",
                    style = Typography.BodyLarge,
                    color = colors.textPlaceholder
                )
                Text(
                    text = "禁用文字 textDisabled",
                    style = Typography.BodyLarge,
                    color = colors.textDisabled
                )
                // 反色文字
                Box(
                    modifier = Modifier
                        .background(colors.textPrimary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "反色文字 textAnti",
                        style = Typography.BodyLarge,
                        color = colors.textAnti
                    )
                }
            }
        }

        // 语义颜色
        ExampleSection(
            title = "语义颜色",
            description = "表达不同含义的文字颜色"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "品牌色文字 Primary",
                    style = Typography.BodyLarge,
                    color = colors.primary
                )
                Text(
                    text = "成功文字 Success",
                    style = Typography.BodyLarge,
                    color = colors.success
                )
                Text(
                    text = "警告文字 Warning",
                    style = Typography.BodyLarge,
                    color = colors.warning
                )
                Text(
                    text = "危险文字 Danger/Error",
                    style = Typography.BodyLarge,
                    color = colors.danger
                )
            }
        }

        // 带背景色的文字
        ExampleSection(
            title = "带背景色的文字",
            description = "文字配合背景色使用"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .background(colors.primaryLight)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "品牌色背景",
                        style = Typography.BodyMedium,
                        color = colors.primary
                    )
                }
                Box(
                    modifier = Modifier
                        .background(colors.successLight)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "成功背景",
                        style = Typography.BodyMedium,
                        color = colors.success
                    )
                }
                Box(
                    modifier = Modifier
                        .background(colors.warningLight)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "警告背景",
                        style = Typography.BodyMedium,
                        color = colors.warning
                    )
                }
                Box(
                    modifier = Modifier
                        .background(colors.dangerLight)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "危险背景",
                        style = Typography.BodyMedium,
                        color = colors.danger
                    )
                }
            }
        }

        // 文字截断
        ExampleSection(
            title = "文字截断",
            description = "超长文字的省略处理"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "这是一段很长的文字，用于测试文字截断效果，当文字超出容器宽度时会显示省略号",
                    style = Typography.BodyMedium,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "这是一段很长的文字，用于测试文字截断效果，当文字超出容器宽度时会显示省略号。这里设置最多显示两行。",
                    style = Typography.BodyMedium,
                    color = colors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 中英文混排
        ExampleSection(
            title = "中英文混排",
            description = "中英文混合显示"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .background(colors.primaryLight)
                        .padding(4.dp)
                ) {
                    Text(
                        text = "中华人民共和国 China",
                        style = Typography.BodyLarge,
                        color = colors.textPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .background(colors.primaryLight)
                        .padding(4.dp)
                ) {
                    Text(
                        text = "腾讯科技 Tencent fgjpqy",
                        style = Typography.TitleMedium,
                        color = colors.textPrimary
                    )
                }
            }
        }

        // 次要和三级文本
        ExampleSection(
            title = "语义化文本",
            description = "使用 secondary 和 tertiary 参数"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "主要文本（默认）",
                    style = Typography.BodyLarge
                )
                Text(
                    text = "次要文本 secondary=true",
                    style = Typography.BodyLarge,
                    secondary = true
                )
                Text(
                    text = "三级文本 tertiary=true",
                    style = Typography.BodyLarge,
                    tertiary = true
                )
            }
        }
    }
}
