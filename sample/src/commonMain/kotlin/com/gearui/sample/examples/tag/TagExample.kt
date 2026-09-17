package com.gearui.sample.examples.tag

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.components.tag.*
import com.gearui.components.switch.Switch
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.layout.Spacing
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection

/** Real Tag controls, including re-enabling and close/click rejection. */
@Composable
fun TagExample(component: ComponentInfo, onBack: () -> Unit) {
    var disabled by remember { mutableStateOf(false) }
    var clicks by remember { mutableStateOf(0) }
    var closes by remember { mutableStateOf(0) }
    ExamplePage(component = component, onBack = onBack) {
        listOf(TagVariant.DARK to "实色标签", TagVariant.LIGHT to "柔和标签", TagVariant.OUTLINE to "描边标签")
            .forEach { (variant, title) ->
                ExampleSection(title = title, useCardContainer = false) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        listOf(TagTheme.DEFAULT to "默认", TagTheme.PRIMARY to "主要", TagTheme.SUCCESS to "成功",
                            TagTheme.WARNING to "警告", TagTheme.DANGER to "危险").forEach { (theme, label) ->
                            Tag(text = label, theme = theme, variant = variant)
                        }
                    }
                }
            }
        ExampleSection(title = "标签尺寸", useCardContainer = false) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Tag("小尺寸", size = TagSize.SMALL, theme = TagTheme.PRIMARY)
                Tag("中尺寸", size = TagSize.MEDIUM, theme = TagTheme.PRIMARY)
                Tag("大尺寸", size = TagSize.LARGE, theme = TagTheme.PRIMARY)
            }
        }
        ExampleSection(title = "交互与禁用", useCardContainer = false) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    Text("禁用")
                    Switch(checked = disabled, onCheckedChange = { disabled = it })
                }
                Tag("点击或关闭", theme = TagTheme.PRIMARY, closable = true, disabled = disabled,
                    onClick = { clicks++ }, onClose = { closes++ })
                Text("点击 $clicks 次 · 关闭 $closes 次")
            }
        }
    }
}
