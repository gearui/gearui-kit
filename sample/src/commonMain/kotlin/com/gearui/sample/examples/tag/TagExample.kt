package com.gearui.sample.examples.tag

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.gearui.components.tag.*
import com.gearui.components.switch.Switch
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.layout.Spacing
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/** Real Tag controls, including re-enabling and close/click rejection. */
@Composable
fun TagExample(component: ComponentInfo, onBack: () -> Unit) {
    var disabled by remember { mutableStateOf(false) }
    var clicks by remember { mutableStateOf(0) }
    var closes by remember { mutableStateOf(0) }
    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(title = "样式与主题", description = "实色、柔和、描边三种样式，各含五种主题") {
            listOf(TagVariant.DARK, TagVariant.LIGHT, TagVariant.OUTLINE).forEach { variant ->
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    listOf(TagTheme.DEFAULT to "默认", TagTheme.PRIMARY to "主要", TagTheme.SUCCESS to "成功",
                        TagTheme.WARNING to "警告", TagTheme.DANGER to "危险").forEach { (theme, label) ->
                        Tag(text = label, theme = theme, variant = variant)
                    }
                }
            }
        }

        ExampleSection(title = "标签尺寸", description = "SMALL / MEDIUM / LARGE") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Tag("小尺寸", size = TagSize.SMALL, theme = TagTheme.PRIMARY)
                Tag("中尺寸", size = TagSize.MEDIUM, theme = TagTheme.PRIMARY)
                Tag("大尺寸", size = TagSize.LARGE, theme = TagTheme.PRIMARY)
            }
        }

        ExampleSection(title = "交互与禁用", description = "禁用后点击与关闭都不再响应") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubLabel("禁用")
                Switch(checked = disabled, onCheckedChange = { disabled = it })
            }
            Tag("点击或关闭", theme = TagTheme.PRIMARY, closable = true, disabled = disabled,
                onClick = { clicks++ }, onClose = { closes++ })
            SubLabel("点击 $clicks 次 · 关闭 $closes 次")
        }

        ExampleSection(title = "TagGroup", description = "可选择、可换行、可移除的标签集合") {
            var single by remember { mutableStateOf(setOf("news")) }
            var multiple by remember { mutableStateOf(setOf("travel", "food")) }
            var removable by remember {
                mutableStateOf(listOf("Kotlin", "Compose", "Kuikly", "HeroUI").map { TagGroupItem(it, it) })
            }
            SubLabel("单选")
            TagGroup(
                items = listOf("news" to "新闻", "travel" to "旅行", "food" to "美食", "tech" to "科技").map {
                    TagGroupItem(it.first, it.second)
                },
                selectedKeys = single,
                onSelectionChange = { single = it },
            )
            SubLabel("多选 · 大尺寸")
            TagGroup(
                items = listOf("news" to "新闻", "travel" to "旅行", "food" to "美食", "tech" to "科技", "sport" to "运动").map {
                    TagGroupItem(it.first, it.second)
                },
                selectedKeys = multiple,
                onSelectionChange = { multiple = it },
                selectionMode = TagGroupSelectionMode.MULTIPLE,
                size = TagGroupSize.LARGE,
            )
            SubLabel("可移除")
            TagGroup(
                items = removable,
                selectedKeys = emptySet(),
                onSelectionChange = {},
                selectionMode = TagGroupSelectionMode.NONE,
                size = TagGroupSize.SMALL,
                onRemove = { key -> removable = removable.filterNot { it.key == key } },
            )
        }
    }
}

@Composable
private fun SubLabel(text: String) {
    Text(text = text, style = Theme.typography.bodySmall, color = Theme.colors.mutedForeground)
}
