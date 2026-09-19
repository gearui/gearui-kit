package com.gearui.foundation.field

import androidx.compose.runtime.Composable
import com.gearui.foundation.material.DecoratedSurface
import com.gearui.foundation.material.surfaceShadowStyles
import com.tencent.kuikly.compose.foundation.layout.BoxScope
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Shape

/**
 * The frame every text field and field trigger sits in.
 *
 * Reference fields (`input.css`, `select.css .select__trigger--variant-default`,
 * `text-area`, `search-field`) draw no border; they separate from the page with
 * the field shadow stack (`--shadow-field`). [shadowed] is false for the secondary
 * (filled) variant used on surfaces. The field's own background, focus ring and
 * content stay with the caller; this only owns the shadow outside the clip.
 */
@Composable
internal fun FieldSurface(
    modifier: Modifier = Modifier,
    shape: Shape = FieldDefaults.shape,
    shadowed: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    DecoratedSurface(
        modifier = modifier,
        shape = shape,
        shadows = if (shadowed) surfaceShadowStyles().field else emptyList(),
        content = content,
    )
}
