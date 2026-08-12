package com.gearui.sample.examples.snackbar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.snackbar.*
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

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
    val colors = Theme.colors

    // Snackbar controller backed by the Overlay system
    val snackbarController = rememberSnackbarController()

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ========== Component types ==========

        // Text-only notice
        ExampleSection(
            title = "纯文字的通知",
            description = "最基本的消息提示，不带图标"
        ) {
            Button(
                text = "纯文字的通知",
                onClick = {
                    snackbarController.show(
                        message = "这是一条普通的通知信息",
                        type = SnackbarType.INFO,
                        showIcon = false
                    )
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Notice with an icon
        ExampleSection(
            title = "带图标的通知",
            description = "带有状态图标的消息提示"
        ) {
            Button(
                text = "带图标的通知",
                onClick = {
                    snackbarController.show(
                        message = "这是一条普通的通知信息",
                        type = SnackbarType.INFO,
                        showIcon = true
                    )
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Notice with a close button
        ExampleSection(
            title = "带关闭的通知",
            description = "可手动关闭的消息提示"
        ) {
            Button(
                text = "带关闭的通知",
                onClick = {
                    snackbarController.show(
                        message = "这是一条普通的通知信息",
                        type = SnackbarType.INFO,
                        showIcon = true,
                        showCloseButton = true,
                        duration = 30000L // 长时间显示，方便测试关闭按钮
                    )
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Notice with a button
        ExampleSection(
            title = "带按钮的通知",
            description = "带有操作按钮的消息提示"
        ) {
            Button(
                text = "带按钮的通知",
                onClick = {
                    snackbarController.show(
                        message = "文件已删除",
                        type = SnackbarType.INFO,
                        showIcon = true,
                        action = "撤销",
                        onActionClick = {
                            // Show a success message after the undo
                            snackbarController.showSuccess("已撤销删除操作")
                        },
                        duration = 5000L
                    )
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ========== Component states ==========

        // Normal notice
        ExampleSection(
            title = "普通通知",
            description = "普通信息提示"
        ) {
            Button(
                text = "普通通知",
                onClick = {
                    snackbarController.showInfo("这是一条普通的通知信息")
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Success notice
        ExampleSection(
            title = "成功通知",
            description = "操作成功时的消息提示"
        ) {
            Button(
                text = "成功通知",
                onClick = {
                    snackbarController.showSuccess("操作成功完成")
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Warning notice
        ExampleSection(
            title = "警示通知",
            description = "警告信息的消息提示"
        ) {
            Button(
                text = "警示通知",
                onClick = {
                    snackbarController.showWarning("请注意数据安全")
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Error notice
        ExampleSection(
            title = "错误通知",
            description = "错误信息的消息提示"
        ) {
            Button(
                text = "错误通知",
                onClick = {
                    snackbarController.showError("操作失败，请稍后重试")
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ========== Combined example ==========

        // With a close button and an action button
        ExampleSection(
            title = "综合示例",
            description = "同时带图标、关闭按钮和操作按钮"
        ) {
            Button(
                text = "综合示例",
                onClick = {
                    snackbarController.show(
                        message = "新消息已收到",
                        type = SnackbarType.SUCCESS,
                        showIcon = true,
                        showCloseButton = true,
                        action = "查看",
                        onActionClick = {
                            snackbarController.showInfo("正在跳转...")
                        },
                        duration = 10000L
                    )
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Rapid switching test
        ExampleSection(
            title = "快速切换测试",
            description = "快速点击不同按钮测试切换效果"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    text = "信息",
                    onClick = { snackbarController.showInfo("信息提示") },
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    text = "成功",
                    onClick = { snackbarController.showSuccess("成功提示") },
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    text = "警告",
                    onClick = { snackbarController.showWarning("警告提示") },
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    text = "错误",
                    onClick = { snackbarController.showError("错误提示") },
                    size = ButtonSize.SMALL,
                    theme = ButtonTheme.DANGER,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
