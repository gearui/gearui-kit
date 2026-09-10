package com.gearui.sample.examples.cellgroup

import androidx.compose.runtime.Composable
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.icon.Icons
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.gearui.unit.dp

/**
 * CellGroup component examples
 */
@Composable
fun CellGroupExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "基础用法",
            description = "分组标题与行文字左对齐；行间有分隔线，最后一行没有"
        ) {
            CellGroup(
                items = listOf("通用", "隐私", "关于"),
                title = "设置",
            ) { name ->
                Cell(title = name, arrow = true, onClick = { Toast.show(name) })
            }
        }

        ExampleSection(
            title = "标题尾部插槽",
            description = "计数或操作放在标题行的尾端，仍与行内容对齐"
        ) {
            val items = listOf("图片", "视频", "文件")
            CellGroup(
                items = items,
                title = "附件",
                titleTrailing = {
                    Text(
                        text = "${items.size} 项",
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground,
                    )
                },
            ) { name ->
                Cell(title = name, arrow = true)
            }
        }

        ExampleSection(
            title = "带前置元素时的缩进",
            description = "分隔线要对齐文字，而不是卡片边缘。分组量不到子节点，所以由调用方传入更大的缩进"
        ) {
            CellGroup(
                items = listOf("消息通知", "声音", "免打扰"),
                title = "提醒",
                // 16dp row padding + a 20dp icon + a 12dp gap lands exactly on
                // where the text starts.
                separatorInset = 48.dp,
            ) { name ->
                Cell(
                    title = name,
                    arrow = true,
                    leading = {
                        Icon(
                            name = Icons.bell,
                            size = IconSizes.Default.md,
                            tint = colors.mutedForeground,
                        )
                    },
                )
            }
        }

        ExampleSection(
            title = "单行分组",
            description = "只有一行时不画任何分隔线——它没有需要被分开的对象"
        ) {
            CellGroup(items = listOf("退出登录")) { name ->
                Cell(title = name, titleColor = colors.destructive)
            }
        }
    }
}
