package com.gearui.components.switch

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.typography.IconSizes
import com.gearui.foundation.control.*
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.motion.feedbackDuration
import com.gearui.foundation.primitives.LoadingIndicator
import com.gearui.foundation.border.BorderWidth
import com.tencent.kuikly.compose.animation.core.*
import com.tencent.kuikly.compose.foundation.interaction.*
import com.tencent.kuikly.compose.foundation.selection.toggleable
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.tencent.kuikly.compose.ui.graphics.lerp
import com.tencent.kuikly.compose.ui.semantics.Role

/**
 * Switch size
 */
enum class SwitchSize {
    LARGE,   // GearUI larger-size extension
    MEDIUM,  // HeroUI Native default: 48 x 24
    SMALL    // GearUI compact-size extension
}

/**
 * Switch type
 */
enum class SwitchType {
    FILL,    // 填充型（默认）
    TEXT,    // 带文字
    LOADING, // 加载中
    ICON     // 带图标
}

/**
 * Switch - toggle switch
 *
 * Features:
 * - on / off toggling
 * - disabled state (whole control at token-defined opacity)
 * - loading state (shows an indicator and is not clickable)
 * - text type (on/off text inside the thumb)
 * - icon type (tick/cross icon inside the thumb)
 * - 3 sizes (large, medium, small)
 * - custom track colour
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    type: SwitchType = SwitchType.FILL,
    size: SwitchSize = SwitchSize.MEDIUM,
    trackOnColor: Color? = null,
    trackOffColor: Color? = null,
    openText: String = I18n.strings.field.switchOn,
    closeText: String = I18n.strings.field.switchOff
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Box(modifier.sizeIn(minWidth = ControlGeometry.selectionTouchTarget, minHeight = ControlGeometry.selectionTouchTarget)
        .toggleable(checked, interactionSource = interaction, indication = null,
            enabled = enabled && type != SwitchType.LOADING, role = Role.Switch, onValueChange = onCheckedChange),
        contentAlignment = Alignment.Center) {
        SwitchVisual(checked, enabled, pressed, type, size, trackOnColor, trackOffColor, openText, closeText)
    }
}

@Composable
private fun SwitchVisual(
    checked: Boolean, enabled: Boolean, pressed: Boolean, type: SwitchType, size: SwitchSize,
    trackOnColor: Color?, trackOffColor: Color?, openText: String, closeText: String,
) {
    val colors = Theme.colors
    val motion = Theme.motion
    val factor = when (size) {
        SwitchSize.SMALL -> ControlGeometry.selectionSmall / ControlGeometry.selectionMedium
        SwitchSize.MEDIUM -> 1f
        SwitchSize.LARGE -> ControlGeometry.selectionLarge / ControlGeometry.selectionMedium
    }
    val width = ControlGeometry.switchWidth * factor
    val height = ControlGeometry.switchHeight * factor
    val thumbWidth = ControlGeometry.switchThumbWidth * factor
    val thumbHeight = ControlGeometry.switchThumbHeight * factor
    val inset = ControlGeometry.switchInset * factor
    val progress by animateFloatAsState(if (checked) 1f else 0f,
        if (motion.normal <= 0) snap() else spring(dampingRatio = switchDampingRatio, stiffness = switchSpringStiffness(motion)))
    val colorProgress by animateFloatAsState(if (checked) 1f else 0f,
        tween(motion.feedbackDuration(FeedbackDefaults.switchColorDuration), easing = FeedbackDefaults.switchEasing))
    val scale by animateFloatAsState(if (pressed && enabled && type != SwitchType.LOADING && motion.normal > 0) FeedbackDefaults.selectionPressScale else 1f,
        tween(motion.feedbackDuration(FeedbackDefaults.selectionPressDuration), easing = selectionTimingEasing))
    val offset = switchThumbPosition(progress, width.value, thumbWidth.value, inset.value).dp
    Box(Modifier.width(width).height(height).graphicsLayer {
        alpha = if (enabled) 1f else FeedbackDefaults.disabledOpacity
        scaleX = scale; scaleY = scale
    }.clip(Theme.shapes.full).background(lerp(trackOffColor ?: colors.muted, trackOnColor ?: colors.primary, colorProgress)),
        contentAlignment = Alignment.CenterStart) {
        Box(Modifier.offset(x = offset).width(thumbWidth).height(thumbHeight).clip(Theme.shapes.full)
            .background(colors.primaryForeground), contentAlignment = Alignment.Center) {
            val contentColor = if (checked) colors.primary else colors.mutedForeground
            when (type) {
                SwitchType.TEXT -> Text(if (checked) openText else closeText, style = Theme.typography.bodySmall, color = contentColor, maxLines = 1)
                SwitchType.ICON -> Icon(if (checked) Icons.check else Icons.x, size = IconSizes.Default.xs, tint = contentColor)
                SwitchType.LOADING -> if (motion.normal <= 0) {
                    Icon(Icons.arrows_clockwise, size = IconSizes.Default.xs, tint = contentColor)
                } else {
                    LoadingIndicator(size = IconSizes.Default.xs, color = contentColor, strokeWidth = BorderWidth.thin)
                }
                SwitchType.FILL -> Unit
            }
        }
    }
}

/**
 * SwitchWithLabel - switch with a label
 */
@Composable
fun SwitchWithLabel(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    type: SwitchType = SwitchType.FILL,
    size: SwitchSize = SwitchSize.MEDIUM
) {
    val colors = Theme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Row(modifier.fillMaxWidth().heightIn(min = ControlGeometry.selectionTouchTarget)
        .toggleable(checked, interactionSource = interaction, indication = null,
            enabled = enabled && type != SwitchType.LOADING, role = Role.Switch, onValueChange = onCheckedChange)
        .padding(vertical = Spacing.md), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = Theme.typography.bodyLarge, color = if (enabled) colors.foreground else colors.mutedForeground)
        SwitchVisual(checked, enabled, pressed, type, size, null, null, I18n.strings.field.switchOn, I18n.strings.field.switchOff)
    }
}
