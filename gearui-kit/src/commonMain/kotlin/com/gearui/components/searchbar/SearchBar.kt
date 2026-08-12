package com.gearui.components.searchbar

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
import com.gearui.i18n.I18n
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.typography.IconSizes

/**
 * SearchBar - fully Theme-driven search bar
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: Color(0x...) or hardcoded colours
 *
 * Features:
 * - search input
 * - clear button
 * - search icon
 * - cancel button
 * - placeholder
 */
@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = I18n.strings.field.searchPlaceholder,
    enabled: Boolean = true,
    showCancel: Boolean = false,
    onCancel: (() -> Unit)? = null,
    onSearch: ((String) -> Unit)? = null,
    shape: SearchBarShape = SearchBarShape.ROUNDED,
    alignment: SearchBarAlignment = SearchBarAlignment.LEFT
) {
    // ⭐ Framework Rule #1: these three are always the first lines
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
        SearchBarShape.ROUNDED -> shapes.full
        SearchBarShape.SQUARE -> FieldDefaults.shape
    }

    val isCenter = alignment == SearchBarAlignment.CENTER

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(FieldSizeTokens.Medium.height),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search box body
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(shapeModifier)
                .background(if (enabled) colors.surface else colors.muted)
                .border(BorderWidth.thin, colors.border, shapeModifier)
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
                    .padding(horizontal = Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search icon
                Box(
                    modifier = if (onSearch != null && enabled) Modifier.clickable { onSearch(value) } else Modifier,
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        name = Icons.search,
                        size = FieldDefaults.trailingIconSize,
                        tint = colors.mutedForeground
                    )
                }

                Spacer(modifier = Modifier.width(Spacing.sm))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = if (isCenter && value.isEmpty()) Alignment.Center else Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = Typography.BodyMedium,
                            color = colors.mutedForeground
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = TextStyle(
                            fontSize = Typography.BodyMedium.fontSize,
                            fontWeight = Typography.BodyMedium.fontWeight,
                            color = if (enabled) colors.foreground else colors.mutedForeground
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

                // Clear button
                if (value.isNotEmpty() && enabled) {
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(colors.muted)
                            .clickable { onValueChange("") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            name = Icons.close,
                            size = IconSizes.Default.xs,
                            tint = colors.mutedForeground
                        )
                    }
                }
            }
        }

        // Cancel button
        if (showCancel) {
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = I18n.strings.common.cancel,
                style = Typography.BodyMedium,
                color = if (enabled) colors.primary else colors.mutedForeground,
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
 * SearchBarShape - search box shape
 */
enum class SearchBarShape {
    /** rounded rectangle */
    ROUNDED,

    /** square corners */
    SQUARE
}

/**
 * SearchBarAlignment - search box alignment
 */
enum class SearchBarAlignment {
    /** leading (default) */
    LEFT,

    /** centred */
    CENTER
}

/**
 * SearchBarWithAction - search bar with an action button
 */
@Composable
fun SearchBarWithAction(
    value: String,
    onValueChange: (String) -> Unit,
    actionText: String = I18n.strings.common.search,
    onAction: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = I18n.strings.field.searchPlaceholder,
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

        Spacer(modifier = Modifier.width(Spacing.sm))

        Box(
            modifier = Modifier
                .height(40.dp)
                .clip(shapes.sm)
                .background(if (enabled) colors.primary else colors.muted)
                .padding(horizontal = Spacing.lg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = actionText,
                style = Typography.BodyMedium,
                color = if (enabled) colors.primaryForeground else colors.mutedForeground
            )
        }
    }
}
