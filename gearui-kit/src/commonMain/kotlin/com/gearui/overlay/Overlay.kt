package com.gearui.overlay

import androidx.compose.runtime.*

/**
 * OverlayRoot - App root wrapper
 *
 * Must wrap the outermost level of the App to provide the Overlay capability
 *
 * Usage:
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
 * Returns the Overlay Controller
 *
 * Usage:
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
