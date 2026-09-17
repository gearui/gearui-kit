package com.gearui.foundation.material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.ui.graphics.luminance

data class SurfaceShadowStyles(
    val surface: List<SurfaceShadow>,
    val field: List<SurfaceShadow>,
    val overlay: List<SurfaceShadow>,
)

/** Optional application override, independent of accent color and shape. */
val LocalSurfaceShadowStyles = staticCompositionLocalOf<SurfaceShadowStyles?> { null }

@Composable
fun surfaceShadowStyles(): SurfaceShadowStyles = LocalSurfaceShadowStyles.current ?: if (Theme.colors.background.luminance() < 0.5f) {
    SurfaceShadowStyles(MaterialDefaults.darkSurface, MaterialDefaults.darkField, MaterialDefaults.darkOverlay)
} else {
    SurfaceShadowStyles(MaterialDefaults.lightSurface, MaterialDefaults.lightField, MaterialDefaults.lightOverlay)
}
