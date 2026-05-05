package com.gearui.overlay

import androidx.compose.runtime.*

/**
 * OverlayRoot - App 根节点包装器
 *
 * 必须在 App 最外层使用，提供 Overlay 能力
 *
 * 使用方式：
 * ```kotlin
 * @Composable
 * fun App() {
 *     OverlayRoot {
 *         MainAppContent()
 *     }
 * }
 * ```
 */
@Composable
fun OverlayRoot(
    content: @Composable () -> Unit
) {
    val controller = remember { OverlayController() }

    CompositionLocalProvider(
        LocalOverlayController provides controller
    ) {
        OverlayHost(controller, content)
    }
}

/**
 * 获取 Overlay Controller
 *
 * 使用方式：
 * ```kotlin
 * val overlay = rememberOverlay()
 *
 * overlay.show(anchorBounds) {
 *     DropdownMenu { ... }
 * }
 * ```
 */
@Composable
fun rememberOverlay(): OverlayController {
    return LocalOverlayController.current
}
