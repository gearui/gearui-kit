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
 * 地位等价于:
 * - Material3: Icon
 * - Ant Design: Icon
 * - Flutter: Icon
 *
 * 设计目标:
 * - ✅ Size Token 化
 * - ✅ Tint Token 化
 * - ✅ 零硬编码
 *
 * 所有组件必须使用此 Primitive:
 * - Button/Input/Tag/List/NavBar
 *
 * ❌ 禁止在组件中直接使用 Image()
 * ✅ 强制使用 Icon()
 */
@Composable
fun Icon(
    painter: Painter,
    modifier: Modifier = Modifier,

    /** 图标尺寸 (使用 Token) */
    size: Dp = IconSizes.Default.medium,

    /** 图标着色 (null = 原色) */
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
    size: Dp = IconSizes.Default.medium,
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
