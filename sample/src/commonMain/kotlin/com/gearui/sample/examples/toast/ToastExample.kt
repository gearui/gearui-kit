package com.gearui.sample.examples.toast

import androidx.compose.runtime.Composable
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.toast.Toast
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface

private class ToastRow(val title: String, val description: String? = null, val onClick: () -> Unit)

/**
 * Toast: a brief, non-blocking message that goes away on its own.
 */
@Composable
fun ToastExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "纯文字",
            description = "Toast.show()，长文本自动换行"
        ) {
            ToastRows(
                listOf(
                    ToastRow("纯文字提示") { Toast.show("这是一条提示消息") },
                    ToastRow("长文本提示") { Toast.show("这是一条较长的提示消息，用于测试多行文本的显示效果") },
                )
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "状态",
            description = "Toast.success()、Toast.error()、Toast.warning() 带状态图标"
        ) {
            ToastRows(
                listOf(
                    ToastRow("成功提示", "Toast.success()") { Toast.success("操作成功") },
                    ToastRow("错误提示", "Toast.error()") { Toast.error("网络连接失败，请检查网络") },
                    ToastRow("警告提示", "Toast.warning()") { Toast.warning("账户余额不足") },
                )
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "显示时长",
            description = "duration 控制停留时间，默认 2 秒"
        ) {
            ToastRows(
                listOf(
                    ToastRow("1 秒") { Toast.show("1 秒后消失", duration = 1000L) },
                    ToastRow("4 秒") { Toast.show("4 秒后消失", duration = 4000L) },
                )
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "连续提示",
            description = "多个 Toast 按顺序排队显示"
        ) {
            ToastRows(
                listOf(
                    ToastRow("连续 3 条提示") {
                        Toast.show("第一条提示")
                        Toast.success("第二条提示")
                        Toast.warning("第三条提示")
                    },
                )
            )
        }
    }
}

// One arrow row per trigger (non-anchored overlay entries, see COMPONENT_SPEC §6).
@Composable
private fun ToastRows(rows: List<ToastRow>) {
    CellGroup(items = rows) { row ->
        Cell(
            title = row.title,
            description = row.description,
            arrow = true,
            onClick = row.onClick
        )
    }
}
