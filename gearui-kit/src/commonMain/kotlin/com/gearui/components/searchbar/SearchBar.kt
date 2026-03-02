package com.gearui.components.searchbar

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.text.BasicTextField
import com.tencent.kuikly.compose.foundation.text.KeyboardActions
import com.tencent.kuikly.compose.foundation.text.KeyboardOptions
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.focus.FocusRequester
import com.tencent.kuikly.compose.ui.focus.focusRequester
import com.tencent.kuikly.compose.ui.graphics.SolidColor
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.input.pointer.positionChange
import com.tencent.kuikly.compose.ui.platform.LocalFocusManager
import com.tencent.kuikly.compose.ui.platform.LocalSoftwareKeyboardController
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.text.input.ImeAction
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import kotlin.math.abs

/**
 * SearchBar - 100% Theme 驱动的搜索栏
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：Color(0x...) / 硬编码颜色
 *
 * 特性：
 * - 搜索输入框
 * - 清除按钮
 * - 搜索图标
 * - 取消按钮
 * - 占位符
 */
@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "搜索",
    enabled: Boolean = true,
    showCancel: Boolean = false,
    onCancel: (() -> Unit)? = null,
    onSearch: ((String) -> Unit)? = null,
    shape: SearchBarShape = SearchBarShape.ROUNDED,
    alignment: SearchBarAlignment = SearchBarAlignment.LEFT
) {
    // ⭐ Framework Rule #1: 第一行永远是这三个
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var focusRequestTick by remember { mutableStateOf(0) }

    LaunchedEffect(focusRequestTick, enabled) {
        if (focusRequestTick > 0 && enabled) {
            focusRequester.requestFocus()
        }
    }

    fun requestSearchFocus() {
        if (enabled) {
            focusRequestTick++
        }
    }

    val shapeModifier = when (shape) {
        SearchBarShape.ROUNDED -> shapes.round
        SearchBarShape.SQUARE -> shapes.small
    }

    val isCenter = alignment == SearchBarAlignment.CENTER

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 搜索框主体
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(shapeModifier)
                .background(if (enabled) colors.surfaceComponent else colors.disabledContainer)
                .border(1.dp, colors.border, shapeModifier)
                .pointerInput(enabled) {
                    if (enabled) {
                        val dragThreshold = 10f
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false)
                            var totalDrag = 0f
                            var isDragging = false

                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: break
                                if (!change.pressed) break

                                val delta = change.positionChange()
                                totalDrag += abs(delta.x) + abs(delta.y)
                                if (!isDragging && totalDrag > dragThreshold) {
                                    isDragging = true
                                    focusManager.clearFocus(force = true)
                                    keyboardController?.hide()
                                }
                            }

                            if (!isDragging) {
                                requestSearchFocus()
                            }
                        }
                    }
                }
                .clickable(enabled = enabled) { requestSearchFocus() }
        ) {
            // Focus catcher: full bordered area inside SearchBar can request focus.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = enabled) { requestSearchFocus() }
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 搜索图标
                Box(
                    modifier = if (onSearch != null && enabled) Modifier.clickable { onSearch(value) } else Modifier,
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        name = Icons.search,
                        size = 16.dp,
                        tint = colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = if (isCenter && value.isEmpty()) Alignment.Center else Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = Typography.BodyMedium,
                            color = colors.textPlaceholder
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = TextStyle(
                            fontSize = Typography.BodyMedium.fontSize,
                            fontWeight = Typography.BodyMedium.fontWeight,
                            color = if (enabled) colors.textPrimary else colors.textDisabled
                        ),
                        cursorBrush = SolidColor(colors.primary),
                        keyboardOptions = KeyboardOptions(
                            imeAction = if (onSearch != null) ImeAction.Search else ImeAction.Default
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                                onSearch?.invoke(value)
                            },
                            onDone = {
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                                onSearch?.invoke(value)
                            }
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )
                }

                // 清除按钮
                if (value.isNotEmpty() && enabled) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(shapes.circle)
                            .background(colors.surfaceVariant)
                            .clickable { onValueChange("") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            name = Icons.close,
                            size = 12.dp,
                            tint = colors.textSecondary
                        )
                    }
                }
            }
        }

        // 取消按钮
        if (showCancel) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "取消",
                style = Typography.BodyMedium,
                color = if (enabled) colors.primary else colors.textDisabled,
                modifier = Modifier.clickable(enabled = enabled) {
                    focusManager.clearFocus(force = true)
                    keyboardController?.hide()
                    onCancel?.invoke()
                }
            )
        }
    }
}

/**
 * SearchBarShape - 搜索框形状
 */
enum class SearchBarShape {
    /** 圆角矩形 */
    ROUNDED,

    /** 直角矩形 */
    SQUARE
}

/**
 * SearchBarAlignment - 搜索框对齐方式
 */
enum class SearchBarAlignment {
    /** 左对齐（默认） */
    LEFT,

    /** 居中对齐 */
    CENTER
}

/**
 * SearchBarWithAction - 带操作按钮的搜索栏
 */
@Composable
fun SearchBarWithAction(
    value: String,
    onValueChange: (String) -> Unit,
    actionText: String = "搜索",
    onAction: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "搜索",
    enabled: Boolean = true
) {
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBar(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .height(40.dp)
                .clip(shapes.small)
                .background(if (enabled) colors.primary else colors.disabledContainer)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = actionText,
                style = Typography.BodyMedium,
                color = if (enabled) colors.onPrimary else colors.textDisabled
            )
        }
    }
}
