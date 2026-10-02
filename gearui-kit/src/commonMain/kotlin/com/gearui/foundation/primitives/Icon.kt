package com.gearui.foundation.primitives

import com.gearui.components.icon.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.icon.IconSource
import com.gearui.components.icon.VectorAsset
import com.gearui.components.icon.ImageAsset
import com.gearui.components.icon.vectorPath
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.Canvas
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.coil3.rememberAsyncImagePainter
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.ColorFilter
import com.tencent.kuikly.compose.ui.graphics.painter.Painter
import com.gearui.unit.Dp
import com.gearui.foundation.typography.*

/**
 * Icon - Icon Engine Primitive
 *
 * Equivalent in role to:
 * - Material3: Icon
 * - Ant Design: Icon
 * - Flutter: Icon
 *
 * Design goals:
 * - ✅ size comes from a token
 * - ✅ tint comes from a token
 * - ✅ nothing hardcoded
 *
 * Every component must use this primitive:
 * - Button/Input/Tag/List/NavBar
 *
 * ❌ Never call Image() directly in a component
 * ✅ Always use Icon()
 */
@Composable
fun Icon(
    painter: Painter,
    modifier: Modifier = Modifier,

    /** icon size (from a token) */
    size: Dp = IconSizes.Default.lg,

    /** icon tint (null = original colours) */
    tint: Color? = null
) {
    Image(
        painter = painter,
        contentDescription = "",
        modifier = modifier.size(size),
        colorFilter = tint?.let { ColorFilter.tint(it) }
    )
}


/**
 * Draws an [IconSource]: GearUI's [Icons], a third-party pack or an app's own.
 *
 * ```kotlin
 * Icon(Icons.heart)
 * Icon(Icons.heart, fill = liked, tint = Theme.colors.destructive)
 * ```
 *
 * @param fill the icon's fill form; an icon without one draws its regular form.
 * @param tint the colour; the theme's foreground by default, so an icon reads in light
 *   and dark alike. [Color.Unspecified] keeps an image icon's own colours (a logo).
 */
@Composable
fun Icon(
    icon: IconSource,
    modifier: Modifier = Modifier,
    size: Dp = IconSizes.Default.lg,
    tint: Color = Theme.colors.foreground,
    fill: Boolean = false,
) {
    when (val asset = icon.resolve(fill)) {
        is VectorAsset -> {
            val px = with(LocalDensity.current) { size.toPx() }
            val path = remember(asset.path, asset.viewport, px) { vectorPath(asset.path, asset.viewport, px) }
            val color = if (tint == Color.Unspecified) Theme.colors.foreground else tint
            Canvas(modifier.size(size)) { drawPath(path, color) }
        }
        is ImageAsset -> Image(
            painter = rememberAsyncImagePainter(model = asset.url),
            contentDescription = null,
            modifier = modifier.size(size),
            colorFilter = if (tint == Color.Unspecified) null else ColorFilter.tint(tint),
        )
    }
}
