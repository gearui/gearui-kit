package com.gearui.components.searchbar
import com.gearui.components.icon.*
import com.gearui.foundation.interaction.touchTarget
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonType
import com.gearui.components.button.Button
import com.gearui.foundation.field.fieldName
import com.tencent.kuikly.compose.ui.semantics.semantics
import com.tencent.kuikly.compose.ui.semantics.contentDescription
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.foundation.interaction.collectIsPressedAsState
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.gearui.foundation.motion.iconPressFeedback
import com.gearui.foundation.field.FieldVariant
import com.gearui.foundation.field.fill
import com.gearui.foundation.field.shadowed
import com.gearui.foundation.field.FieldSurface
import com.gearui.foundation.typography.resolveFontFamily

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.gestures.detectTapGestures
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.text.BasicTextField
import com.tencent.kuikly.compose.foundation.text.KeyboardActions
import com.tencent.kuikly.compose.foundation.text.KeyboardOptions
import com.tencent.kuikly.compose.ui.text.input.KeyboardCapitalization
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
import com.gearui.foundation.keyboard.keyboardDismissExempt
import androidx.compose.runtime.mutableStateOf
import com.tencent.kuikly.compose.ui.focus.onFocusChanged
import com.gearui.theme.Theme
import com.gearui.theme.LocalInputColors
import com.gearui.foundation.field.FieldFocusOverlay
import com.gearui.foundation.field.rememberInputFeedback
import kotlin.math.abs
import com.gearui.i18n.I18n
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.typography.IconSizes
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.FeedbackDefaults
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer

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
/**
 * When the Cancel button is shown.
 *
 * [WhileEditing] is the default because it is the platform behaviour GearUI
 * takes as its reference: Cancel arrives when the field takes focus and leaves
 * when it gives it up. A permanently visible Cancel is a control that does
 * nothing most of the time; a permanently absent one leaves no way out of a
 * search but the back gesture.
 */
internal fun cancelVisible(mode: SearchBarCancel, focused: Boolean): Boolean =
    when (mode) {
        SearchBarCancel.Never -> false
        SearchBarCancel.Always -> true
        SearchBarCancel.WhileEditing -> focused
    }

enum class SearchBarCancel {
    /** Never. For a search field embedded in a page that has its own way back. */
    Never,

    /** While the field has focus. The platform behaviour, and the default. */
    WhileEditing,

    /** Always. For a dedicated search page where Cancel is the way out. */
    Always,
}

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = I18n.strings.field.searchPlaceholder,
    enabled: Boolean = true,
    /** When Cancel shows; see [SearchBarCancel]. */
    cancel: SearchBarCancel = SearchBarCancel.WhileEditing,
    onCancel: (() -> Unit)? = null,
    onSearch: ((String) -> Unit)? = null,
    shape: SearchBarShape = SearchBarShape.ROUNDED,
    alignment: SearchBarAlignment = SearchBarAlignment.LEFT,
    /** Focus and raise the keyboard on entry (the right behaviour for a search page — the user came to type). */
    autoFocus: Boolean = false,
    /** PRIMARY on the page background; SECONDARY (the default, where a search bar usually sits) on a card, sheet or header. */
    variant: FieldVariant = FieldVariant.SECONDARY,
) {
    // ⭐ Framework Rule #1: these three are always the first lines
    val colors = Theme.colors
    val shapes = Theme.shapes
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusedState = remember { mutableStateOf(false) }
    var isFocused by focusedState
    val hoveredState = remember { mutableStateOf(false) }
    val inputColors = LocalInputColors.current
    var focusRequestTick by remember { mutableStateOf(0) }

    LaunchedEffect(enabled) {
        if (!enabled && isFocused) {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
        }
    }

    LaunchedEffect(focusRequestTick, enabled) {
        if (focusRequestTick > 0 && enabled) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(Unit) {
        if (autoFocus && enabled) {
            // Wait for the textarea to finish composition before requesting focus (same timing trap as MessagePage voice-to-text).
            kotlinx.coroutines.delay(80)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    fun requestSearchFocus() {
        if (enabled) {
            focusRequestTick++
        }
    }

    val shapeModifier = when (shape) {
        SearchBarShape.ROUNDED -> FieldDefaults.shape
        SearchBarShape.SQUARE -> shapes.none
    }

    val feedback = rememberInputFeedback(inputColors, shapeModifier, focusedState, hoveredState, enabled, null)
    val searchLabel = I18n.strings.common.search
    val clearLabel = I18n.strings.field.clear

    val isCenter = alignment == SearchBarAlignment.CENTER

    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = if (enabled) 1f else FeedbackDefaults.disabledOpacity }
            .height(FieldSizeTokens.Medium.height),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search box body
        FieldSurface(
            Modifier.weight(1f).fillMaxHeight(),
            shape = shapeModifier,
            shadowed = variant.shadowed,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(feedback)
                    .clip(shapeModifier)
                    .background(if (variant == FieldVariant.SECONDARY) colors.muted else variant.fill(inputColors.background, enabled))
                    .border(BorderWidth.thin, inputColors.border, shapeModifier)
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
            ) {
                // Focus catcher: a tap anywhere in the bordered area focuses the field. A gesture,
                // not a clickable, so it adds no unlabeled button to the accessibility tree; the
                // text field itself is what assistive technology focuses.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(enabled) { detectTapGestures { if (enabled) requestSearchFocus() } }
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search icon
                    Box(
                        modifier = if (onSearch != null && enabled) {
                            // A 16dp glyph, touched over 44dp; the field after it wins where they overlap.
                            Modifier.touchTarget(FieldDefaults.trailingIconSize)
                                .semantics { contentDescription = searchLabel }.clickable(role = Role.Button) { onSearch(value) }
                        } else Modifier,
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.magnifyingGlass,
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
                                style = Theme.typography.bodyMedium,
                                color = inputColors.placeholder
                            )
                        }

                        BasicTextField(
                            value = value,
                            onValueChange = { if (enabled) onValueChange(it) },
                            enabled = enabled,
                            textStyle = TextStyle(
                                fontSize = Theme.typography.bodyMedium.fontSize,
                                fontWeight = Theme.typography.bodyMedium.fontWeight,
                                fontFamily = Theme.typography.bodyMedium.resolveFontFamily(),
                                letterSpacing = Theme.typography.bodyMedium.letterSpacing,
                                color = inputColors.foreground
                            ),
                            cursorBrush = SolidColor(inputColors.focusRing),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
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
                            // onFocusChanged sits on a chain that does not itself
                            // depend on focus. Input.kt records why that distinction
                            // matters: a chain rebuilt *because* focus changed
                            // recreates the underlying EditText.
                            modifier = Modifier.fieldName(placeholder).keyboardDismissExempt()
                                .fillMaxWidth()
                                .onFocusChanged { isFocused = it.isFocused }
                                .focusRequester(focusRequester)
                        )
                    }

                    // Clear button
                    // Keep the input's measured width stable while editing.
                    run {
                        val clearInteraction = remember { MutableInteractionSource() }
                        val clearPressed by clearInteraction.collectIsPressedAsState()
                        Spacer(modifier = Modifier.width(Spacing.md))
                        Box(
                            modifier = Modifier
                                .size(ControlGeometry.searchClearSize)
                                .graphicsLayer { alpha = if (value.isNotEmpty() && enabled) 1f else 0f }
                                .iconPressFeedback(clearPressed, ControlGeometry.searchClearSize, CircleShape)
                                .semantics { contentDescription = clearLabel }
                                .clickable(
                                    role = Role.Button,
                                    enabled = enabled && value.isNotEmpty(),
                                    interactionSource = clearInteraction,
                                    indication = null,
                                ) { onValueChange("") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.x,
                                size = IconSizes.Default.sm,
                                tint = colors.mutedForeground
                            )
                        }
                    }
                }
                FieldFocusOverlay(inputColors, shapeModifier, focusedState, enabled, null)
            }
        }

        // Cancel is tinted text, as on iOS and in HeroUI Native: a filled button beside
        // the field would read as the primary action. A brand colour too light for
        // text fails the theme's TEXT-button contract everywhere, not only here.
        //
        // Visibility is decided by cancelVisible(), which is a function so the
        // rule can be tested.
        if (cancelVisible(cancel, isFocused)) {
            Button(
                text = I18n.strings.common.cancel,
                type = ButtonType.TEXT,
                size = ButtonSize.SMALL,
                disabled = !enabled,
                onClick = {
                    focusManager.clearFocus(force = true)
                    keyboardController?.hide()
                    onCancel?.invoke()
                },
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
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBar(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            enabled = enabled,
            cancel = SearchBarCancel.Never,
            onSearch = onAction,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(Spacing.sm))

        Button(
            text = actionText,
            disabled = !enabled,
            onClick = {
                focusManager.clearFocus(force = true)
                keyboardController?.hide()
                onAction(value)
            },
        )
    }
}
