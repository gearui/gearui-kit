package com.gearui.foundation.field

import androidx.compose.runtime.Composable
import com.gearui.foundation.material.DecoratedSurface
import com.gearui.foundation.material.surfaceShadowStyles
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.BoxScope
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
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

/**
 * The frame around a field: the decorated surface normally, and nothing at all when the
 * field is embedded in an [com.gearui.components.inputgroup.InputGroup], which draws the
 * frame itself.
 */
@Composable
internal fun FieldFrame(
    embedded: Boolean,
    shape: Shape,
    shadowed: Boolean,
    content: @Composable BoxScope.() -> Unit,
) {
    if (embedded) {
        Box(modifier = Modifier.fillMaxWidth(), content = content)
    } else {
        FieldSurface(modifier = Modifier.fillMaxWidth(), shape = shape, shadowed = shadowed, content = content)
    }
}
