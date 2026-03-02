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
import com.gearui.Spacing
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Slider 样式类型
 */
enum class SliderStyle {
    NORMAL,   // 普通样式
    CAPSULE   // 胶囊样式
}

/**
 * Slider - 滑动选择器
 *
 * 特性：
 * - 单值滑动选择
 * - 左右标签
 * - 自定义范围
 * - 步进值（分段）
 * - 禁用状态
 * - 显示刻度值
 * - 显示当前值
 * - 普通/胶囊样式
 * - 点击轨道跳转
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

    // 计算标准化值 (0-1)
    val normalizedValue = ((displayValue - valueRange.start) / (valueRange.endInclusive - valueRange.start))
        .coerceIn(0f, 1f)

    // 轨道参数
    val trackHeight = if (style == SliderStyle.CAPSULE) 24.dp else 4.dp
    val thumbSize = if (style == SliderStyle.CAPSULE) 18.dp else 20.dp
    val thumbRadius = thumbSize / 2

    // 计算有效轨道宽度（减去滑块半径的两边）
    fun getEffectiveWidth(): Float {
        return (sliderSize.width - with(density) { thumbSize.toPx() }).coerceAtLeast(0f)
    }

    // 根据位置计算值
    fun calculateValue(positionX: Float): Float {
        val effectiveWidth = getEffectiveWidth()
        if (effectiveWidth <= 0) return valueRange.start

        val thumbPx = with(density) { thumbRadius.toPx() }
        val adjustedX = (positionX - thumbPx).coerceIn(0f, effectiveWidth)
        val ratio = adjustedX / effectiveWidth

        var newValue = valueRange.start + (valueRange.endInclusive - valueRange.start) * ratio

        // 如果有步进，对齐到最近的步进值
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

    // 格式化显示值
    fun formatValue(v: Float): String {
        return if (v == v.roundToInt().toFloat()) {
            v.roundToInt().toString()
        } else {
            val rounded = (v * 10).roundToInt() / 10.0
            rounded.toString()
        }
    }

    Column(modifier = modifier) {
        // 显示当前值（在滑块上方）
        if (showThumbValue) {
            Spacer(modifier = Modifier.height(20.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧标签
            if (leftLabel != null) {
                Text(
                    text = leftLabel,
                    style = Typography.BodyMedium,
                    color = if (enabled) colors.textPrimary else colors.textDisabled
                )
            }

            // 滑块容器
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
                // 轨道
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

                // 滑块
                val thumbOffsetX = with(density) {
                    val effectiveWidth = (sliderSize.width.toFloat() - thumbSize.toPx())
                    (effectiveWidth * normalizedValue).toDp()
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = thumbOffsetX)
                ) {
                    // 当前值显示（在滑块上方）
                    if (showThumbValue) {
                        Text(
                            text = formatValue(displayValue),
                            style = Typography.BodySmall,
                            color = if (enabled) colors.textPrimary else colors.textDisabled,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-24).dp)
                        )
                    }

                    // 滑块本体
                    Box(
                        modifier = Modifier
                            .size(thumbSize)
                            .shadow(
                                elevation = if (enabled) 4.dp else 0.dp,
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .background(if (enabled) colors.surface else colors.disabledContainer)
                            .border(
                                width = 1.dp,
                                color = if (enabled) colors.border else colors.disabled,
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

            // 右侧标签
            if (rightLabel != null) {
                Text(
                    text = rightLabel,
                    style = Typography.BodyMedium,
                    color = if (enabled) colors.textPrimary else colors.textDisabled
                )
            }
        }

        // 刻度值显示
        if (showScaleValue && steps > 0) {
            Spacer(modifier = Modifier.height(4.dp))
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
                        color = if (enabled) colors.textSecondary else colors.textDisabled
                    )
                }
            }
        }
    }
}

/**
 * 普通样式轨道
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
        // 非激活轨道（背景）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(RoundedCornerShape(trackHeight / 2))
                .background(if (enabled) colors.surfaceVariant else colors.disabledContainer)
        )

        // 激活轨道
        Box(
            modifier = Modifier
                .fillMaxWidth(normalizedValue.coerceAtLeast(0.001f))
                .height(trackHeight)
                .clip(RoundedCornerShape(trackHeight / 2))
                .background(if (enabled) colors.primary else colors.disabled)
        )

        // 刻度点
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
                                    if (isActive) colors.primary else colors.surfaceVariant
                                } else {
                                    colors.disabledContainer
                                }
                            )
                    )
                }
            }
        }
    }
}

/**
 * 胶囊样式轨道
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
        // 非激活轨道（背景）- 胶囊形状
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(RoundedCornerShape(trackHeight / 2))
                .background(if (enabled) colors.surfaceVariant else colors.disabledContainer)
        )

        // 激活轨道 - 内缩一点以留出滑块空间
        val innerPadding = 3.dp
        Box(
            modifier = Modifier
                .padding(horizontal = innerPadding, vertical = innerPadding)
                .fillMaxWidth(normalizedValue.coerceAtLeast(0.001f))
                .height(trackHeight - innerPadding * 2)
                .clip(RoundedCornerShape((trackHeight - innerPadding * 2) / 2))
                .background(if (enabled) colors.primary else colors.disabled)
        )
    }
}

/**
 * RangeSlider - 范围选择滑块
 *
 * 特性：
 * - 双滑块范围选择
 * - 左右标签
 * - 自定义范围
 * - 步进值
 * - 禁用状态
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

    // 计算标准化值
    val startNormalized = ((displayStart - valueRange.start) / (valueRange.endInclusive - valueRange.start))
        .coerceIn(0f, 1f)
    val endNormalized = ((displayEnd - valueRange.start) / (valueRange.endInclusive - valueRange.start))
        .coerceIn(0f, 1f)

    val thumbSize = 20.dp
    val thumbRadius = thumbSize / 2
    val trackHeight = 4.dp

    // 计算有效轨道宽度
    fun getEffectiveWidth(): Float {
        return (sliderSize.width - with(density) { thumbSize.toPx() }).coerceAtLeast(0f)
    }

    // 根据位置计算值
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

    // 格式化显示值
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
            horizontalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧标签
            if (leftLabel != null) {
                Text(
                    text = leftLabel,
                    style = Typography.BodyMedium,
                    color = if (enabled) colors.textPrimary else colors.textDisabled
                )
            }

            // 滑块容器
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
                // 轨道背景
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(trackHeight)
                        .clip(RoundedCornerShape(trackHeight / 2))
                        .background(if (enabled) colors.surfaceVariant else colors.disabledContainer)
                )

                // 激活轨道（中间部分）
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
                            .background(if (enabled) colors.primary else colors.disabled)
                    )
                }

                // 起始滑块
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
                            color = if (enabled) colors.textPrimary else colors.textDisabled,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-24).dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(thumbSize)
                            .shadow(elevation = if (enabled) 4.dp else 0.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(if (enabled) colors.surface else colors.disabledContainer)
                            .border(1.dp, if (enabled) colors.border else colors.disabled, CircleShape)
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

                // 结束滑块
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
                            color = if (enabled) colors.textPrimary else colors.textDisabled,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-24).dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(thumbSize)
                            .shadow(elevation = if (enabled) 4.dp else 0.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(if (enabled) colors.surface else colors.disabledContainer)
                            .border(1.dp, if (enabled) colors.border else colors.disabled, CircleShape)
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

            // 右侧标签
            if (rightLabel != null) {
                Text(
                    text = rightLabel,
                    style = Typography.BodyMedium,
                    color = if (enabled) colors.textPrimary else colors.textDisabled
                )
            }
        }

        // 刻度值显示
        if (showScaleValue && steps > 0) {
            Spacer(modifier = Modifier.height(4.dp))
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
                        color = if (enabled) colors.textSecondary else colors.textDisabled
                    )
                }
            }
        }
    }
}
