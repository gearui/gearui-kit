package com.gearui.sample.examples.dialog

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.dialog.AlertDialog
import com.gearui.components.dialog.ConfirmDialog
import com.gearui.components.dialog.Dialog
import com.gearui.components.dialog.DialogContent
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/**
 * Dialog 对话框组件示例
 *
 */
@Composable
fun DialogExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 各种对话框的显示状态
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var showConfirmNoTitle by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var showDangerDialog by remember { mutableStateOf(false) }

    // 操作结果提示
    var resultText by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 反馈对话框 - 带标题
        ExampleSection(
            title = "反馈对话框",
            description = "带标题和内容的确认对话框"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    text = "带标题对话框",
                    onClick = { showConfirmDialog = true },
                    size = ButtonSize.MEDIUM
                )

                if (resultText.isNotEmpty()) {
                    Text(
                        text = resultText,
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
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
        }

        // 反馈对话框 - 无标题
        ExampleSection(
            title = "无标题对话框",
            description = "仅有内容的简洁对话框"
        ) {
            Button(
                text = "无标题对话框",
                onClick = { showConfirmNoTitle = true },
                size = ButtonSize.MEDIUM
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
        }

        // 警告对话框
        ExampleSection(
            title = "警告对话框",
            description = "单按钮提示对话框"
        ) {
            Button(
                text = "警告对话框",
                onClick = { showAlertDialog = true },
                size = ButtonSize.MEDIUM
            )

            AlertDialog(
                visible = showAlertDialog,
                title = "警告",
                message = "此操作不可逆，请谨慎操作。",
                buttonText = "我知道了",
                onConfirm = { showAlertDialog = false }
            )
        }

        // 危险操作确认
        ExampleSection(
            title = "危险操作",
            description = "需要用户确认的危险操作"
        ) {
            Button(
                text = "删除确认",
                onClick = { showDangerDialog = true },
                size = ButtonSize.MEDIUM,
                theme = ButtonTheme.DANGER
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
                onCancel = { showDangerDialog = false }
            )
        }

        // 自定义内容对话框
        ExampleSection(
            title = "自定义内容",
            description = "支持自定义对话框内容"
        ) {
            Button(
                text = "自定义对话框",
                onClick = { showCustomDialog = true },
                size = ButtonSize.MEDIUM
            )

            Dialog.Host(
                visible = showCustomDialog,
                onDismiss = { showCustomDialog = false }
            ) {
                DialogContent(
                    title = "自定义内容",
                    content = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "这里可以放置任意自定义内容",
                                style = Typography.BodyMedium,
                                color = colors.textSecondary
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                repeat(3) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .padding(4.dp)
                                    ) {
                                        Text(
                                            text = "项目${index + 1}",
                                            style = Typography.BodySmall,
                                            color = colors.textPrimary
                                        )
                                    }
                                }
                            }
                        }
                    },
                    actions = {
                        Button(
                            text = "关闭",
                            onClick = { showCustomDialog = false },
                            size = ButtonSize.SMALL
                        )
                    }
                )
            }
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Dialog 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. ConfirmDialog: 带确认/取消的对话框",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. AlertDialog: 单按钮警告对话框",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. Dialog.Host: 自定义内容对话框",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. 模态遮罩，点击外部可关闭",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
