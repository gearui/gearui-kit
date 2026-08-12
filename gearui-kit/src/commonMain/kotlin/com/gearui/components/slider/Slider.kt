package com.gearui.components.slider

import androidx.compose.runtime.*
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.gestures.detectDragGestures
import com.tencent.kuikly.compose.foundation.gestures.detectTapGestures
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.IntSize
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
import com.gearui.foundation.layout.Spacing
import kotlin.math.abs
import kotlin.math.roundToInt
import com.gearui.foundation.elevation.Elevation
import com.gearui.foundation.border.BorderWidth

/**
 * Slider style
 */
enum class SliderStyle {
    NORMAL,   // 普通样式
    CAPSULE   // 胶囊样式
}

/**
 * Slider - value selection by dragging
 *
 * Features:
 * - single-value selection
 * - leading and trailing labels
 * - custom range
 * - step values (discrete)
 * - disabled state
 * - tick labels
 * - current value readout
 * - plain or capsule style
 * - tap the track to jump
 */
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    leftLabel: String? = null,
    rightLabel: String? = null,
    showThumbValue: Boolean = false,
    showScaleValue: Boolean = false,
    style: SliderStyle = SliderStyle.NORMAL,
    onChangeStart: ((Float) -> Unit)? = null,
    onChangeEnd: ((Float) -> Unit)? = null
) {
    val colors = Theme.colors

    var sliderSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    var isDragging by remember { mutableStateOf(false) }
    var dragValue by remember { mutableStateOf(value) }

    LaunchedEffect(value, isDragging) {
        if (!isDragging) {
            dragValue = value
        }
    }

    val displayValue = if (isDragging) dragValue else value

    // Normalised value (0-1)
    val normalizedValue = ((displayValue - valueRange.start) / (valueRange.endInclusive - valueRange.start))
        .coerceIn(0f, 1f)

    // Track parameters
    val trackHeight = if (style == SliderStyle.CAPSULE) 24.dp else 4.dp
    val thumbSize = if (style == SliderStyle.CAPSULE) 18.dp else 20.dp
    val thumbRadius = thumbSize / 2

    // Usable track width, minus the thumb radius at each end
    fun getEffectiveWidth(): Float {
        return (sliderSize.width - with(density) { thumbSize.toPx() }).coerceAtLeast(0f)
    }

    // Value for a given position
    fun calculateValue(positionX: Float): Float {
        val effectiveWidth = getEffectiveWidth()
        if (effectiveWidth <= 0) return valueRange.start

        val thumbPx = with(density) { thumbRadius.toPx() }
        val adjustedX = (positionX - thumbPx).coerceIn(0f, effectiveWidth)
        val ratio = adjustedX / effectiveWidth

        var newValue = valueRange.start + (valueRange.endInclusive - valueRange.start) * ratio

        // With a step, snap to the nearest step value
        if (steps > 0) {
            val stepSize = (valueRange.endInclusive - valueRange.start) / (steps + 1)
            newValue = (newValue / stepSize).roundToInt() * stepSize
        }

        return newValue.coerceIn(valueRange)
    }

    fun snapValue(v: Float): Float {
        var newValue = v
        if (steps > 0) {
            val stepSize = (valueRange.endInclusive - valueRange.start) / (steps + 1)
            newValue = (newValue / stepSize).roundToInt() * stepSize
        }
        return newValue.coerceIn(valueRange)
    }

    // Formatted display value
    fun formatValue(v: Float): String {
        return if (v == v.roundToInt().toFloat()) {
            v.roundToInt().toString()
        } else {
            val rounded = (v * 10).roundToInt() / 10.0
            rounded.toString()
        }
    }

    Column(modifier = modifier) {
        // Current value, shown above the thumb
        if (showThumbValue) {
            Spacer(modifier = Modifier.height(20.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading label
            if (leftLabel != null) {
                Text(
                    text = leftLabel,
                    style = Typography.BodyMedium,
                    color = if (enabled) colors.foreground else colors.mutedForeground
                )
            }

            // Thumb container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(trackHeight + 16.dp)
                    .onSizeChanged { sliderSize = it }
                    .then(
                        if (enabled) {
                            Modifier.pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    val newValue = calculateValue(offset.x)
                                    onChangeStart?.invoke(newValue)
                                    dragValue = newValue
                                    onValueChange(newValue)
                                    onChangeEnd?.invoke(newValue)
                                }
                            }
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Track
                when (style) {
                    SliderStyle.NORMAL -> NormalTrack(
                        normalizedValue = normalizedValue,
                        enabled = enabled,
                        trackHeight = trackHeight,
                        steps = steps,
                        showScaleValue = showScaleValue,
                        valueRange = valueRange
                    )
                    SliderStyle.CAPSULE -> CapsuleTrack(
                        normalizedValue = normalizedValue,
                        enabled = enabled,
                        trackHeight = trackHeight,
                        steps = steps,
                        showScaleValue = showScaleValue,
                        valueRange = valueRange
                    )
                }

                // Thumb
                val thumbOffsetX = with(density) {
                    val effectiveWidth = (sliderSize.width.toFloat() - thumbSize.toPx())
                    (effectiveWidth * normalizedValue).toDp()
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = thumbOffsetX)
                ) {
                    // Current value, shown above the thumb
                    if (showThumbValue) {
                        Text(
                            text = formatValue(displayValue),
                            style = Typography.BodySmall,
                            color = if (enabled) colors.foreground else colors.mutedForeground,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-24).dp)
                        )
                    }

                    // The thumb itself
                    Box(
                        modifier = Modifier
                            .size(thumbSize)
                            .shadow(
                                elevation = if (enabled) Elevation.raised else Elevation.none,
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .background(if (enabled) colors.surface else colors.muted)
                            .border(width = BorderWidth.thin,
                                color = if (enabled) colors.border else colors.mutedForeground,
                                shape = CircleShape
                            )
                            .then(
                                if (enabled) {
                                    Modifier.pointerInput(Unit) {
                                        detectDragGestures(
                                            onDragStart = {
                                                isDragging = true
                                                dragValue = value
                                                onChangeStart?.invoke(value)
                                            },
                                            onDragEnd = {
                                                val endValue = snapValue(dragValue)
                                                if (endValue != dragValue) {
                                                    dragValue = endValue
                                                    onValueChange(endValue)
                                                }
                                                isDragging = false
                                                onChangeEnd?.invoke(endValue)
                                            },
                                            onDragCancel = {
                                                isDragging = false
                                            }
                                        ) { change, dragAmount ->
                                            change.consume()
                                            val effectiveWidth = getEffectiveWidth()
                                            if (effectiveWidth <= 0f) return@detectDragGestures

                                            val deltaRatio = dragAmount.x / effectiveWidth
                                            val deltaValue =
                                                (valueRange.endInclusive - valueRange.start) * deltaRatio
                                            val newValue = (dragValue + deltaValue).coerceIn(valueRange)
                                            dragValue = newValue
                                            onValueChange(newValue)
                                        }
                                    }
                                } else Modifier
                            )
                    )
                }
            }

            // Trailing label
            if (rightLabel != null) {
                Text(
                    text = rightLabel,
                    style = Typography.BodyMedium,
                    color = if (enabled) colors.foreground else colors.mutedForeground
                )
            }
        }

        // Tick labels
        if (showScaleValue && steps > 0) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = thumbRadius),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val stepCount = steps + 1
                for (i in 0..stepCount) {
                    val stepValue = valueRange.start + (valueRange.endInclusive - valueRange.start) * i / stepCount
                    Text(
                        text = formatValue(stepValue),
                        style = Typography.BodySmall,
                        color = if (enabled) colors.mutedForeground else colors.mutedForeground
                    )
                }
            }
        }
    }
}

/**
 * Plain track
 */
@Composable
private fun NormalTrack(
    normalizedValue: Float,
    enabled: Boolean,
    trackHeight: Dp,
    steps: Int,
    showScaleValue: Boolean,
    valueRange: ClosedFloatingPointRange<Float>
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        // Inactive track (background)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(RoundedCornerShape(trackHeight / 2))
                .background(if (enabled) colors.muted else colors.muted)
        )

        // Active track
        Box(
            modifier = Modifier
                .fillMaxWidth(normalizedValue.coerceAtLeast(0.001f))
                .height(trackHeight)
                .clip(RoundedCornerShape(trackHeight / 2))
                .background(if (enabled) colors.primary else colors.mutedForeground)
        )

        // Tick marks
        if (steps > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 0..steps + 1) {
                    val stepNormalized = i.toFloat() / (steps + 1)
                    val isActive = stepNormalized <= normalizedValue
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (enabled) {
                                    if (isActive) colors.primary else colors.muted
                                } else {
                                    colors.muted
                                }
                            )
                    )
                }
            }
        }
    }
}

/**
 * Capsule track
 */
@Composable
private fun CapsuleTrack(
    normalizedValue: Float,
    enabled: Boolean,
    trackHeight: Dp,
    steps: Int,
    showScaleValue: Boolean,
    valueRange: ClosedFloatingPointRange<Float>
) {
    val colors = Theme.colors

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        // Inactive track (background), capsule shaped
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(RoundedCornerShape(trackHeight / 2))
                .background(if (enabled) colors.muted else colors.muted)
        )

        // Active track, inset slightly to leave room for the thumb
        val innerPadding = 3.dp
        Box(
            modifier = Modifier
                .padding(horizontal = innerPadding, vertical = innerPadding)
                .fillMaxWidth(normalizedValue.coerceAtLeast(0.001f))
                .height(trackHeight - innerPadding * 2)
                .clip(RoundedCornerShape((trackHeight - innerPadding * 2) / 2))
                .background(if (enabled) colors.primary else colors.mutedForeground)
        )
    }
}

/**
 * RangeSlider - range selection
 *
 * Features:
 * - two thumbs for a range
 * - leading and trailing labels
 * - custom range
 * - step values
 * - disabled state
 */
@Composable
fun RangeSlider(
    values: ClosedFloatingPointRange<Float>,
    onValuesChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    leftLabel: String? = null,
    rightLabel: String? = null,
    showThumbValue: Boolean = false,
    showScaleValue: Boolean = false,
    onChangeStart: ((ClosedFloatingPointRange<Float>) -> Unit)? = null,
    onChangeEnd: ((ClosedFloatingPointRange<Float>) -> Unit)? = null
) {
    val colors = Theme.colors

    var sliderSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    var draggingThumb by remember { mutableStateOf<Int?>(null) } // 0 = start, 1 = end
    var dragStartValue by remember { mutableStateOf(values.start) }
    var dragEndValue by remember { mutableStateOf(values.endInclusive) }

    LaunchedEffect(values.start, values.endInclusive, draggingThumb) {
        if (draggingThumb == null) {
            dragStartValue = values.start
            dragEndValue = values.endInclusive
        }
    }

    val displayStart = if (draggingThumb == 0) dragStartValue else values.start
    val displayEnd = if (draggingThumb == 1) dragEndValue else values.endInclusive

    // Normalised values
    val startNormalized = ((displayStart - valueRange.start) / (valueRange.endInclusive - valueRange.start))
        .coerceIn(0f, 1f)
    val endNormalized = ((displayEnd - valueRange.start) / (valueRange.endInclusive - valueRange.start))
        .coerceIn(0f, 1f)

    val thumbSize = 20.dp
    val thumbRadius = thumbSize / 2
    val trackHeight = 4.dp

    // Usable track width
    fun getEffectiveWidth(): Float {
        return (sliderSize.width - with(density) { thumbSize.toPx() }).coerceAtLeast(0f)
    }

    // Value for a given position
    fun calculateValue(positionX: Float): Float {
        val effectiveWidth = getEffectiveWidth()
        if (effectiveWidth <= 0) return valueRange.start

        val thumbPx = with(density) { thumbRadius.toPx() }
        val adjustedX = (positionX - thumbPx).coerceIn(0f, effectiveWidth)
        val ratio = adjustedX / effectiveWidth

        var newValue = valueRange.start + (valueRange.endInclusive - valueRange.start) * ratio

        if (steps > 0) {
            val stepSize = (valueRange.endInclusive - valueRange.start) / (steps + 1)
            newValue = (newValue / stepSize).roundToInt() * stepSize
        }

        return newValue.coerceIn(valueRange)
    }

    // Formatted display value
    fun formatValue(v: Float): String {
        return if (v == v.roundToInt().toFloat()) {
            v.roundToInt().toString()
        } else {
            val rounded = (v * 10).roundToInt() / 10.0
            rounded.toString()
        }
    }

    fun snapValue(v: Float): Float {
        var newValue = v
        if (steps > 0) {
            val stepSize = (valueRange.endInclusive - valueRange.start) / (steps + 1)
            newValue = (newValue / stepSize).roundToInt() * stepSize
        }
        return newValue.coerceIn(valueRange)
    }

    Column(modifier = modifier) {
        if (showThumbValue) {
            Spacer(modifier = Modifier.height(20.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading label
            if (leftLabel != null) {
                Text(
                    text = leftLabel,
                    style = Typography.BodyMedium,
                    color = if (enabled) colors.foreground else colors.mutedForeground
                )
            }

            // Thumb container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(trackHeight + 16.dp)
                    .onSizeChanged { sliderSize = it }
                    .then(
                        if (enabled) {
                            Modifier.pointerInput(displayStart, displayEnd, sliderSize) {
                                detectTapGestures { offset ->
                                    val tappedValue = calculateValue(offset.x)
                                    val moveStart =
                                        abs(tappedValue - displayStart) <= abs(tappedValue - displayEnd)

                                    if (moveStart) {
                                        val newStart = snapValue(tappedValue).coerceAtMost(displayEnd)
                                        dragStartValue = newStart
                                        val range = newStart..displayEnd
                                        onChangeStart?.invoke(range)
                                        onValuesChange(range)
                                        onChangeEnd?.invoke(range)
                                    } else {
                                        val newEnd = snapValue(tappedValue).coerceAtLeast(displayStart)
                                        dragEndValue = newEnd
                                        val range = displayStart..newEnd
                                        onChangeStart?.invoke(range)
                                        onValuesChange(range)
                                        onChangeEnd?.invoke(range)
                                    }
                                }
                            }
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Track background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(trackHeight)
                        .clip(RoundedCornerShape(trackHeight / 2))
                        .background(if (enabled) colors.muted else colors.muted)
                )

                // Active track (the span between the thumbs)
                val thumbRadiusPx = with(density) { thumbRadius.toPx() }
                val effectiveWidth = getEffectiveWidth()
                val startCenterPx = thumbRadiusPx + effectiveWidth * startNormalized
                val endCenterPx = thumbRadiusPx + effectiveWidth * endNormalized
                val activeStart = with(density) { startCenterPx.toDp() }
                val activeWidth = with(density) { (endCenterPx - startCenterPx).coerceAtLeast(0f).toDp() }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(trackHeight),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .offset(x = activeStart)
                            .width(activeWidth)
                            .height(trackHeight)
                            .clip(RoundedCornerShape(trackHeight / 2))
                            .background(if (enabled) colors.primary else colors.mutedForeground)
                    )
                }

                // Start thumb
                val startThumbOffsetX = with(density) {
                    val effectiveWidth = (sliderSize.width.toFloat() - thumbSize.toPx())
                    (effectiveWidth * startNormalized).toDp()
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = startThumbOffsetX)
                ) {
                    if (showThumbValue) {
                        Text(
                            text = formatValue(displayStart),
                            style = Typography.BodySmall,
                            color = if (enabled) colors.foreground else colors.mutedForeground,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-24).dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(thumbSize)
                            .shadow(elevation = if (enabled) Elevation.raised else Elevation.none, shape = CircleShape)
                            .clip(CircleShape)
                            .background(if (enabled) colors.surface else colors.muted)
                            .border(BorderWidth.thin, if (enabled) colors.border else colors.mutedForeground, CircleShape)
                            .then(
                                if (enabled) {
                                    Modifier.pointerInput(Unit) {
                                        detectDragGestures(
                                            onDragStart = {
                                                draggingThumb = 0
                                                dragStartValue = values.start
                                                dragEndValue = values.endInclusive
                                                onChangeStart?.invoke(values.start..values.endInclusive)
                                            },
                                            onDragEnd = {
                                                val snappedStart = snapValue(dragStartValue).coerceAtMost(dragEndValue)
                                                if (snappedStart != dragStartValue) {
                                                    dragStartValue = snappedStart
                                                    onValuesChange(snappedStart..dragEndValue)
                                                }
                                                draggingThumb = null
                                                onChangeEnd?.invoke(dragStartValue..dragEndValue)
                                            },
                                            onDragCancel = {
                                                draggingThumb = null
                                            }
                                        ) { change, dragAmount ->
                                            change.consume()
                                            val effectiveWidth = getEffectiveWidth()
                                            if (effectiveWidth <= 0f) return@detectDragGestures

                                            val deltaRatio = dragAmount.x / effectiveWidth
                                            val deltaValue =
                                                (valueRange.endInclusive - valueRange.start) * deltaRatio
                                            val newStart = (dragStartValue + deltaValue)
                                                .coerceIn(valueRange.start, dragEndValue)
                                                .coerceAtMost(dragEndValue)
                                            dragStartValue = newStart
                                            onValuesChange(newStart..dragEndValue)
                                        }
                                    }
                                } else Modifier
                            )
                    )
                }

                // End thumb
                val endThumbOffsetX = with(density) {
                    val effectiveWidth = (sliderSize.width.toFloat() - thumbSize.toPx())
                    (effectiveWidth * endNormalized).toDp()
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = endThumbOffsetX)
                ) {
                    if (showThumbValue) {
                        Text(
                            text = formatValue(displayEnd),
                            style = Typography.BodySmall,
                            color = if (enabled) colors.foreground else colors.mutedForeground,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-24).dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(thumbSize)
                            .shadow(elevation = if (enabled) Elevation.raised else Elevation.none, shape = CircleShape)
                            .clip(CircleShape)
                            .background(if (enabled) colors.surface else colors.muted)
                            .border(BorderWidth.thin, if (enabled) colors.border else colors.mutedForeground, CircleShape)
                            .then(
                                if (enabled) {
                                    Modifier.pointerInput(Unit) {
                                        detectDragGestures(
                                            onDragStart = {
                                                draggingThumb = 1
                                                dragStartValue = values.start
                                                dragEndValue = values.endInclusive
                                                onChangeStart?.invoke(values.start..values.endInclusive)
                                            },
                                            onDragEnd = {
                                                val snappedEnd = snapValue(dragEndValue).coerceAtLeast(dragStartValue)
                                                if (snappedEnd != dragEndValue) {
                                                    dragEndValue = snappedEnd
                                                    onValuesChange(dragStartValue..snappedEnd)
                                                }
                                                draggingThumb = null
                                                onChangeEnd?.invoke(dragStartValue..dragEndValue)
                                            },
                                            onDragCancel = {
                                                draggingThumb = null
                                            }
                                        ) { change, dragAmount ->
                                            change.consume()
                                            val effectiveWidth = getEffectiveWidth()
                                            if (effectiveWidth <= 0f) return@detectDragGestures

                                            val deltaRatio = dragAmount.x / effectiveWidth
                                            val deltaValue =
                                                (valueRange.endInclusive - valueRange.start) * deltaRatio
                                            val newEnd = (dragEndValue + deltaValue)
                                                .coerceIn(dragStartValue, valueRange.endInclusive)
                                                .coerceAtLeast(dragStartValue)
                                            dragEndValue = newEnd
                                            onValuesChange(dragStartValue..newEnd)
                                        }
                                    }
                                } else Modifier
                            )
                    )
                }
            }

            // Trailing label
            if (rightLabel != null) {
                Text(
                    text = rightLabel,
                    style = Typography.BodyMedium,
                    color = if (enabled) colors.foreground else colors.mutedForeground
                )
            }
        }

        // Tick labels
        if (showScaleValue && steps > 0) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = thumbRadius),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val stepCount = steps + 1
                for (i in 0..stepCount) {
                    val stepValue = valueRange.start + (valueRange.endInclusive - valueRange.start) * i / stepCount
                    Text(
                        text = formatValue(stepValue),
                        style = Typography.BodySmall,
                        color = if (enabled) colors.mutedForeground else colors.mutedForeground
                    )
                }
            }
        }
    }
}
