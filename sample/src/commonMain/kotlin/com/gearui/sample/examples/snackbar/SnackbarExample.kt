package com.gearui.sample.examples.snackbar

import androidx.compose.runtime.*
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.snackbar.*
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface

private const val CloseableSnackbarMillis = 30_000L // long enough to try the close button
private const val ActionSnackbarMillis = 5_000L
private const val FullSnackbarMillis = 10_000L

private class SnackbarRow(val title: String, val description: String? = null, val onClick: () -> Unit)

/**
 * Snackbar component examples
 *
 * - the message drops in from the top (80dp below the top by default)
 * - four states: normal / success / warning / error
 * - supports an icon, a close button and an action button
 */
@Composable
fun SnackbarExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    // Snackbar controller backed by the Overlay system
    val snackbarController = rememberSnackbarController()

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // Content variants: text only, icon, close button, action button, all combined
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "内容",
            description = "点击任一行弹出对应配置的 Snackbar"
        ) {
            SnackbarRows(
                listOf(
                    SnackbarRow("纯文字的通知", "最基本的消息提示，不带图标") {
                        snackbarController.show(
                            message = "这是一条普通的通知信息",
                            type = SnackbarType.INFO,
                            showIcon = false
                        )
                    },
                    SnackbarRow("带图标的通知", "带有状态图标的消息提示") {
                        snackbarController.show(
                            message = "这是一条普通的通知信息",
                            type = SnackbarType.INFO,
                            showIcon = true
                        )
                    },
                    SnackbarRow("带关闭的通知", "可手动关闭的消息提示") {
                        snackbarController.show(
                            message = "这是一条普通的通知信息",
                            type = SnackbarType.INFO,
                            showIcon = true,
                            showCloseButton = true,
                            duration = CloseableSnackbarMillis
                        )
                    },
                    SnackbarRow("带按钮的通知", "带有操作按钮的消息提示") {
                        snackbarController.show(
                            message = "文件已删除",
                            type = SnackbarType.INFO,
                            showIcon = true,
                            action = "撤销",
                            onActionClick = {
                                // Show a success message after the undo
                                snackbarController.showSuccess("已撤销删除操作")
                            },
                            duration = ActionSnackbarMillis
                        )
                    },
                    SnackbarRow("综合示例", "同时带图标、关闭按钮和操作按钮") {
                        snackbarController.show(
                            message = "新消息已收到",
                            type = SnackbarType.SUCCESS,
                            showIcon = true,
                            showCloseButton = true,
                            action = "查看",
                            onActionClick = {
                                snackbarController.showInfo("正在跳转...")
                            },
                            duration = FullSnackbarMillis
                        )
                    },
                )
            )
        }

        // States: one row per type; tapping them in quick succession shows the
        // controller replacing the current message.
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "通知状态",
            description = "INFO / SUCCESS / WARNING / ERROR 四种状态，连续点击可见切换效果"
        ) {
            SnackbarRows(
                listOf(
                    SnackbarRow("普通", "showInfo") { snackbarController.showInfo("这是一条普通的通知信息") },
                    SnackbarRow("成功", "showSuccess") { snackbarController.showSuccess("操作成功完成") },
                    SnackbarRow("警示", "showWarning") { snackbarController.showWarning("请注意数据安全") },
                    SnackbarRow("错误", "showError") { snackbarController.showError("操作失败，请稍后重试") },
                )
            )
        }
    }
}

// One arrow row per trigger (non-anchored overlay entries, see COMPONENT_SPEC §6).
@Composable
private fun SnackbarRows(rows: List<SnackbarRow>) {
    CellGroup(items = rows) { row ->
        Cell(
            title = row.title,
            description = row.description,
            arrow = true,
            onClick = row.onClick
        )
    }
}
