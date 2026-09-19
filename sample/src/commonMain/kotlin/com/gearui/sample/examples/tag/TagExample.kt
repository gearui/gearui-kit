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
        ExampleSection(title = "TagGroup", description = "可选择、可换行、可移除的标签集合", useCardContainer = false) {
            var single by remember { mutableStateOf(setOf("news")) }
            var multiple by remember { mutableStateOf(setOf("travel", "food")) }
            var removable by remember {
                mutableStateOf(listOf("Kotlin", "Compose", "Kuikly", "HeroUI").map {
                    com.gearui.components.tag.TagGroupItem(it, it)
                })
            }
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Text("单选")
                com.gearui.components.tag.TagGroup(
                    items = listOf("news" to "新闻", "travel" to "旅行", "food" to "美食", "tech" to "科技").map {
                        com.gearui.components.tag.TagGroupItem(it.first, it.second)
                    },
                    selectedKeys = single,
                    onSelectionChange = { single = it },
                )
                Text("多选 · 大尺寸")
                com.gearui.components.tag.TagGroup(
                    items = listOf("news" to "新闻", "travel" to "旅行", "food" to "美食", "tech" to "科技", "sport" to "运动").map {
                        com.gearui.components.tag.TagGroupItem(it.first, it.second)
                    },
                    selectedKeys = multiple,
                    onSelectionChange = { multiple = it },
                    selectionMode = com.gearui.components.tag.TagGroupSelectionMode.MULTIPLE,
                    size = com.gearui.components.tag.TagGroupSize.LARGE,
                )
                Text("可移除")
                com.gearui.components.tag.TagGroup(
                    items = removable,
                    selectedKeys = emptySet(),
                    onSelectionChange = {},
                    selectionMode = com.gearui.components.tag.TagGroupSelectionMode.NONE,
                    size = com.gearui.components.tag.TagGroupSize.SMALL,
                    onRemove = { key -> removable = removable.filterNot { it.key == key } },
                )
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
