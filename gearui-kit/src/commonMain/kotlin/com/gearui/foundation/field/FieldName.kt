package com.gearui.foundation.field

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.extension.placeHolder
import com.tencent.kuikly.compose.extension.setProp
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * Names a native text field for screen readers.
 *
 * The kit draws a field's label and placeholder itself, beside the native input rather
 * than through it, so the input has no name of its own and VoiceOver reads a bare
 * "text field". Fields pass their label, or the placeholder when there is none.
 *
 * The name goes into the native placeholder, drawn transparent — the kit's own placeholder
 * stays the visible one. A `contentDescription` cannot do this: Kuikly puts it on the
 * wrapper view, which becomes a static-text element and hides the input inside it.
 */
internal fun Modifier.fieldName(name: String?): Modifier =
    if (name.isNullOrBlank()) this else placeHolder(name, Color.Transparent)

/**
 * [fieldName] for a multi-line field. On iOS the native placeholder of a multi-line field
 * is a separate text view, so it names nothing and VoiceOver read the input as an
 * unnamed text view; there the name is also set as the input's own accessibility label
 * (the input is the UITextView itself, so nothing is hidden). Elsewhere the placeholder
 * already names it, and a content description would replace the typed text in TalkBack.
 */
@Composable
internal fun Modifier.multiLineFieldName(name: String?): Modifier {
    val named = fieldName(name)
    return if (name.isNullOrBlank() || !LocalConfiguration.current.isIOS) named else named.setProp("accessibility", name)
}
