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
 * Snackbar 组件示例
 *
 * - 消息从顶部弹出（默认距顶部 80dp）
 * - 支持四种状态：普通/成功/警告/错误
 * - 支持带图标、带关闭按钮、带操作按钮
 */
@Composable
fun SnackbarExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 使用 Overlay 系统的 Snackbar 控制器
    val snackbarController = rememberSnackbarController()

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ========== 组件类型 ==========

        // 纯文字的通知
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

        // 带图标的通知
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

        // 带关闭的通知
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

        // 带按钮的通知
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
                            // 点击撤销后显示成功提示
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

        // ========== 组件状态 ==========

        // 普通通知
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

        // 成功通知
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

        // 警示通知
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

        // 错误通知
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

        // ========== 综合示例 ==========

        // 带关闭和操作按钮
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

        // 快速切换测试
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
