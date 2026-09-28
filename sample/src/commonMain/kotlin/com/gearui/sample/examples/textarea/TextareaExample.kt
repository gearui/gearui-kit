package com.gearui.sample.examples.textarea

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.components.textarea.Textarea
import com.gearui.components.textarea.TextareaLayout
import com.gearui.foundation.field.FieldVariant
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface

private const val MAX_LENGTH = 500

/**
 * Textarea component examples
 *
 * Every field sits on a white Card section, so it uses the filled variant
 * (`variant = FieldVariant.SECONDARY`); the shadowed PRIMARY variant is shown once,
 * on the page background, where it belongs.
 */
@Composable
fun TextareaExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    var basicText by remember { mutableStateOf("") }
    var basicTitleText by remember { mutableStateOf("") }
    var autoHeightText by remember { mutableStateOf("") }
    var maxLengthText by remember { mutableStateOf("") }
    var requiredText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    var horizontalText by remember { mutableStateOf("") }
    var pageText by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "基础用法",
            description = "固定 4 行高度的多行输入"
        ) {
            Textarea(
                value = basicText,
                onValueChange = { basicText = it },
                placeholder = "请输入文字",
                minLines = 4,
                maxLines = 4,
                variant = FieldVariant.SECONDARY,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ExampleSection(
            title = "带标签",
            description = "label 显示在输入框上方"
        ) {
            Textarea(
                value = basicTitleText,
                onValueChange = { basicTitleText = it },
                label = "标签文字",
                placeholder = "请输入文字",
                minLines = 4,
                maxLines = 4,
                variant = FieldVariant.SECONDARY,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ExampleSection(
            title = "自动增高",
            description = "autosize = true，从一行开始随内容增高"
        ) {
            Textarea(
                value = autoHeightText,
                onValueChange = { autoHeightText = it },
                placeholder = "请输入文字",
                minLines = 1,
                autosize = true,
                variant = FieldVariant.SECONDARY,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ExampleSection(
            title = "字数限制",
            description = "maxLength 配合 indicator 显示已输入字数"
        ) {
            Textarea(
                value = maxLengthText,
                onValueChange = { maxLengthText = it },
                label = "标签文字",
                placeholder = "请输入文字",
                minLines = 4,
                maxLines = 4,
                maxLength = MAX_LENGTH,
                indicator = true,
                variant = FieldVariant.SECONDARY,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ExampleSection(
            title = "必填与辅助说明",
            description = "required 标星，additionInfo 显示在下方"
        ) {
            Textarea(
                value = requiredText,
                onValueChange = { requiredText = it },
                label = "标签文字",
                placeholder = "请输入文字",
                minLines = 4,
                maxLines = 4,
                required = true,
                additionInfo = "辅助说明",
                variant = FieldVariant.SECONDARY,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ExampleSection(
            title = "错误状态",
            description = "内容为空时 error 显示错误提示"
        ) {
            Textarea(
                value = errorText,
                onValueChange = { errorText = it },
                label = "反馈内容",
                placeholder = "请输入反馈内容",
                minLines = 4,
                maxLines = 4,
                error = if (errorText.isBlank()) "反馈内容不能为空" else null,
                variant = FieldVariant.SECONDARY,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ExampleSection(
            title = "禁用状态",
            description = "enabled = false"
        ) {
            Textarea(
                value = "",
                onValueChange = {},
                label = "标签文字",
                placeholder = "不可编辑文字",
                minLines = 4,
                maxLines = 4,
                enabled = false,
                variant = FieldVariant.SECONDARY,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ExampleSection(
            title = "横排标签",
            description = "TextareaLayout.HORIZONTAL，标签与输入框同一行"
        ) {
            Textarea(
                value = horizontalText,
                onValueChange = { horizontalText = it },
                label = "标签文字",
                placeholder = "请输入文字",
                minLines = 4,
                maxLines = 4,
                maxLength = MAX_LENGTH,
                indicator = true,
                layout = TextareaLayout.HORIZONTAL,
                variant = FieldVariant.SECONDARY,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ExampleSection(
            title = "页面背景上的默认样式",
            description = "FieldVariant.PRIMARY：直接放在页面背景上时用带阴影的默认样式",
            surface = SectionSurface.Plain
        ) {
            Textarea(
                value = pageText,
                onValueChange = { pageText = it },
                label = "标签文字",
                placeholder = "请输入文字",
                minLines = 4,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
