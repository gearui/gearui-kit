package com.gearui.sample.examples.dialog

import com.gearui.components.input.Input
import com.gearui.components.dialog.DialogActionLayout
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.gearui.components.button.Button
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.dialog.AlertDialog
import com.gearui.components.dialog.ConfirmDialog
import com.gearui.components.dialog.Dialog
import com.gearui.components.dialog.DialogAction
import com.gearui.components.dialog.DialogActionRole
import com.gearui.components.dialog.DialogContent
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.sample.pages.LocalSettingsState
import com.gearui.sample.pages.ThemeStyle
import com.gearui.sample.pages.BrandAccent
import com.gearui.theme.Theme

private class DialogRow(val title: String, val description: String? = null, val onClick: () -> Unit)

/**
 * Dialog: a modal that asks for a decision or shows something that must be read.
 */
@Composable
fun DialogExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val settings = LocalSettingsState.current
    var contentVersion by remember { mutableStateOf(0) }

    // Visibility state of each dialog
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var showConfirmNoTitle by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var showDangerDialog by remember { mutableStateOf(false) }
    var showThreeWay by remember { mutableStateOf(false) }
    var showFormDialog by remember { mutableStateOf(false) }
    var inputValue by remember { mutableStateOf("") }

    // Result message
    var resultText by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // Feedback dialogs: confirm, no title, alert, destructive
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "反馈对话框",
            description = "点击任一行打开对应的对话框"
        ) {
            DialogRows(
                listOf(
                    DialogRow("带标题对话框", "带标题和内容的确认对话框") { showConfirmDialog = true },
                    DialogRow("无标题对话框", "仅有内容的简洁对话框") { showConfirmNoTitle = true },
                    DialogRow("警告对话框", "单按钮提示对话框") { showAlertDialog = true },
                    DialogRow("删除确认", "destructive = true，需要用户确认的危险操作") { showDangerDialog = true },
                )
            )

            if (resultText.isNotEmpty()) {
                Text(
                    text = resultText,
                    style = Theme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }

            ConfirmDialog(
                visible = showConfirmDialog,
                title = "对话框标题",
                message = "告知当前状态、信息和解决方法，描述文字尽量控制在三行内。",
                confirmText = "确认",
                cancelText = "取消",
                onConfirm = {
                    resultText = "点击了确认"
                    showConfirmDialog = false
                },
                onCancel = {
                    resultText = "点击了取消"
                    showConfirmDialog = false
                }
            )

            ConfirmDialog(
                visible = showConfirmNoTitle,
                title = "",
                message = "告知当前状态、信息和解决方法，描述文字尽量控制在三行内。",
                confirmText = "知道了",
                cancelText = "取消",
                onConfirm = { showConfirmNoTitle = false },
                onCancel = { showConfirmNoTitle = false }
            )

            AlertDialog(
                visible = showAlertDialog,
                title = "警告",
                message = "此操作不可逆，请谨慎操作。",
                buttonText = "我知道了",
                onConfirm = { showAlertDialog = false }
            )

            ConfirmDialog(
                visible = showDangerDialog,
                title = "确认删除",
                message = "删除后数据将无法恢复，确定要删除吗？",
                confirmText = "删除",
                cancelText = "取消",
                onConfirm = {
                    resultText = "已删除"
                    showDangerDialog = false
                },
                onCancel = { showDangerDialog = false },
                destructive = true,
            )
        }

        // Action layouts and custom content
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "动作与内容",
            description = "动作排列方式与自定义 content"
        ) {
            DialogRows(
                listOf(
                    // Three choices: the policy stacks them, cancel last.
                    DialogRow("三个动作", "三个及以上动作纵向通栏排列，取消永远在最后") { showThreeWay = true },
                    // A form in a dialog: the buttons only close it, so the caller names the
                    // reference's trailing row — the one layout the policy never picks itself.
                    DialogRow("表单对话框", "DialogActionLayout.TRAILING：按钮右对齐紧凑排列") { showFormDialog = true },
                    DialogRow("自定义对话框", "content 放任意组件，打开时内容和主题可实时更新") { showCustomDialog = true },
                )
            )

            Dialog.Host(
                visible = showThreeWay,
                dismissOnOutside = true,
                onDismiss = { showThreeWay = false }
            ) {
                DialogContent(
                    title = "保存更改？",
                    message = "你有尚未保存的修改。",
                    actions = listOf(
                        DialogAction(
                            text = "保存",
                            role = DialogActionRole.PRIMARY,
                            onClick = { resultText = "已保存"; showThreeWay = false },
                        ),
                        DialogAction(
                            text = "不保存",
                            onClick = { resultText = "未保存"; showThreeWay = false },
                        ),
                        DialogAction(
                            text = "取消",
                            role = DialogActionRole.CANCEL,
                            onClick = { showThreeWay = false },
                        ),
                    ),
                )
            }

            Dialog.Host(
                visible = showFormDialog,
                dismissOnOutside = true,
                onDismiss = { showFormDialog = false }
            ) {
                DialogContent(
                    title = "重命名",
                    content = {
                        Input(
                            value = inputValue,
                            onValueChange = { inputValue = it },
                            placeholder = "新名称",
                        )
                    },
                    actionLayout = DialogActionLayout.TRAILING,
                    actions = listOf(
                        DialogAction(
                            text = "取消",
                            role = DialogActionRole.CANCEL,
                            onClick = { showFormDialog = false },
                        ),
                        DialogAction(
                            text = "确定",
                            role = DialogActionRole.PRIMARY,
                            onClick = { resultText = "已重命名"; showFormDialog = false },
                        ),
                    ),
                )
            }

            Dialog.Host(
                visible = showCustomDialog,
                onDismiss = { showCustomDialog = false }
            ) {
                DialogContent(
                    title = "自定义内容",
                    content = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.md)
                        ) {
                            Text(
                                text = "内容版本：$contentVersion",
                                style = Theme.typography.bodyMedium,
                                color = colors.mutedForeground
                            )
                            Button(text = "更新内容", block = true, onClick = { contentVersion++ })
                            Button(text = "切换明暗", block = true, onClick = {
                                settings.themeStyle = if (settings.themeStyle == ThemeStyle.DARK)
                                    ThemeStyle.LIGHT else ThemeStyle.DARK
                            })
                            Button(text = "切换关键色", block = true, onClick = {
                                settings.brandAccent = if (settings.brandAccent == BrandAccent.GREEN)
                                    BrandAccent.DEFAULT else BrandAccent.GREEN
                            })
                        }
                    },
                    actions = listOf(
                        DialogAction(
                            text = "关闭",
                            role = DialogActionRole.PRIMARY,
                            onClick = { showCustomDialog = false },
                        ),
                    ),
                )
            }
        }
    }
}

// One arrow row per trigger (non-anchored overlay entries, see COMPONENT_SPEC §6).
@Composable
private fun DialogRows(rows: List<DialogRow>) {
    CellGroup(items = rows) { row ->
        Cell(
            title = row.title,
            description = row.description,
            arrow = true,
            onClick = row.onClick
        )
    }
}
