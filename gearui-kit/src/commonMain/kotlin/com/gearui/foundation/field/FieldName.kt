package com.gearui.foundation.field

import com.tencent.kuikly.compose.extension.placeHolder
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
