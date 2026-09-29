package com.gearui.components.checkbox

import androidx.compose.runtime.Composable
import com.gearui.components.link.LinkedTextFlow
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * The consent line under a sign-up or login button: a checkbox and "I have read and
 * agree to the 《Terms》 and the 《Privacy Policy》", each document a link.
 *
 * China's Personal Information Protection Law and the app-store reviews built on it
 * (Huawei, Xiaomi, OPPO, vivo) require the box to start **unchecked** and consent to be
 * given by the user, so there is no default here: the caller holds [checked], and should
 * refuse to proceed — typically with a Toast — while it is false.
 *
 * Tapping a link opens only that document; tapping anywhere else in the sentence toggles
 * the box, as consent lines in Chinese apps do. The sentence reacts through a tap
 * gesture rather than a clickable, so it does not swallow the checkbox for a screen
 * reader; the checkbox carries the whole sentence as its name.
 *
 * @param text the full sentence, from the app's language pack.
 * @param links phrases of [text] and what tapping each does; see [LinkedText].
 */
@Composable
fun AgreementCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
    links: Map<String, () -> Unit>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val style = Theme.typography.bodySmall
    // The checkbox keeps its 44pt touch target; the sentence starts level with the mark,
    // so a two-line consent reads as one block beside it.
    val firstLineInset = ((ControlGeometry.selectionTouchTarget.value - style.lineHeight.value) / 2)
        .coerceAtLeast(0f).dp
    Row(
        modifier = modifier.alpha(if (enabled) 1f else FeedbackDefaults.disabledOpacity),
        verticalAlignment = Alignment.Top,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            size = CheckboxSize.SMALL,
            contentDescription = text,
        )
        // The checkbox already carries the sentence; the text adds only its links.
        LinkedTextFlow(
            text = text,
            links = links,
            modifier = Modifier.padding(top = firstLineInset),
            style = style,
            color = Theme.colors.mutedForeground,
            linkColor = Theme.colors.primary,
            onTextClick = { if (enabled) onCheckedChange(!checked) },
            readSentence = false,
        )
    }
}
