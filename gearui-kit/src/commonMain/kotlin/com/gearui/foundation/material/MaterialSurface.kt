package com.gearui.foundation.material

import androidx.compose.runtime.Composable
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.extension.MakeKuiklyComposeNode
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.graphics.RectangleShape
import com.tencent.kuikly.core.views.BlurView

/**
 * A surface that blurs what is behind it, or a flat one where it cannot.
 *
 * Degradation is the whole design, so it is stated once here: **when the blur
 * does not run, the translucency goes with it.** The fallback is an opaque
 * colour, not the same tint without the blur.
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
 * @param fallback the opaque colour to paint when the blur does not run.
 *
 *   Defaults to `Theme.colors.surface`, which is right for sheets and popovers.
 *   It is a parameter because it is **not** right for every surface: NavBar
 *   deliberately paints `colors.background`, since in a dark theme `surface`
 *   (#121212) is one step lighter than `background` (#0A0A0A) and draws a
 *   visible band across the top that does not meet the status bar. That was a
 *   fixed bug; a material whose fallback ignored it would put it back.
 *
 *   The *tint* over a running blur stays `Theme.colors.surface` for every
 *   material (§11.2) — a blurred backdrop is low-frequency enough that one tint
 *   works, which is the whole reason the fallback needs its own answer.
 * @param shape clipped shape. Clip the blur, not just the content, or the blur
 *   squares off the corners of a rounded sheet.
 */
@Composable
fun MaterialSurface(
    material: Material,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    // After `shape` so existing positional calls keep binding to the same
    // parameters; inserting it before would have silently re-aimed them.
    fallback: Color? = null,
    content: @Composable () -> Unit,
) {
    val colors = Theme.colors
    val opaque = fallback ?: colors.surface
    val blurred = isMaterialBlurEnabled()

    Box(modifier = modifier.clip(shape)) {
        // matchParentSize, never fillMaxSize. A fillMaxSize child *participates*
        // in the Box's own measurement, so the Box stops wrapping its content and
        // expands to the largest size its constraints allow. Every surface routed
        // through here then grows to fill its parent — a bottom bar became the
        // whole screen, with its row of tabs at the top of it and the page
        // content squeezed out. matchParentSize takes the parent's size without
        // voting on it, which is what a backdrop layer wants.
        if (blurred) {
            MakeKuiklyComposeNode<BlurView>(
                factory = { BlurView() },
                modifier = Modifier.matchParentSize(),
                viewInit = { getViewAttr().blurRadius(material.blurRadius) },
                viewUpdate = { it.getViewAttr().blurRadius(material.blurRadius) },
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(colors.surface.copy(alpha = material.tintAlpha))
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(opaque)
            )
        }
        content()
    }
}
