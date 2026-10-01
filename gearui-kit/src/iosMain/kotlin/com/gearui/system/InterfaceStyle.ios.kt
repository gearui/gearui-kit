package com.gearui.system

import com.gearui.theme.ThemeMode
import platform.UIKit.UIApplication
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * Sets every window's interface style; the keyboard, alerts and menus follow it.
 *
 * Compose runs on KuiklyUI's context thread, not the main thread, and UIKit may only be
 * touched on the main thread — so the change is posted there.
 */
internal actual fun applyPlatformInterfaceStyle(mode: ThemeMode) {
    val style = when (mode) {
        ThemeMode.Light -> UIUserInterfaceStyle.UIUserInterfaceStyleLight
        ThemeMode.Dark -> UIUserInterfaceStyle.UIUserInterfaceStyleDark
        ThemeMode.System -> UIUserInterfaceStyle.UIUserInterfaceStyleUnspecified
    }
    dispatch_async(dispatch_get_main_queue()) {
        UIApplication.sharedApplication.connectedScenes.forEach { scene ->
            (scene as? UIWindowScene)?.windows?.forEach { (it as? UIWindow)?.overrideUserInterfaceStyle = style }
        }
    }
}
