package com.gearui.sample

import com.tencent.kuikly.compose.ui.graphics.Color
import platform.UIKit.UIScreen
import platform.UIKit.UIUserInterfaceStyle

/**
 * iOS status bar controller implementation
 * On iOS the status bar colour is controlled by the system, so this is a no-op
 */
actual object StatusBarControllerImpl {
    actual fun setStatusBarColor(color: Color, darkIcons: Boolean) {
        // On iOS the status bar follows the system; nothing to set
    }

    // The screen's trait is the system's: GearUI sets the app's choice on the windows,
    // which the screen does not inherit.
    actual fun isSystemDarkMode(): Boolean =
        UIScreen.mainScreen.traitCollection.userInterfaceStyle == UIUserInterfaceStyle.UIUserInterfaceStyleDark
}
