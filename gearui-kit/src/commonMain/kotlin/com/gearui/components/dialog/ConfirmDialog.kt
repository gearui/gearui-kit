package com.gearui.components.dialog

import androidx.compose.runtime.Composable
import com.gearui.i18n.I18n

/**
 * ConfirmDialog - confirmation dialog
 *
 * Title, message, and two actions drawn as a platform alert. Use
 * [destructive] when confirming means losing something: deleting a friend,
 * leaving a group, revoking a message.
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
    dismissOnOutside: Boolean = false,
    destructive: Boolean = false,
    /** false while an action is in flight: both rows grey out and stop responding. */
    enabled: Boolean = true,
) {
    Dialog.Host(
        visible = visible,
        dismissOnOutside = dismissOnOutside,
        onDismiss = onDismiss
    ) {
        DialogContent(
            title = title,
            message = message,
            actions = listOf(
                DialogAction(
                    text = confirmText,
                    role = if (destructive) DialogActionRole.DESTRUCTIVE else DialogActionRole.PRIMARY,
                    enabled = enabled,
                    onClick = onConfirm,
                ),
                DialogAction(
                    text = cancelText,
                    role = DialogActionRole.CANCEL,
                    enabled = enabled,
                    onClick = onCancel,
                ),
            ),
        )
    }
}

/**
 * AlertDialog - alert dialog
 *
 * A single-button message dialog.
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
            actions = listOf(
                DialogAction(
                    text = buttonText,
                    role = DialogActionRole.PRIMARY,
                    onClick = onConfirm,
                ),
            ),
        )
    }
}
