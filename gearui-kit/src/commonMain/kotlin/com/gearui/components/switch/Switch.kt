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
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.typography.IconSizes

/**
 * Switch size
 */
enum class SwitchSize {
    LARGE,   // 大尺寸 52x32
    MEDIUM,  // 中尺寸 45x28
    SMALL    // 小尺寸 39x24
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
 * Switch size data class
 */
private data class SwitchDimensions(
    val trackWidth: Dp,
    val trackHeight: Dp,
    val thumbSize: Dp,
    val thumbPadding: Dp
)

/**
 * Switch - toggle switch
 *
 * Features:
 * - on / off toggling
 * - disabled state (whole control at 0.4 opacity)
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
    val colors = Theme.colors

    val dimensions = when (size) {
        SwitchSize.LARGE -> SwitchDimensions(52.dp, 32.dp, 28.dp, 2.dp)
        SwitchSize.MEDIUM -> SwitchDimensions(45.dp, 28.dp, 24.dp, 2.dp)
        SwitchSize.SMALL -> SwitchDimensions(39.dp, 24.dp, 20.dp, 2.dp)
    }

    // Not clickable while loading
    val switchEnabled = enabled && type != SwitchType.LOADING

    // Track colour
    val activeTrackColor = trackOnColor ?: colors.primary
    val inactiveTrackColor = trackOffColor ?: colors.mutedForeground

    val trackColor = if (checked) activeTrackColor else inactiveTrackColor

    // Thumb content colour
    val thumbContentColor = if (checked) colors.primary else colors.mutedForeground

    // Thumb position
    val thumbOffset = if (checked) {
        dimensions.trackWidth - dimensions.thumbSize - dimensions.thumbPadding
    } else {
        dimensions.thumbPadding
    }

    // Overall opacity: 0.4 when disabled
    val alpha = if (enabled) 1f else 0.4f

    Box(
        modifier = modifier
            .width(dimensions.trackWidth)
            .height(dimensions.trackHeight)
            .alpha(alpha)
            .clip(RoundedCornerShape(dimensions.trackHeight / 2))
            .background(trackColor)
            .then(
                if (switchEnabled) {
                    Modifier.clickable { onCheckedChange(!checked) }
                } else Modifier
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        // Thumb
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(dimensions.thumbSize)
                .clip(RoundedCornerShape(dimensions.thumbSize / 2))
                .background(colors.surface),
            contentAlignment = Alignment.Center
        ) {
            // Thumb content
            when (type) {
                SwitchType.TEXT -> {
                    // Text type: on/off wording
                    Text(
                        text = if (checked) openText else closeText,
                        style = Typography.BodySmall,
                        color = thumbContentColor,
                        maxLines = 1
                    )
                }
                SwitchType.ICON -> {
                    // Icon type: tick/cross
                    Icon(
                        name = if (checked) Icons.check else Icons.close,
                        size = IconSizes.Default.xs,
                        tint = thumbContentColor
                    )
                }
                SwitchType.LOADING -> {
                    // Loading type: the indicator
                    Icon(
                        name = Icons.autorenew,
                        size = IconSizes.Default.xs,
                        tint = thumbContentColor
                    )
                }
                SwitchType.FILL -> {
                    // Filled type: no content
                }
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
    val typography = Theme.typography

    // Not clickable while loading
    val switchEnabled = enabled && type != SwitchType.LOADING

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (switchEnabled) {
                    Modifier.clickable { onCheckedChange(!checked) }
                } else Modifier
            )
            .padding(vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = Typography.BodyLarge,
            color = if (enabled) colors.foreground else colors.mutedForeground
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            type = type,
            size = size
        )
    }
}
