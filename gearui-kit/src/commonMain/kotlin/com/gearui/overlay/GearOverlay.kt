package com.gearui.overlay

import androidx.compose.runtime.*

/**
 * GearOverlayRoot - App 根节点包装器
 *
 * 必须在 App 最外层使用，提供 Overlay 能力
 *
 * 使用方式：
 * ```kotlin
 * @Composable
 * fun App() {
 *     GearOverlayRoot {
 *         MainAppContent()
 *     }
 * }
 * ```
 */
@Composable
fun GearOverlayRoot(
    content: @Composable () -> Unit
) {
    val controller = remember { GearOverlayController() }

    CompositionLocalProvider(
        LocalGearOverlayController provides controller
    ) {
        GearOverlayHost(controller, content)
    }
}

/**
 * 获取 Overlay Controller
 *
 * 使用方式：
 * ```kotlin
 * val overlay = rememberGearOverlay()
 *
 * overlay.show(anchorBounds) {
 *     DropdownMenu { ... }
 * }
 * ```
 */
@Composable
fun rememberGearOverlay(): GearOverlayController {
    return LocalGearOverlayController.current
}
