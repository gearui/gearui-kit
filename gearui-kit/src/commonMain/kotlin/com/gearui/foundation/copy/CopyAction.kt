package com.gearui.foundation.copy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.icon.*
import com.gearui.foundation.interaction.IconActionButton
import com.gearui.i18n.I18n
import com.gearui.theme.Theme
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.typography.IconSizes
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.Modifier
import kotlinx.coroutines.delay

/** How long "Copied" stays before the copy action returns. */
internal const val COPIED_FEEDBACK_MILLIS = 2_000L

/**
 * A copy button that confirms itself: after a tap it shows a check and is named
 * "Copied" for [COPIED_FEEDBACK_MILLIS]. Kuikly has no clipboard, so the host's
 * [onCopy] does the copying, as with Snippet.
 */
@Composable
internal fun CopyAction(text: String, onCopy: (String) -> Unit, modifier: Modifier = Modifier) {
    val actions = I18n.actions
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(COPIED_FEEDBACK_MILLIS)
            copied = false
        }
    }
    IconActionButton(
        icon = if (copied) Icons.check else Icons.copy,
        contentDescription = if (copied) actions.copied else actions.copy,
        onClick = {
            onCopy(text)
            copied = true
        },
        size = ControlGeometry.closeButtonSize,
        iconSize = IconSizes.Default.md,
        background = Color.Transparent,
        tint = if (copied) Theme.colors.success else Theme.colors.mutedForeground,
        modifier = modifier,
    )
}
