package com.gearui.foundation.keyboard

import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSValue
import platform.UIKit.CGRectValue
import platform.UIKit.UIKeyboardFrameEndUserInfoKey
import platform.UIKit.UIKeyboardWillChangeFrameNotification
import platform.UIKit.UIKeyboardWillHideNotification
import platform.UIKit.UIScreen

/** The visible keyboard height from UIKit's keyboard notifications (0 for a floating or undocked keyboard). */
@OptIn(ExperimentalForeignApi::class)
internal actual fun observePlatformKeyboard(onHeight: (Dp) -> Unit) {
    val center = NSNotificationCenter.defaultCenter
    center.addObserverForName(UIKeyboardWillChangeFrameNotification, null, NSOperationQueue.mainQueue) { note ->
        val frame = note?.userInfo?.get(UIKeyboardFrameEndUserInfoKey) as? NSValue
        val screen = UIScreen.mainScreen.bounds.useContents { size.height }
        val height = frame?.CGRectValue()?.useContents {
            if (origin.y + size.height >= screen - 1.0) (screen - origin.y).coerceAtLeast(0.0) else 0.0
        } ?: 0.0
        onHeight(height.dp)
    }
    center.addObserverForName(UIKeyboardWillHideNotification, null, NSOperationQueue.mainQueue) { onHeight(0.dp) }
}
