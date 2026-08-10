package com.gearui.components.input

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.shape.CircleShape
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
import com.tencent.kuikly.compose.ui.focus.onFocusChanged
import com.tencent.kuikly.compose.ui.graphics.SolidColor
import com.tencent.kuikly.compose.ui.input.pointer.PointerEventPass
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.platform.LocalFocusManager
import com.tencent.kuikly.compose.ui.platform.LocalSoftwareKeyboardController
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.text.input.ImeAction
import com.tencent.kuikly.compose.ui.text.input.KeyboardType
import com.tencent.kuikly.compose.ui.text.input.PasswordVisualTransformation
import com.tencent.kuikly.compose.ui.text.input.VisualTransformation
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.interaction.*
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.layout.Spacing

/**
 * GearUI Input - 100% Theme 驱动
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：ColorTokens / 硬编码颜色
 *
 * 支持功能：
 * - 基础输入
 * - 标签（左侧/顶部）
 * - 必填标记
 * - 前缀/后缀
 * - 清除按钮
 * - 字数限制
 * - 密码模式
 * - 多行输入
 * - 卡片样式
 * - 文本对齐
 * - 状态（正常/错误/禁用/只读）
 */
@Composable
fun Input(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: InputSize = InputSize.MEDIUM,
    placeholder: String = "",
    label: String? = null,
    labelPosition: String = "left", // "left" or "top"
    required: Boolean = false,
    helperText: String? = null,
    error: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    maxLength: Int? = null,
    showCounter: Boolean = false,
    maxLines: Int = 1,
    blurOnImeDone: Boolean = maxLines == 1,
    isPassword: Boolean = false,
    textAlign: TextAlign = TextAlign.Start,
    clearable: Boolean = false,
    onClear: (() -> Unit)? = null,
    cardStyle: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    onSend: (() -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null,
    autoFocus: Boolean = false,
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val interactionSource = remember { createMutableInteractionSource() }
    val inputFocusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    val hasError = error != null

    // 自动聚焦
    if (autoFocus) {
        LaunchedEffect(Unit) {
            inputFocusRequester.requestFocus()
        }
    }

    when {
        !enabled -> interactionSource.updateState(InteractionState.Disabled)
        hasError && !isFocused -> interactionSource.updateState(InteractionState.Normal)
        isFocused -> interactionSource.updateState(InteractionState.Focused)
        else -> interactionSource.updateState(InteractionState.Normal)
    }

    val tokens = when (size) {
        InputSize.LARGE -> FieldSizeTokens.Large
        InputSize.MEDIUM -> FieldSizeTokens.Medium
        InputSize.SMALL -> FieldSizeTokens.Small
    }

    val shape = when (size) {
        InputSize.LARGE -> FieldDefaults.shape
        InputSize.MEDIUM -> FieldDefaults.shape
        InputSize.SMALL -> FieldDefaults.compactShape
    }

    // 不让 borderColor 依赖 isFocused：Kuikly 下 modifier 链在 focus 瞬间重建，
    // 会让底层 EditText 被重建，恰好发生在 tap 处理过程中就会表现为"偶发失焦"。
    val borderColor = when {
        hasError -> colors.destructive
        else -> colors.border
    }

    // Keep border width stable to avoid layout jump when focus/error changes.
    val borderWidth = tokens.borderWidth

    val backgroundColor = when {
        !enabled -> colors.muted
        cardStyle -> colors.muted
        else -> colors.surface
    }

    // 输入框内容
    @Composable
    fun InputField() {
        val canFocus = enabled && !readOnly
        var focusRequestTick by remember { mutableStateOf(0) }

        LaunchedEffect(focusRequestTick, canFocus) {
            if (focusRequestTick > 0 && canFocus) {
                inputFocusRequester.requestFocus()
            }
        }

        fun requestInputFocus() {
            if (canFocus) {
                focusRequestTick++
            }
        }

        // Width comes from the field tokens and is never zero, so this is not
        // conditional. It was already constant-true before (borderWidth = 1f).
        val borderModifier = Modifier.border(borderWidth, borderColor, shape)

        val containerModifier = if (cardStyle) {
            Modifier
                .fillMaxWidth()
                .heightIn(min = tokens.height)
                .clip(shape)
                .background(backgroundColor)
                .then(borderModifier)
        } else {
            Modifier
                .fillMaxWidth()
                .height(tokens.height)
                .clip(shape)
                .background(backgroundColor)
                .then(borderModifier)
        }

        // 关键：用 pointerInput + requireUnconsumed=false 拦住所有 tap。
        // Compose 的 clickable 在子 composable（BasicTextField）消费事件时不会触发，
        // 所以点击输入框本体时外层 clickable 完全收不到事件 —— 靠 clickable 抢焦点
        // 只能覆盖 padding 点击，无法兜底 Kuikly EditText 内部的偶发失焦。
        // 用 pointerInput 忽略消费状态，在 UP 时无条件 requestInputFocus 抢回焦点。
        Box(
            modifier = containerModifier
                .pointerInput(canFocus) {
                    if (!canFocus) return@pointerInput
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id }
                            if (change == null || !change.pressed) {
                                requestInputFocus()
                                break
                            }
                        }
                    }
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = tokens.paddingHorizontal,
                        vertical = if (cardStyle) 12.dp else 0.dp
                    ),
                verticalAlignment = if (maxLines > 1) Alignment.Top else Alignment.CenterVertically
            ) {
                // 左侧标签（labelPosition == "left" 时）
                if (label != null && labelPosition == "left") {
                    Row {
                        if (required) {
                            Text(
                                text = "*",
                                style = Typography.BodyMedium,
                                color = colors.destructive
                            )
                        }
                        Text(
                            text = label,
                            style = Typography.BodyMedium,
                            color = if (!enabled) colors.mutedForeground else colors.foreground
                        )
                    }
                    Spacer(modifier = Modifier.width(Spacing.md))
                }

                // 前缀
                if (prefix != null) {
                    prefix()
                    Spacer(modifier = Modifier.width(Spacing.sm))
                }

                // 输入区域
                // 架构要点：
                // 1) BasicTextField 用 fillMaxWidth，不要 fillMaxSize，否则 tap 落不到外层。
                // 2) placeholder 放回 decorationBox 里：它是 BasicTextField 自己的渲染树，
                //    tap 打在 placeholder 上和 tap 打在 innerTextField 上被 BasicTextField 统一处理，
                //    不会像 sibling Text 那样截走焦点。
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = when (textAlign) {
                        TextAlign.Center -> Alignment.Center
                        TextAlign.End -> Alignment.CenterEnd
                        else -> Alignment.CenterStart
                    }
                ) {
                    BasicTextField(
                        value = value,
                        onValueChange = { newValue ->
                            if (!readOnly && enabled) {
                                if (maxLength == null || newValue.length <= maxLength) {
                                    onValueChange(newValue)
                                }
                            }
                        },
                        textStyle = TextStyle(
                            fontSize = Typography.BodyMedium.fontSize,
                            fontWeight = Typography.BodyMedium.fontWeight,
                            color = if (!enabled) colors.mutedForeground else colors.foreground,
                            textAlign = textAlign
                        ),
                        cursorBrush = SolidColor(colors.primary),
                        keyboardOptions = KeyboardOptions(
                            // isPassword 必须走 KeyboardType.Password:Kuikly iOS 的掩码
                            // 通道是原生 secureTextEntry(由 keyboardType=password 触发),
                            // visualTransformation 在 Kuikly 桥接下不生效。
                            keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
                            imeAction = when {
                                onSend != null -> ImeAction.Send
                                maxLines == 1 -> ImeAction.Done
                                else -> ImeAction.Default
                            }
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = { onSend?.invoke() },
                            onDone = {
                                if (blurOnImeDone && maxLines == 1) {
                                    focusManager.clearFocus(force = true)
                                    keyboardController?.hide()
                                }
                            }
                        ),
                        singleLine = maxLines == 1,
                        maxLines = maxLines,
                        readOnly = readOnly,
                        enabled = enabled,
                        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(inputFocusRequester)
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                                onFocusChanged?.invoke(focusState.isFocused)
                            },
                        decorationBox = { innerTextField ->
                            Box(
                                contentAlignment = when (textAlign) {
                                    TextAlign.Center -> Alignment.Center
                                    TextAlign.End -> Alignment.CenterEnd
                                    else -> Alignment.CenterStart
                                }
                            ) {
                                if (value.isEmpty() && placeholder.isNotEmpty()) {
                                    Text(
                                        text = placeholder,
                                        style = Typography.BodyMedium,
                                        color = colors.mutedForeground
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                // 清除按钮
                // 用 pointerInput 在 Initial pass 消费 down 事件，避免事件冒到底层 native
                // EditText 触发"失焦→IME 隐藏→再 requestFocus→IME 弹出"的可见闪烁。
                // 只在 tap（非拖动）时触发清除；点完再 requestInputFocus 以防万一。
                if (clearable && value.isNotEmpty() && enabled && !readOnly) {
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(colors.muted)
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    val down = awaitFirstDown(
                                        requireUnconsumed = false,
                                        pass = PointerEventPass.Initial,
                                    )
                                    down.consume()
                                    while (true) {
                                        val event = awaitPointerEvent(PointerEventPass.Initial)
                                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                        change.consume()
                                        if (!change.pressed) {
                                            onClear?.invoke()
                                            onValueChange("")
                                            requestInputFocus()
                                            break
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            name = Icons.close,
                            size = 12.dp,
                            tint = colors.mutedForeground
                        )
                    }
                }

                // 后缀
                if (suffix != null) {
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    suffix()
                }

                // 字数统计
                if (showCounter && maxLength != null) {
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = "${value.length}/$maxLength",
                        style = Typography.BodySmall,
                        color = if (value.length >= maxLength) colors.destructive else colors.mutedForeground
                    )
                }
            }
        }
    }

    // 主布局
    Column(modifier = modifier) {
        // 顶部标签（labelPosition == "top" 时）
        if (label != null && labelPosition == "top") {
            Row(modifier = Modifier.padding(bottom = Spacing.sm)) {
                if (required) {
                    Text(
                        text = "*",
                        style = Typography.BodyMedium,
                        color = colors.destructive
                    )
                }
                Text(
                    text = label,
                    style = Typography.BodyMedium,
                    color = if (!enabled) colors.mutedForeground else colors.foreground
                )
            }
        }

        InputField()

        // 底部提示文字
        val bottomText = error ?: helperText
        if (bottomText != null) {
            Text(
                text = bottomText,
                style = Typography.BodySmall,
                color = if (hasError) colors.destructive else colors.mutedForeground,
                modifier = Modifier.padding(top = Spacing.xs)
            )
        }
    }
}

enum class InputSize { LARGE, MEDIUM, SMALL }
