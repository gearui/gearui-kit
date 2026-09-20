package com.gearui.sample.examples.list

import androidx.compose.runtime.Composable
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.icon.Icons
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.List
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.scroll.ListTokens
import com.gearui.foundation.typography.IconSizes
import com.gearui.primitives.composite.ListItem
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp

private data class Entry(val title: String, val icon: String, val value: String? = null)

private val SETTINGS = listOf(
    Entry("消息", Icons.chat_circle, "3 条未读"),
    Entry("通知", Icons.bell, "已开启"),
    Entry("隐私", Icons.lock_simple),
    Entry("关于", Icons.info, "v1.0.0"),
)

@Composable
fun ListExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            useCardContainer = false,
            title = "三者的分工",
            description = "Cell 是一行；CellGroup 是一张卡片的行；List 是会滚动的容器"
        ) {
            CellGroup(
                items = SETTINGS,
                title = "CellGroup:分组卡片",
            ) { entry ->
                Cell(
                    title = entry.title,
                    note = entry.value,
                    arrow = true,
                    leading = {
                        Icon(name = entry.icon, size = IconSizes.Default.xl, tint = colors.mutedForeground)
                    },
                    onClick = { Toast.show(entry.title) },
                )
            }
        }

        ExampleSection(
            useCardContainer = false,
            title = "ListItem",
            description = "同一个 Cell 的语义封装:标题 / 副标题 / 右侧值，箭头自动"
        ) {
            CellGroup(items = listOf("账号与安全", "新消息通知")) { title ->
                if (title == "账号与安全") {
                    ListItem(title = title, onClick = { Toast.show(title) })
                } else {
                    ListItem(title = title, subtitle = "接收新消息提醒", value = "开")
                }
            }
        }

        ExampleSection(
            title = "List:滚动容器",
            description = "ListTokens.Settings 时行之间有分隔线，最后一行没有"
        ) {
            List(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(Theme.shapes.xl)
                    .background(colors.surface),
                tokens = ListTokens.Settings,
            ) {
                section(header = {
                    Text(
                        text = "常用",
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }) {
                    items(3) { index ->
                        Cell(title = "常用项 ${index + 1}", arrow = true, onClick = { Toast.show("常用项 ${index + 1}") })
                    }
                }
                section(header = {
                    Text(
                        text = "更多",
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }) {
                    items(6) { index ->
                        Cell(title = "更多项 ${index + 1}", arrow = true, onClick = { Toast.show("更多项 ${index + 1}") })
                    }
                }
            }
        }
    }
}
