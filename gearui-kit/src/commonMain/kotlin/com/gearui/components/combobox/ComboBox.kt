package com.gearui.components.combobox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.gearui.components.input.Input
import com.gearui.components.select.SelectOption
import com.gearui.components.select.SelectPanel
import com.gearui.components.select.selectPanelLayout
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.field.FieldVariant
import com.gearui.foundation.layout.Spacing
import com.gearui.i18n.I18n
import com.gearui.overlay.LocalOverlayViewportSize
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.rememberOverlay
import com.gearui.runtime.LocalRuntimeEnvironment
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * ComboBox — HeroUI v3 `ComboBox` for mobile: a text field that suggests options
 * as you type, for lists too long to pick from (a city, a currency, a contact).
 *
 * It is Select's panel over an editable field rather than a read-only trigger, so
 * suggestions look and behave exactly like Select's options. The caller owns the
 * query, so it can be prefilled, cleared or fetched remotely:
 *
 * ```kotlin
 * var query by remember { mutableStateOf("") }
 * ComboBox(query, { query = it }, cities, onSelect = { query = it.label; city = it.value })
 * ```
 *
 * The panel opens while the field has focus and the filter matches something; it
 * never covers the field with an empty list. [filter] defaults to a case-insensitive
 * "contains" over the label; pass your own for pinyin, initials or server-side search
 * (return true to keep everything when the list is already filtered). [autoFocus] opens
 * the field, and so the suggestions, as soon as the screen appears — what a search page
 * wants, and wrong anywhere the keyboard would cover the content the user came for.
 */
@Composable
fun <T> ComboBox(
    query: String,
    onQueryChange: (String) -> Unit,
    options: List<SelectOption<T>>,
    onSelect: (SelectOption<T>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = I18n.strings.field.selectPlaceholder,
    label: String? = null,
    error: String? = null,
    /** PRIMARY on the page background; SECONDARY on a card, sheet or header. */
    variant: FieldVariant = FieldVariant.PRIMARY,
    autoFocus: Boolean = false,
    filter: (SelectOption<T>, String) -> Boolean = { option, text ->
        text.isBlank() || option.label.contains(text, ignoreCase = true)
    },
) {
    val overlay = rememberOverlay()
    val density = LocalDensity.current
    val viewport = LocalOverlayViewportSize.current
    val environment = LocalRuntimeEnvironment.current
    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var focused by remember { mutableStateOf(false) }
    var overlayId by remember { mutableStateOf<Long?>(null) }

    val matches = options.filter { filter(it, query) }
    val matchesState = rememberUpdatedState(matches)
    val onSelectState = rememberUpdatedState(onSelect)
    val enabledState = rememberUpdatedState(enabled)

    fun close() {
        overlayId?.let { overlay.dismiss(it) }
        overlayId = null
    }

    fun open() {
        val bounds = anchorBounds ?: return
        if (!enabledState.value || viewport.height <= 0 || matchesState.value.isEmpty()) return
        val layout = with(density) {
            selectPanelLayout(
                matchesState.value.size, -1, FieldSizeTokens.Medium.height.value,
                bounds.top.toDp().value, bounds.bottom.toDp().value,
                viewport.height.toDp().value, environment.safeArea.top.value,
                maxOf(environment.safeArea.bottom.value, environment.keyboard.height.value),
                ControlGeometry.selectPanelOffset.value, false,
                contentPadding = ControlGeometry.selectContentPadding.value,
            )
        }
        if (layout.height <= 0f) return
        val panelTop = with(density) { layout.top.dp.toPx() }
        val anchorWidth = bounds.width
        overlayId = overlay.show(
            anchorBounds = Rect(bounds.left, panelTop, bounds.right, panelTop),
            options = OverlayOptions(
                placement = OverlayPlacement.BottomLeft,
                offsetY = Spacing.none,
                autoFlip = false,
                // The field keeps focus and the keyboard; only a tap outside closes it.
                dismissPolicy = OverlayDismissPolicy.Dropdown,
            ),
            onDismiss = { overlayId = null },
        ) {
            SelectPanel(
                options = matchesState.value,
                isSelected = { false },
                anchorWidth = anchorWidth,
                layout = layout,
                viewportWidth = viewport.width,
                enabled = enabledState.value,
                onOptionClick = { option ->
                    if (!option.disabled) {
                        close()
                        onSelectState.value(option)
                    }
                },
            )
        }
    }

    // Reopen whenever the suggestions change, so the panel tracks typing.
    LaunchedEffect(focused, query, matches.size, enabled, viewport) {
        close()
        if (focused && enabled) open()
    }

    DisposableEffect(Unit) { onDispose { overlayId?.let { overlay.dismiss(it) } } }

    Column(modifier = modifier.fillMaxWidth()) {
        Input(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { anchorBounds = it.boundsInRoot() },
            enabled = enabled,
            placeholder = placeholder,
            label = label,
            error = error,
            variant = variant,
            clearable = true,
            autoFocus = autoFocus,
            onFocusChanged = { focused = it },
        )
    }
}
