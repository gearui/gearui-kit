package com.gearui.components.picker

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
import com.gearui.components.dialog.Dialog
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * DatePicker - 100% Theme 驱动的日期选择器
 */
@Composable
fun DatePickerInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = "选择日期",
    label: String? = null,
    format: String = "YYYY-MM-DD"
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    var showPicker by remember { mutableStateOf(false) }

    // 解析当前值
    val parts = value.split("-")
    var selectedYear by remember(value) { mutableStateOf(parts.getOrNull(0)?.toIntOrNull() ?: 2024) }
    var selectedMonth by remember(value) { mutableStateOf(parts.getOrNull(1)?.toIntOrNull() ?: 1) }
    var selectedDay by remember(value) { mutableStateOf(parts.getOrNull(2)?.toIntOrNull() ?: 1) }

    Column(modifier = modifier) {
        // 标签
        if (label != null) {
            Text(
                text = label,
                style = Typography.BodyMedium,
                color = if (enabled) colors.textPrimary else colors.textDisabled,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // 输入触发器
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(shapes.small)
                .border(1.dp, if (enabled) colors.stroke else colors.disabled, shapes.small)
                .background(if (enabled) colors.surface else colors.disabledContainer)
                .clickable(enabled = enabled) { showPicker = true }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = value.ifEmpty { placeholder },
                style = Typography.BodyMedium,
                color = if (value.isNotEmpty()) {
                    if (enabled) colors.textPrimary else colors.textDisabled
                } else {
                    colors.textPlaceholder
                }
            )

            Text(
                text = "📅",
                style = Typography.BodyMedium
            )
        }
    }

    // 日期选择对话框
    Dialog.Host(
        visible = showPicker,
        onDismiss = { showPicker = false }
    ) {
        DatePickerDialogContent(
            selectedYear = selectedYear,
            selectedMonth = selectedMonth,
            selectedDay = selectedDay,
            onYearChange = { selectedYear = it },
            onMonthChange = { selectedMonth = it },
            onDayChange = { selectedDay = it },
            onConfirm = {
                val maxDay = when (selectedMonth) {
                    2 -> if (selectedYear % 4 == 0) 29 else 28
                    4, 6, 9, 11 -> 30
                    else -> 31
                }
                val formattedDate = "${selectedYear.toString().padStart(4, '0')}-${selectedMonth.toString().padStart(2, '0')}-${selectedDay.coerceAtMost(maxDay).toString().padStart(2, '0')}"
                onValueChange(formattedDate)
                showPicker = false
            },
            onCancel = { showPicker = false }
        )
    }
}

@Composable
private fun DatePickerDialogContent(
    selectedYear: Int,
    selectedMonth: Int,
    selectedDay: Int,
    onYearChange: (Int) -> Unit,
    onMonthChange: (Int) -> Unit,
    onDayChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "选择日期",
            style = Typography.TitleMedium,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 年月日选择器
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 年份选择
            IntPickerColumn(
                items = (2020..2030).toList(),
                selectedItem = selectedYear,
                onItemSelected = onYearChange,
                modifier = Modifier.weight(1f),
                suffix = "年"
            )

            // 月份选择
            IntPickerColumn(
                items = (1..12).toList(),
                selectedItem = selectedMonth,
                onItemSelected = onMonthChange,
                modifier = Modifier.weight(1f),
                suffix = "月"
            )

            // 日期选择
            val daysInMonth = when (selectedMonth) {
                2 -> if (selectedYear % 4 == 0) 29 else 28
                4, 6, 9, 11 -> 30
                else -> 31
            }
            IntPickerColumn(
                items = (1..daysInMonth).toList(),
                selectedItem = selectedDay.coerceAtMost(daysInMonth),
                onItemSelected = onDayChange,
                modifier = Modifier.weight(1f),
                suffix = "日"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(shapes.small)
                    .border(1.dp, colors.stroke, shapes.small)
                    .clickable { onCancel() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "取消",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(shapes.small)
                    .background(colors.primary)
                    .clickable { onConfirm() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "确定",
                    style = Typography.BodyMedium,
                    color = colors.onPrimary
                )
            }
        }
    }
}

/**
 * TimePicker - 时间选择器
 */
@Composable
fun TimePickerInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = "选择时间",
    label: String? = null,
    format: String = "HH:mm"
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    var showPicker by remember { mutableStateOf(false) }

    // 解析当前值
    val parts = value.split(":")
    var selectedHour by remember(value) { mutableStateOf(parts.getOrNull(0)?.toIntOrNull() ?: 0) }
    var selectedMinute by remember(value) { mutableStateOf(parts.getOrNull(1)?.toIntOrNull() ?: 0) }

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = Typography.BodyMedium,
                color = if (enabled) colors.textPrimary else colors.textDisabled,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(shapes.small)
                .border(1.dp, if (enabled) colors.stroke else colors.disabled, shapes.small)
                .background(if (enabled) colors.surface else colors.disabledContainer)
                .clickable(enabled = enabled) { showPicker = true }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = value.ifEmpty { placeholder },
                style = Typography.BodyMedium,
                color = if (value.isNotEmpty()) {
                    if (enabled) colors.textPrimary else colors.textDisabled
                } else {
                    colors.textPlaceholder
                }
            )

            Text(
                text = "🕐",
                style = Typography.BodyMedium
            )
        }
    }

    // 时间选择对话框
    Dialog.Host(
        visible = showPicker,
        onDismiss = { showPicker = false }
    ) {
        TimePickerDialogContent(
            selectedHour = selectedHour,
            selectedMinute = selectedMinute,
            onHourChange = { selectedHour = it },
            onMinuteChange = { selectedMinute = it },
            onConfirm = {
                val formattedTime = "${selectedHour.toString().padStart(2, '0')}:${selectedMinute.toString().padStart(2, '0')}"
                onValueChange(formattedTime)
                showPicker = false
            },
            onCancel = { showPicker = false }
        )
    }
}

@Composable
private fun TimePickerDialogContent(
    selectedHour: Int,
    selectedMinute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "选择时间",
            style = Typography.TitleMedium,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 时分选择器
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 小时选择
            IntPickerColumn(
                items = (0..23).toList(),
                selectedItem = selectedHour,
                onItemSelected = onHourChange,
                modifier = Modifier.weight(1f),
                suffix = "时",
                padZero = true
            )

            // 分钟选择
            IntPickerColumn(
                items = (0..59).toList(),
                selectedItem = selectedMinute,
                onItemSelected = onMinuteChange,
                modifier = Modifier.weight(1f),
                suffix = "分",
                padZero = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(shapes.small)
                    .border(1.dp, colors.stroke, shapes.small)
                    .clickable { onCancel() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "取消",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(shapes.small)
                    .background(colors.primary)
                    .clickable { onConfirm() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "确定",
                    style = Typography.BodyMedium,
                    color = colors.onPrimary
                )
            }
        }
    }
}

/**
 * 整数选择器列 - 可滚动的选项列表
 */
@Composable
private fun IntPickerColumn(
    items: List<Int>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    suffix: String = "",
    padZero: Boolean = false
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val itemHeight = 40.dp
    val itemHeightPx = with(density) { itemHeight.toPx() }
    val centerPadding = itemHeight * 2

    // 滚动到选中项
    LaunchedEffect(selectedItem) {
        val index = items.indexOf(selectedItem)
        if (index >= 0 && !listState.isScrollInProgress) {
            listState.animateScrollToItem(index)
        }
    }

    // 滚动结束时吸附到最近项，并更新选中值（标准交互）
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress && items.isNotEmpty()) {
            val firstIndex = listState.firstVisibleItemIndex
            val firstOffset = listState.firstVisibleItemScrollOffset
            val nearest = (firstIndex + (firstOffset / itemHeightPx).roundToInt())
                .coerceIn(0, items.lastIndex)
            listState.animateScrollToItem(nearest)
            val value = items[nearest]
            if (value != selectedItem) {
                onItemSelected(value)
            }
        }
    }

    Box(
        modifier = modifier
            .clip(shapes.small)
            .background(colors.surfaceVariant)
    ) {
        // 居中选中高亮层（统一视觉层次）
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .height(itemHeight)
                .clip(shapes.default)
                .background(colors.surface)
                .border(1.dp, colors.stroke, shapes.default)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = centerPadding)
        ) {
            itemsIndexed(items) { index, item ->
                val isSelected = item == selectedItem
                val displayText = if (padZero) "${item.toString().padStart(2, '0')}$suffix" else "$item$suffix"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .background(colors.surfaceVariant)
                        .clickable {
                            scope.launch {
                                // Tap first scrolls into selection window,
                                // then final selected value is committed by snap logic.
                                listState.animateScrollToItem(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayText,
                        style = if (isSelected) Typography.TitleSmall else Typography.BodyMedium,
                        color = if (isSelected) colors.textPrimary else colors.textSecondary
                    )
                }
            }
        }
    }
}

/**
 * DateTimePicker - 日期时间选择器
 */
@Composable
fun DateTimePickerInput(
    dateValue: String,
    timeValue: String,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null
) {
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = Typography.BodyMedium,
                color = if (enabled) Theme.colors.textPrimary else Theme.colors.textDisabled,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DatePickerInput(
                value = dateValue,
                onValueChange = onDateChange,
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )

            TimePickerInput(
                value = timeValue,
                onValueChange = onTimeChange,
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
