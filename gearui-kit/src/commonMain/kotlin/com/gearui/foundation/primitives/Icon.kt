package com.gearui.foundation.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.icon.Icons
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
 * Icon by icon name.
 *
 * Usage:
 * - Icon(Icons.home)
 * - Icon(Icons.arrow_back, preferSvg = true)
 */
@Composable
fun Icon(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = IconSizes.Default.lg,
    tint: Color? = null,
    preferSvg: Boolean = false
) {
    var model by remember(name, preferSvg) {
        mutableStateOf(if (preferSvg) Icons.svg(name) else Icons.png(name))
    }
    val painter = rememberAsyncImagePainter(
        model = model,
        onError = {
            if (preferSvg && model == Icons.svg(name)) {
                model = Icons.png(name)
            }
        }
    )
    Image(
        painter = painter,
        contentDescription = "",
        modifier = modifier.size(size),
        colorFilter = tint?.let { ColorFilter.tint(it) }
    )
}
