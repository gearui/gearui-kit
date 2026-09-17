package com.gearui.sample.examples.card

import androidx.compose.runtime.*
import com.gearui.components.button.Button
import com.gearui.foundation.material.*
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme
import com.gearui.theme.LocalThemeColors
import com.gearui.theme.Themes
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.StrokeCap
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp

/** Shared, deterministic device acceptance cases; no platform-specific imitation. */
@Composable
internal fun SurfaceLab(initialDark: Boolean = false, initialSquare: Boolean = false) {
    var dark by remember { mutableStateOf(initialDark) }
    var square by remember { mutableStateOf(initialSquare) }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(text = if (dark) "Light" else "Dark", onClick = { dark = !dark })
        Button(text = if (square) "Rounded" else "Square", onClick = { square = !square })
    }
    // Scope only the specimen palette; the sample retains its single App runtime.
    CompositionLocalProvider(LocalThemeColors provides (if (dark) Themes.Dark.colors else Themes.Light.colors)) {
        val colors = Theme.colors
        val shape = RoundedCornerShape(if (square) 0.dp else 16.dp)
        Column(Modifier.fillMaxWidth().background(colors.background).padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text("DTCG surface acceptance", style = Theme.typography.titleMedium)
            val shadows = surfaceShadowStyles()
            for ((name, layers) in listOf("Layered outer" to shadows.overlay,
                "Inset + spread" to listOf(SurfaceShadow(colors.primary.copy(alpha = .45f), 3.dp, 2.dp, 8.dp, 3.dp, true)),
                "Negative spread" to listOf(SurfaceShadow(colors.foreground.copy(alpha = .3f), 0.dp, 5.dp, 10.dp, (-3).dp)))) {
                DecoratedSurface(Modifier.fillMaxWidth().height(56.dp), shape, layers) {
                    Box(Modifier.fillMaxSize().background(colors.surface), contentAlignment = Alignment.Center) { Text(name) }
                }
            }
            for (pair in BorderLineStyle.entries.chunked(3)) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (style in pair) DecoratedSurface(
                    Modifier.weight(1f).height(52.dp), shape,
                    border = SurfaceBorder(colors.primary, 3.dp, SurfaceStroke(style, listOf(5.dp, 3.dp, 1.dp), StrokeCap.Round)),
                ) {
                    Box(Modifier.fillMaxSize().background(colors.surface), contentAlignment = Alignment.Center) {
                        Text(style.name, style = Theme.typography.bodySmall)
                    }
                }
            }
            Text("System · 默认字体 Aa 123", style = Theme.typography.bodyMedium)
            Text("Monospace Aa 123", style = Theme.typography.bodyMedium.copy(fontFamily = listOf("monospace")))
            Text("Tracking +2 Aa 123", style = Theme.typography.bodyMedium.copy(letterSpacing = 2.sp))
            Text("Fallback Aa 123", style = Theme.typography.bodyMedium.copy(fontFamily = listOf("NotInstalled", "serif")))
        }
    }
}
