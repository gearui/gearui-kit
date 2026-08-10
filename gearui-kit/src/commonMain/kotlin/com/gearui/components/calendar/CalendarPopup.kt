package com.gearui.components.calendar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonTheme
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import com.gearui.overlay.OverlayDefaults
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.typography.IconSizes

/**
 * CalendarPopup - 日历弹出层组件
 *
 * 以弹出层形式展示日历选择器
 *
 * 注意：此组件需要配合 OverlayRoot 使用
 *
 * Example:
 * ```kotlin
 * var showCalendar by remember { mutableStateOf(false) }
 * var selectedDate by remember { mutableStateOf<CalendarDate?>(null) }
 *
 * if (showCalendar) {
 *     CalendarPopup(
 *         visible = true,
 *         onClose = { showCalendar = false },
 *         onConfirm = { date ->
 *             selectedDate = date
 *             showCalendar = false
 *         }
 *     )
 * }
 * ```
 */
@Composable
fun CalendarPopup(
    visible: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = I18n.strings.dateTime.selectDateTitle,
    type: CalendarType = CalendarType.Single,
    // 单选
    initialDate: CalendarDate? = null,
    onConfirm: ((CalendarDate) -> Unit)? = null,
    // 多选
    initialDates: List<CalendarDate> = emptyList(),
    onConfirmMultiple: ((List<CalendarDate>) -> Unit)? = null,
    // 区间
    initialRangeStart: CalendarDate? = null,
    initialRangeEnd: CalendarDate? = null,
    onConfirmRange: ((CalendarDate?, CalendarDate?) -> Unit)? = null,
    // 通用配置
    minDate: CalendarDate? = null,
    maxDate: CalendarDate? = null,
    autoClose: Boolean = true,
    confirmText: String = I18n.strings.common.confirm,
    showConfirmButton: Boolean = true
) {
    if (!visible) return

    val colors = Theme.colors

    // 内部状态
    var selectedDate by remember(initialDate) { mutableStateOf(initialDate) }
    var selectedDates by remember(initialDates) { mutableStateOf(initialDates) }
    var rangeStart by remember(initialRangeStart) { mutableStateOf(initialRangeStart) }
    var rangeEnd by remember(initialRangeEnd) { mutableStateOf(initialRangeEnd) }

    // 遮罩层 + 内容
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 遮罩层
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OverlayDefaults.scrimColor)
                .clickable {
                    if (autoClose) onClose()
                }
        )

        // 日历内容 - 从底部弹出
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(OverlayDefaults.sheetShape)
                .background(colors.surface)
                .padding(Spacing.lg)
        ) {
            // 标题栏
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = Typography.TitleMedium,
                    color = colors.foreground
                )

                // 关闭按钮
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(Theme.shapes.xl)
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        name = Icons.close,
                        size = IconSizes.Default.md,
                        tint = colors.mutedForeground
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 日历组件
            Calendar(
                type = type,
                selectedDate = selectedDate,
                onDateSelect = { date ->
                    selectedDate = date
                    if (autoClose && !showConfirmButton) {
                        onConfirm?.invoke(date)
                        onClose()
                    }
                },
                selectedDates = selectedDates,
                onDatesChange = { dates ->
                    selectedDates = dates
                },
                rangeStart = rangeStart,
                rangeEnd = rangeEnd,
                onRangeSelect = { start, end ->
                    rangeStart = start
                    rangeEnd = end
                },
                minDate = minDate,
                maxDate = maxDate,
                showTitle = false
            )

            // 确认按钮
            if (showConfirmButton) {
                Spacer(modifier = Modifier.height(Spacing.lg))

                Button(
                    text = confirmText,
                    theme = ButtonTheme.PRIMARY,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        when (type) {
                            CalendarType.Single -> {
                                selectedDate?.let { onConfirm?.invoke(it) }
                            }
                            CalendarType.Multiple -> {
                                onConfirmMultiple?.invoke(selectedDates)
                            }
                            CalendarType.Range -> {
                                onConfirmRange?.invoke(rangeStart, rangeEnd)
                            }
                        }
                        if (autoClose) onClose()
                    }
                )
            }

            // 底部安全区域
            Spacer(modifier = Modifier.height(Spacing.lg))
        }
    }
}
