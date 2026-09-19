package com.gearui.foundation.motion

import androidx.compose.runtime.Composable
import com.gearui.foundation.interaction.pressScale
import com.gearui.foundation.interaction.pressedSurfaceColor
import com.gearui.theme.Theme
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Shape

/**
 * A small icon control on the neutral fill (a field's clear button): the press scale
 * plus the neutral fill tinted while pressed. The caller reports [pressed]; gesture
 * handling stays with the caller because the touch must not reach the native editor.
 */
@Composable
internal fun Modifier.iconPressFeedback(pressed: Boolean, @Suppress("UNUSED_PARAMETER") size: Dp, shape: Shape): Modifier =
    this.pressScale(pressed).clip(shape).background(pressedSurfaceColor(Theme.colors.muted, pressed))
