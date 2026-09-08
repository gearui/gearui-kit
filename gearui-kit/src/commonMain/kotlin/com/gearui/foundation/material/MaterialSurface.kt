package com.gearui.foundation.material

import androidx.compose.runtime.Composable
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.extension.MakeKuiklyComposeNode
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.graphics.RectangleShape
import com.tencent.kuikly.core.views.BlurView

/**
 * A surface that blurs what is behind it, or a flat one where it cannot.
 *
 * Degradation is the whole design, so it is stated once here: **when the blur
 * does not run, the translucency goes with it.** The fallback is
 * `Theme.colors.surface` at full opacity, not the same tint without the blur.
 *
 * A tint is calibrated against blurred, low-frequency backdrop. Over raw
 * content it stops being a material and becomes a wash: body text sits on top
 * of whatever happened to scroll underneath, and contrast depends on the
 * user's data. Keeping the tint "so it still looks a bit glassy" is the
 * tempting version of this fallback and the one that ships unreadable screens.
 *
 * Which platforms blur, and why, is [isMaterialBlurEnabled].
 *
 * The blur is a KuiklyUI core `BlurView` rather than a modifier because the
 * compose layer has no `Modifier.blur` — `RenderNodeLayer` carries a
 * `renderEffect` field with a `// todo` where it would be applied. It is
 * mounted as a sibling behind [content] rather than as its parent, since
 * `BlurView` is a leaf view and cannot host children.
 *
 * @param material which surface this is; see [Materials].
 * @param shape clipped shape. Clip the blur, not just the content, or the blur
 *   squares off the corners of a rounded sheet.
 */
@Composable
fun MaterialSurface(
    material: Material,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    content: @Composable () -> Unit,
) {
    val colors = Theme.colors
    val blurred = isMaterialBlurEnabled()

    Box(modifier = modifier.clip(shape)) {
        if (blurred) {
            MakeKuiklyComposeNode<BlurView>(
                factory = { BlurView() },
                modifier = Modifier.fillMaxSize(),
                viewInit = { getViewAttr().blurRadius(material.blurRadius) },
                viewUpdate = { it.getViewAttr().blurRadius(material.blurRadius) },
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.surface.copy(alpha = material.tintAlpha))
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.surface)
            )
        }
        content()
    }
}
