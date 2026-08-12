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
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing

/**
 * ConfirmDialog - confirmation dialog
 *
 * The standard dialog with confirm and cancel buttons
 */
@Composable
fun ConfirmDialog(
    visible: Boolean,
    title: String,
    message: String,
    confirmText: String = I18n.strings.common.confirm,
    cancelText: String = I18n.strings.common.cancel,
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
                // Cancel button - text style
                Box(
                    modifier = Modifier
                        .clickable { onCancel() }
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cancelText,
                        style = Typography.BodyMedium,
                        color = colors.mutedForeground
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.sm))
                // Confirm button
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
 * AlertDialog - alert dialog
 *
 * A single-button message dialog
 */
@Composable
fun AlertDialog(
    visible: Boolean,
    title: String,
    message: String,
    buttonText: String = I18n.strings.common.ok,
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
