package com.gearui.foundation.field

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * True while a field renders *inside* another field's frame, as the editable part of
 * an [com.gearui.components.inputgroup.InputGroup].
 *
 * An embedded field drops its own surface: no shadow, no fill, no outline and no fixed
 * width, because the group already draws all of that. Without this an Input inside a
 * group painted a second pill inside the first one, and the two rounded corners never
 * lined up.
 */
internal val LocalFieldEmbedded = staticCompositionLocalOf { false }
internal val LocalFieldGroupEnabled = staticCompositionLocalOf { true }
internal val LocalFieldDisabledAppearanceOwned = staticCompositionLocalOf { false }
