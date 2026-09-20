package com.gearui.components.rate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.icon.Icons
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.interaction.pressScale
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.i18n.I18n
import com.gearui.i18n.formatArgs
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.semantics.contentDescription
import com.tencent.kuikly.compose.ui.semantics.role
import com.tencent.kuikly.compose.ui.semantics.semantics
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.gestures.detectTapGestures
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.requiredHeight
import com.tencent.kuikly.compose.foundation.layout.requiredSize
import com.tencent.kuikly.compose.foundation.layout.wrapContentSize
import com.tencent.kuikly.compose.foundation.layout.requiredWidth
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clipToBounds
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.Dp
import kotlin.math.ceil
import kotlin.math.round

/**
 * Rate — a star rating, for reading a score and for giving one.
 *
 * Both layers use the *same* glyph: a track star in muted grey and, above it, the
 * active star clipped to the score. Identical shapes are what makes a half star line
 * up; an outline glyph under a solid one only matches if their bounding boxes agree,
 * and the fill has to be clipped per star, because clipping a whole row of stars
 * squeezes the row rather than cutting it.
 *
 * With [allowHalf] the tapped half of a star decides the value, so a half score is one
 * tap rather than "tap the same star twice". [allowClear] lets a second tap on the
 * current value reset it to zero.
 *
 * A fractional [value] is shown to the nearest half when [allowHalf] is on and rounded
 * to a whole star otherwise, so a 4.3 average never renders as a sliver no one can read.
 *
 * ```kotlin
 * var score by remember { mutableStateOf(3.5f) }
 * Rate(score, { score = it }, allowHalf = true)
 * ```
 *
 * [icon] and [emptyIcon] take icon *names* (as in [Icons]) to replace the star, for a
 * heart or a flame. Use [RateDisplay] for a read-only score.
 */
@Composable
fun Rate(
    value: Float,
    onValueChange: ((Float) -> Unit)?,
    modifier: Modifier = Modifier,
    count: Int = 5,
    allowHalf: Boolean = false,
    allowClear: Boolean = false,
    readonly: Boolean = false,
    /** Icon name for the active star; the built-in star is used when left null. */
    icon: String? = null,
    /** Icon name for the track star; defaults to [icon], and to the built-in star. */
    emptyIcon: String? = null,
    size: Dp = ControlGeometry.rateStarSize,
    gap: Dp = ControlGeometry.rateStarGap,
    enabled: Boolean = true,
    showText: Boolean = false,
    texts: List<String>? = null,
    activeColor: Color = Color.Unspecified,
) {
    val colors = Theme.colors
    val interactive = enabled && !readonly && onValueChange != null
    val clamped = value.coerceIn(0f, count.toFloat())
    val active = if (activeColor != Color.Unspecified) activeColor else colors.warning
    val track = colors.mutedForeground.copy(alpha = FeedbackDefaults.ratingTrackOpacity)
    val activeIcon = icon ?: Icons.star_fill
    val trackIcon = emptyIcon ?: icon ?: Icons.star_fill

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
        repeat(count) { index ->
            Star(
                label = I18n.strings.field.ratingValueFormat.formatArgs("value" to (index + 1).toString()),
                fraction = starFraction(clamped, index, allowHalf),
                size = size,
                activeIcon = activeIcon,
                trackIcon = trackIcon,
                activeColor = active,
                trackColor = track,
                enabled = enabled,
                onTap = if (!interactive) null else { atStart ->
                    onValueChange!!(nextRateValue(value, index, atStart, allowHalf, allowClear))
                },
            )
        }

        if (showText) {
            Text(
                text = rateText(clamped, texts),
                style = Theme.typography.bodyMedium,
                color = colors.mutedForeground,
                modifier = Modifier,
            )
        }
    }
}

@Composable
private fun Star(
    label: String,
    fraction: Float,
    size: Dp,
    activeIcon: String,
    trackIcon: String,
    activeColor: Color,
    trackColor: Color,
    enabled: Boolean,
    onTap: ((atStart: Boolean) -> Unit)?,
) {
    var pressed by remember { mutableStateOf(false) }
    val gesture = if (onTap == null) {
        Modifier
    } else {
        Modifier.pointerInput(size, onTap) {
            detectTapGestures(
                onPress = {
                    pressed = true
                    tryAwaitRelease()
                    pressed = false
                },
                onTap = { offset -> onTap(offset.x <= this.size.width / 2f) },
            )
        }
    }
    Box(
        modifier = Modifier
            .requiredSize(size)
            .pressScale(pressed)
            .then(gesture)
            .semantics {
                contentDescription = label
                if (onTap != null) role = Role.Button
            },
    ) {
        Icon(name = trackIcon, size = size, tint = if (enabled) trackColor else trackColor.copy(alpha = trackColor.alpha * FeedbackDefaults.disabledOpacity))
        if (fraction > 0f) {
            // The cut has to take the *leading* part of the star. requiredSize would keep
            // the icon at full size but centres it in the narrower box, which sliced a band
            // out of the middle; wrapContentSize(unbounded) keeps full size and pins the
            // icon's start edge to the clip's start edge.
            Box(
                modifier = Modifier
                    .requiredWidth(size * fraction)
                    .requiredHeight(size)
                    .clipToBounds(),
            ) {
                Icon(
                    name = activeIcon,
                    size = size,
                    tint = if (enabled) activeColor else activeColor.copy(alpha = FeedbackDefaults.disabledOpacity),
                    modifier = Modifier.wrapContentSize(Alignment.CenterStart, unbounded = true),
                )
            }
        }
    }
}

/** How much of star [index] is filled: whole stars, or halves when [allowHalf]. */
internal fun starFraction(value: Float, index: Int, allowHalf: Boolean): Float {
    val raw = (value - index).coerceIn(0f, 1f)
    return if (allowHalf) round(raw * 2f) / 2f else if (raw >= 0.5f) 1f else 0f
}

/**
 * The value after tapping star [index]. [atStart] (the leading half of the star) gives
 * the half score; tapping the current value again clears it when [allowClear].
 */
internal fun nextRateValue(
    current: Float,
    index: Int,
    atStart: Boolean,
    allowHalf: Boolean,
    allowClear: Boolean,
): Float {
    val tapped = index + if (allowHalf && atStart) 0.5f else 1f
    return if (allowClear && current == tapped) 0f else tapped
}

/** The label after the stars: the matching description, else the score without a trailing ".0". */
internal fun rateText(value: Float, texts: List<String>?): String {
    val index = ceil(value).toInt() - 1
    if (texts != null && index in texts.indices) return texts[index]
    val whole = value.toInt()
    return if (value == whole.toFloat()) whole.toString() else value.toString()
}

/**
 * Rate with the matching description underneath, for a rating the user is giving
 * ("Poor" … "Excellent").
 */
@Composable
fun RateWithDescription(
    value: Float,
    onValueChange: ((Float) -> Unit)?,
    modifier: Modifier = Modifier,
    count: Int = 5,
    allowHalf: Boolean = false,
    enabled: Boolean = true,
    descriptions: List<String> = I18n.strings.guide.rateDescriptions
) {
    val colors = Theme.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Rate(
            value = value,
            onValueChange = onValueChange,
            count = count,
            allowHalf = allowHalf,
            enabled = enabled,
        )
        val index = ceil(value).toInt() - 1
        if (index in descriptions.indices) {
            Text(
                text = descriptions[index],
                style = Theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = colors.foreground,
            )
        }
    }
}

/**
 * A score as read-only stars. Averages are common here, so halves are on and the score
 * is shown next to the stars.
 */
@Composable
fun RateDisplay(
    value: Float,
    modifier: Modifier = Modifier,
    count: Int = 5,
    size: Dp = ControlGeometry.rateStarSizeCompact,
    showValue: Boolean = true
) {
    Rate(
        value = value,
        onValueChange = null,
        count = count,
        allowHalf = true,
        readonly = true,
        size = size,
        showText = showValue,
        modifier = modifier
    )
}
