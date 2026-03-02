package com.gearui.components.dialog

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonSize
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * ConfirmDialog - 确认对话框
 *
 * 带确认/取消按钮的标准对话框
 */
@Composable
fun ConfirmDialog(
    visible: Boolean,
    title: String,
    message: String,
    confirmText: String = "确认",
    cancelText: String = "取消",
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit = onCancel,
    dismissOnOutside: Boolean = false
) {
    val colors = Theme.colors

    Dialog.Host(
        visible = visible,
        dismissOnOutside = dismissOnOutside,
        onDismiss = onDismiss
    ) {
        DialogContent(
            title = title,
            message = message,
            actions = {
                // 取消按钮 - 文本样式
                Box(
                    modifier = Modifier
                        .clickable { onCancel() }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cancelText,
                        style = Typography.BodyMedium,
                        color = colors.textSecondary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // 确认按钮
                Button(
                    text = confirmText,
                    onClick = onConfirm,
                    theme = ButtonTheme.PRIMARY,
                    size = ButtonSize.SMALL
                )
            }
        )
    }
}

/**
 * AlertDialog - 警告对话框
 *
 * 单按钮的提示对话框
 */
@Composable
fun AlertDialog(
    visible: Boolean,
    title: String,
    message: String,
    buttonText: String = "确定",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = onConfirm,
    dismissOnOutside: Boolean = false
) {
    Dialog.Host(
        visible = visible,
        dismissOnOutside = dismissOnOutside,
        onDismiss = onDismiss
    ) {
        DialogContent(
            title = title,
            message = message,
            actions = {
                Button(
                    text = buttonText,
                    onClick = onConfirm,
                    theme = ButtonTheme.PRIMARY,
                    size = ButtonSize.SMALL
                )
            }
        )
    }
}
