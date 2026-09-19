package com.gearui.components.alert

import androidx.compose.runtime.Composable
import com.gearui.components.closebutton.CloseButton
import com.gearui.components.icon.Icons
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.material.DecoratedSurface
import com.gearui.foundation.material.surfaceShadowStyles
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Colors
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.lerp
import com.tencent.kuikly.compose.ui.text.font.FontWeight

/** Status of an [Alert]; sets the indicator icon and the title colour. */
enum class AlertStatus { DEFAULT, ACCENT, SUCCESS, WARNING, DANGER }

/**
 * Alert — HeroUI Native `Alert`: an inline status message inside the page flow.
 *
 * A surface card (radius 24, surface shadow, padding and gap 12) with a status
 * indicator, a medium title in the status "soft foreground" colour and a muted
 * description. Unlike Toast or Notification it does not float and does not expire.
 *
 * - [icon] overrides the status icon; [showIcon] false drops the indicator.
 * - [action] renders under the text, for a small button such as "Retry".
 * - [onClose] adds a [CloseButton] at the end.
 */
@Composable
fun Alert(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    status: AlertStatus = AlertStatus.DEFAULT,
    icon: String? = null,
    showIcon: Boolean = true,
    action: (@Composable () -> Unit)? = null,
    onClose: (() -> Unit)? = null,
) {
    val colors = Theme.colors
    val statusColor = alertStatusColor(colors, status)
    DecoratedSurface(
        modifier = modifier.fillMaxWidth(),
        shape = Theme.shapes.xl,
        shadows = surfaceShadowStyles().surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .padding(ControlGeometry.alertPadding),
            horizontalArrangement = Arrangement.spacedBy(ControlGeometry.alertGap),
            verticalAlignment = Alignment.Top,
        ) {
            if (showIcon) {
                // Reference indicator sits 3.5 below the top to centre on the title's first line.
                Box(Modifier.padding(top = ControlGeometry.alertIndicatorOffset)) {
                    Icon(
                        name = icon ?: alertStatusIcon(status),
                        size = ControlGeometry.alertIcon,
                        tint = statusColor,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(ControlGeometry.dialogTextGap),
            ) {
                Text(
                    text = title,
                    style = Theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = statusColor,
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground,
                    )
                }
                if (action != null) {
                    Box(Modifier.padding(top = ControlGeometry.alertActionTop)) { action() }
                }
            }
            if (onClose != null) {
                CloseButton(onClick = onClose)
            }
        }
    }
}

/**
 * The reference `*-soft-foreground` roles: the status colour mixed toward the
 * foreground so it stays legible on the surface (accent and danger 20%, warning 35%,
 * success 30%). Mixed in sRGB rather than OKLab; the difference is below what the
 * eye separates at these ratios.
 */
internal fun alertStatusColor(colors: Colors, status: AlertStatus): Color = when (status) {
    AlertStatus.DEFAULT -> colors.foreground
    AlertStatus.ACCENT -> lerp(colors.primary, colors.foreground, 0.20f)
    AlertStatus.SUCCESS -> lerp(colors.success, colors.foreground, 0.30f)
    AlertStatus.WARNING -> lerp(colors.warning, colors.foreground, 0.35f)
    AlertStatus.DANGER -> lerp(colors.destructive, colors.foreground, 0.20f)
}

/** Reference icons: a check for success, a triangle for warning, "info" otherwise. Danger uses the circled warning so it does not read as neutral information. */
internal fun alertStatusIcon(status: AlertStatus): String = when (status) {
    AlertStatus.SUCCESS -> Icons.check
    AlertStatus.WARNING -> Icons.warning
    AlertStatus.DANGER -> Icons.warning_circle
    else -> Icons.info
}
