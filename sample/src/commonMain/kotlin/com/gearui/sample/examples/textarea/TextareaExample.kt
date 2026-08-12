package com.gearui.sample.examples.textarea

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.foundation.text.BasicTextField as KuiklyBasicTextField
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.SolidColor
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.gearui.components.textarea.Textarea
import com.gearui.components.textarea.TextareaLayout
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/**
 * Textarea component examples
 *
 */
@Composable
fun TextareaExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // Component types
    var basicText by remember { mutableStateOf("") }
    var basicTitleText by remember { mutableStateOf("") }
    var autoHeightText by remember { mutableStateOf("") }
    var maxLengthText by remember { mutableStateOf("") }

    // Component states
    var disabledText by remember { mutableStateOf("") }

    // Component styles
    var verticalText by remember { mutableStateOf("") }
    var cardText by remember { mutableStateOf("") }

    // Special styles
    var borderedText by remember { mutableStateOf("") }
    var labelIconText by remember { mutableStateOf("") }
    var requiredText by remember { mutableStateOf("") }

    // Control test against the raw Kuikly BasicTextField
    var rawText by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // Raw Kuikly BasicTextField (no gearui-kit wrapper)
        ExampleSection(
            title = "原始 BasicTextField（对照组）",
            description = "直接用 Kuikly BasicTextField，无任何 gearui 包装"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Theme.colors.border, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                KuiklyBasicTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = Theme.colors.foreground
                    ),
                    cursorBrush = SolidColor(Theme.colors.primary),
                    singleLine = false,
                    minLines = 3,
                )
            }
        }

        // Component types
        ExampleSection(
            title = "组件类型",
            description = ""
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Basic multi-line input
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

                // Multi-line input with a title
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

                // Self-growing multi-line input
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

                // With a character limit
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

        // Component states
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

        // Component styles
        ExampleSection(
            title = "组件样式",
            description = ""
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Stacked style
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

                // Card style
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

        // Special styles
        ExampleSection(
            title = "特殊样式",
            description = ""
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Label outside the input
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

                // Custom title
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

                // Required, with helper text
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
 * Section title
 */
@Composable
private fun SectionTitle(title: String) {
    val colors = Theme.colors
    val typography = Theme.typography

    com.gearui.foundation.primitives.Text(
        text = title,
        style = com.gearui.foundation.typography.Typography.BodySmall,
        color = colors.mutedForeground,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
