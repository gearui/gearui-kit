package com.gearui.components.noticebar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.closebutton.CloseButton
import com.gearui.components.icon.Icons
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Colors
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.animation.core.Animatable
import com.tencent.kuikly.compose.animation.core.LinearEasing
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.heightIn
import com.tencent.kuikly.compose.foundation.layout.offset
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.layout.wrapContentSize
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clipToBounds
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.tencent.kuikly.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

/** Tone of a [NoticeBar]: sets the soft fill and the text colour. */
enum class NoticeBarTone { NEUTRAL, INFO, SUCCESS, WARNING, DANGER }

/**
 * NoticeBar — a running announcement strip: shop notices, maintenance windows, the
 * line a group owner pins to the top of a chat.
 *
 * It is the moving sibling of [com.gearui.components.alert.Alert]. Alert is a block the
 * user reads once and dismisses; NoticeBar is one line that keeps running, so a message
 * longer than the screen still gets read. A tone gives it the soft fill and soft
 * foreground of that status rather than a solid badge colour.
 *
 * The text scrolls only when it does not fit, and never under reduced motion, where it
 * is ellipsized instead: an endlessly moving strip is exactly what that setting is for.
 *
 * ```kotlin
 * NoticeBar("Maintenance tonight at 23:00, about an hour", tone = NoticeBarTone.WARNING, onClose = { … })
 * ```
 */
@Composable
fun NoticeBar(
    text: String,
    modifier: Modifier = Modifier,
    tone: NoticeBarTone = NoticeBarTone.INFO,
    icon: String? = null,
    showIcon: Boolean = true,
    scroll: Boolean = true,
    onClick: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val colors = Theme.colors
    val content = noticeBarForeground(colors, tone)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = ControlGeometry.noticeBarHeight)
            .background(noticeBarFill(colors, tone))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = ControlGeometry.noticeBarPaddingInline),
        horizontalArrangement = Arrangement.spacedBy(ControlGeometry.noticeBarGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showIcon) {
            Icon(name = icon ?: noticeBarIcon(tone), size = ControlGeometry.alertIcon, tint = content)
        }
        Box(modifier = Modifier.weight(1f)) {
            MarqueeText(text = text, color = content, scroll = scroll)
        }
        action?.invoke()
        if (onClose != null) {
            CloseButton(onClick = onClose, containerColor = Color.Transparent, iconColor = content)
        }
    }
}

/**
 * One line that scrolls itself when it is wider than the space it gets.
 *
 * A single line is drawn, measured unbounded and shifted by an animated offset: a
 * constrained line reports the strip's width, so "is it too long?" would have been
 * decided by its own clipping and nothing would ever scroll. One copy also avoids a
 * hidden measuring copy, which Kuikly draws anyway — neither `alpha(0f)` nor a
 * transparent colour hides a Text there.
 *
 * Each pass runs the line out to the left, then brings it back in from the right edge,
 * which is what makes a long notice readable without a second copy chasing it.
 */
@Composable
private fun MarqueeText(text: String, color: Color, scroll: Boolean) {
    val motion = Theme.motion
    val density = LocalDensity.current
    var viewportPx by remember { mutableStateOf(0) }
    // Widest measurement seen for this text. Once the line has been measured unbounded the
    // value stands, so switching modes cannot argue the text back into fitting.
    var intrinsicPx by remember(text) { mutableStateOf(0) }
    val free = scroll && motion.normal > 0
    val overflows = intrinsicPx > viewportPx && viewportPx > 0
    val animate = free && overflows
    val offset = remember { Animatable(0f) }

    LaunchedEffect(animate, intrinsicPx, viewportPx) {
        offset.snapTo(0f)
        if (!animate) return@LaunchedEffect
        val gapPx = with(density) { ControlGeometry.noticeBarScrollGap.toPx() }
        var from = 0f
        while (true) {
            val to = -intrinsicPx.toFloat() - gapPx
            // A fixed speed, so a long notice is not rushed to keep a fixed duration.
            val distanceDp = (from - to) / density.density
            val duration = (distanceDp / ControlGeometry.noticeBarScrollSpeed.value * 1000f).roundToInt()
            offset.animateTo(to, tween(durationMillis = duration.coerceAtLeast(1), easing = LinearEasing))
            from = viewportPx.toFloat()
            offset.snapTo(from)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clipToBounds()
            .onSizeChanged { viewportPx = it.width },
    ) {
        Text(
            text = text,
            style = Theme.typography.bodySmall,
            color = color,
            maxLines = 1,
            overflow = if (free) TextOverflow.Clip else TextOverflow.Ellipsis,
            modifier = (if (free) {
                Modifier
                    .wrapContentSize(Alignment.CenterStart, unbounded = true)
                    .offset { IntOffset(offset.value.roundToInt(), 0) }
            } else {
                Modifier.fillMaxWidth()
            }).onSizeChanged { intrinsicPx = maxOf(intrinsicPx, it.width) },
        )
    }
}

/** Soft fill of a tone; the neutral tone sits on the muted fill. */
internal fun noticeBarFill(colors: Colors, tone: NoticeBarTone): Color = when (tone) {
    NoticeBarTone.NEUTRAL -> colors.muted
    NoticeBarTone.INFO -> colors.primarySoft
    NoticeBarTone.SUCCESS -> colors.successSoft
    NoticeBarTone.WARNING -> colors.warningSoft
    NoticeBarTone.DANGER -> colors.destructiveSoft
}

/** Soft foreground of a tone, for text and icons on [noticeBarFill]. */
internal fun noticeBarForeground(colors: Colors, tone: NoticeBarTone): Color = when (tone) {
    NoticeBarTone.NEUTRAL -> colors.mutedForeground
    NoticeBarTone.INFO -> colors.primarySoftForeground
    NoticeBarTone.SUCCESS -> colors.successSoftForeground
    NoticeBarTone.WARNING -> colors.warningSoftForeground
    NoticeBarTone.DANGER -> colors.destructiveSoftForeground
}

/** Default icon per tone, matching Alert so the two read as one family. */
internal fun noticeBarIcon(tone: NoticeBarTone): String = when (tone) {
    NoticeBarTone.SUCCESS -> Icons.check
    NoticeBarTone.WARNING -> Icons.warning
    NoticeBarTone.DANGER -> Icons.warning_circle
    NoticeBarTone.NEUTRAL -> Icons.bell
    NoticeBarTone.INFO -> Icons.info
}
