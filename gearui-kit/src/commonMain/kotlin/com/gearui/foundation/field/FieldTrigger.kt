package com.gearui.foundation.field

import androidx.compose.runtime.*
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.field.inputBorderColor
import com.gearui.theme.LocalInputColors
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.hoverable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.interaction.collectIsHoveredAsState
import com.tencent.kuikly.compose.foundation.interaction.collectIsPressedAsState
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.gearui.foundation.motion.FeedbackDefaults

/** Opening a list is not a validation state or a keyboard focus ring. */
@Composable
internal fun fieldTriggerModifier(enabled: Boolean, error: String?, onClick: () -> Unit): Modifier {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val pressed by interaction.collectIsPressedAsState()
    val palette = LocalInputColors.current
    val colors = Theme.colors
    val shape = FieldDefaults.shape
    return Modifier.graphicsLayer {
        alpha = if (enabled) 1f else FeedbackDefaults.disabledOpacity
    }.clip(shape)
        .border(FieldSizeTokens.Medium.borderWidth,
            inputBorderColor(palette, enabled, false, hovered || pressed,
                if (error != null) colors.destructive else null), shape)
        .background(if (enabled && pressed) colors.muted else palette.background)
        .hoverable(interaction, enabled)
        .clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick)
}
