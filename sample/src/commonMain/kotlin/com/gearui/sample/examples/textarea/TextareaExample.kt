package com.gearui.sample.examples.textarea

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.textarea.Textarea
import com.gearui.components.textarea.TextareaLayout
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/**
 * Textarea 多行文本框示例
 *
 */
@Composable
fun TextareaExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 组件类型
    var basicText by remember { mutableStateOf("") }
    var basicTitleText by remember { mutableStateOf("") }
    var autoHeightText by remember { mutableStateOf("") }
    var maxLengthText by remember { mutableStateOf("") }

    // 组件状态
    var disabledText by remember { mutableStateOf("") }

    // 组件样式
    var verticalText by remember { mutableStateOf("") }
    var cardText by remember { mutableStateOf("") }

    // 特殊样式
    var borderedText by remember { mutableStateOf("") }
    var labelIconText by remember { mutableStateOf("") }
    var requiredText by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 组件类型
        ExampleSection(
            title = "组件类型",
            description = ""
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 基础多文本输入框
                Column {
                    SectionTitle("基础多文本输入框")
                    Textarea(
                        value = basicText,
                        onValueChange = { basicText = it },
                        placeholder = "请输入文字",
                        minLines = 4,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 带标题多文本输入框
                Column {
                    SectionTitle("带标题多文本输入框")
                    Textarea(
                        value = basicTitleText,
                        onValueChange = { basicTitleText = it },
                        label = "标签文字",
                        placeholder = "请输入文字",
                        minLines = 4,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 自动增高多文本输入框
                Column {
                    SectionTitle("自动增高多文本输入框")
                    Textarea(
                        value = autoHeightText,
                        onValueChange = { autoHeightText = it },
                        placeholder = "请输入文字",
                        minLines = 1,
                        autosize = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 设置字符数限制
                Column {
                    SectionTitle("设置字符数限制")
                    Textarea(
                        value = maxLengthText,
                        onValueChange = { maxLengthText = it },
                        label = "标签文字",
                        placeholder = "请输入文字",
                        minLines = 4,
                        maxLines = 4,
                        maxLength = 500,
                        indicator = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 组件状态
        ExampleSection(
            title = "组件状态",
            description = ""
        ) {
            Column {
                SectionTitle("禁用状态")
                Textarea(
                    value = disabledText,
                    onValueChange = { disabledText = it },
                    label = "标签文字",
                    placeholder = "不可编辑文字",
                    minLines = 4,
                    maxLines = 4,
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 组件样式
        ExampleSection(
            title = "组件样式",
            description = ""
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 竖排样式
                Column {
                    SectionTitle("竖排样式")
                    Textarea(
                        value = verticalText,
                        onValueChange = { verticalText = it },
                        label = "标签文字",
                        placeholder = "请输入文字",
                        minLines = 4,
                        maxLines = 4,
                        maxLength = 500,
                        indicator = true,
                        layout = TextareaLayout.VERTICAL,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 卡片样式
                Column {
                    SectionTitle("卡片样式")
                    Textarea(
                        value = cardText,
                        onValueChange = { cardText = it },
                        label = "标签文字",
                        placeholder = "请输入文字",
                        minLines = 4,
                        maxLines = 4,
                        maxLength = 500,
                        indicator = true,
                        cardStyle = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 特殊样式
        ExampleSection(
            title = "特殊样式",
            description = ""
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 标签外置输入框
                Column {
                    SectionTitle("标签外置输入框")
                    Textarea(
                        value = borderedText,
                        onValueChange = { borderedText = it },
                        label = "标签文字",
                        placeholder = "请输入文字",
                        minLines = 4,
                        maxLines = 4,
                        maxLength = 500,
                        indicator = true,
                        layout = TextareaLayout.VERTICAL,
                        bordered = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 自定义标题
                Column {
                    SectionTitle("自定义标题")
                    Textarea(
                        value = labelIconText,
                        onValueChange = { labelIconText = it },
                        label = "地址信息",
                        labelIcon = "location",
                        placeholder = "请输入文字",
                        minLines = 4,
                        maxLines = 4,
                        maxLength = 500,
                        indicator = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 必填和辅助说明
                Column {
                    SectionTitle("必填和辅助说明")
                    Textarea(
                        value = requiredText,
                        onValueChange = { requiredText = it },
                        label = "标签文字",
                        placeholder = "请输入文字",
                        minLines = 4,
                        maxLines = 4,
                        maxLength = 500,
                        indicator = true,
                        layout = TextareaLayout.VERTICAL,
                        required = true,
                        additionInfo = "辅助说明",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * 小节标题
 */
@Composable
private fun SectionTitle(title: String) {
    val colors = Theme.colors
    val typography = Theme.typography

    com.gearui.foundation.primitives.Text(
        text = title,
        style = com.gearui.foundation.typography.Typography.BodySmall,
        color = colors.textSecondary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
