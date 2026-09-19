package com.gearui.components.select

import com.gearui.foundation.field.FieldSurface
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.rememberOverlay
import com.gearui.theme.Theme
import com.gearui.theme.LocalInputColors
import com.gearui.overlay.LocalOverlayViewportSize
import com.gearui.runtime.LocalRuntimeEnvironment
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.gearui.i18n.formatArgs
import com.gearui.i18n.I18n
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.field.fieldTriggerModifier
import com.gearui.foundation.field.FieldErrorText

/**
 * Select - fully Theme-driven dropdown select
 *
 * Built on the GearUI Overlay system:
 * - a real floating layer, leaving the page layout untouched
 * - no fullscreen scrim (tap outside to dismiss)
 * - automatic direction (opens upwards when there is no room below)
 * - scrollable options
 * - width follows the trigger
 * - item-aligned opening, group labels and selected-item indicators
 * - the legacy TRIGGER_OVERLAID mode opens separately above/below the trigger
 */
@Composable
fun <T> Select(
    value: T?,
    options: List<SelectOption<T>>,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = I18n.strings.field.selectPlaceholder,
    label: String? = null,
    error: String? = null,
    panelMode: SelectPanelMode = SelectPanelMode.TRIGGER_OVERLAID
) {
    val colors = Theme.colors
    val overlay = rememberOverlay()
    val density = LocalDensity.current
    val viewport = LocalOverlayViewportSize.current
    val environment = LocalRuntimeEnvironment.current
    val optionsState = rememberUpdatedState(options)
    val enabledState = rememberUpdatedState(enabled)
    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var overlayId by remember { mutableStateOf<Long?>(null) }
    val selectedOption = options.find { it.value == value }

    // Wrapped in State so the lambdas can read the current value
    val valueState = rememberUpdatedState(value)
    val onValueChangeState = rememberUpdatedState(onValueChange)

    // Closes the dropdown (state only; not dismiss, which would fire onDismiss)
    fun clearDropdownState() {
        overlayId = null
        expanded = false
    }

    // Closes the dropdown (calling dismiss explicitly)
    fun closeDropdown() {
        overlayId?.let { overlay.dismiss(it) }
        // Note: state clearing is handled by the onDismiss callback
    }

    // Opens the dropdown
    fun openDropdown() {
        if (anchorBounds == null || viewport.height <= 0 || !enabledState.value || expanded) return

        val bounds = anchorBounds!!
        val anchorWidth = bounds.width
        val rows = selectRows(optionsState.value)
        val selectedRow = rows.indexOfFirst { it.option?.value == valueState.value }
        val layout = with(density) {
            selectPanelLayout(
                rows.size, selectedRow, FieldSizeTokens.Medium.height.value,
                bounds.top.toDp().value, bounds.bottom.toDp().value,
                viewport.height.toDp().value, environment.safeArea.top.value,
                maxOf(environment.safeArea.bottom.value, environment.keyboard.height.value),
                ControlGeometry.selectPanelOffset.value, panelMode == SelectPanelMode.ITEM_ALIGNED,
                contentPadding = ControlGeometry.selectContentPadding.value,
            )
        }
        if (layout.height <= 0f) return

        // A resolved zero-height anchor avoids the generic dropdown rule that
        // forbids covering the trigger. Select owns the real trigger lifecycle.
        val panelTop = with(density) { layout.top.dp.toPx() }
        overlayId = overlay.show(
            anchorBounds = Rect(bounds.left, panelTop, bounds.right, panelTop),
            options = OverlayOptions(
                placement = OverlayPlacement.BottomLeft,
                offsetY = Spacing.none,
                autoFlip = false,
                dismissPolicy = OverlayDismissPolicy.Dropdown
            ),
            onDismiss = {
                // Clear the state whether the close was manual or from a tap outside
                clearDropdownState()
            }
        ) {
            // Read the current value straight from the State object
            SelectPanel(
                options = optionsState.value,
                isSelected = { it.value == valueState.value },
                anchorWidth = anchorWidth,
                layout = layout,
                viewportWidth = viewport.width,
                enabled = enabledState.value,
                onOptionClick = { option ->
                    if (expanded && enabledState.value && !option.disabled) {
                        closeDropdown()
                        onValueChangeState.value(option.value)
                    }
                }
            )
        }
        expanded = true
    }

    // Dismiss the Overlay when the component leaves composition
    LaunchedEffect(enabled, viewport) {
        if (!enabled || expanded) closeDropdown()
    }

    DisposableEffect(Unit) {
        onDispose {
            overlayId?.let { overlay.dismiss(it) }
        }
    }

    Column(modifier = modifier) {
        // Label
        if (label != null) {
            Text(
                text = label,
                style = Theme.typography.bodyMedium,
                color = if (enabled) colors.foreground else colors.mutedForeground,
                modifier = Modifier.padding(bottom = com.gearui.foundation.control.ControlGeometry.fieldLabelGap)
            )
        }

        // Trigger
        FieldSurface(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FieldSizeTokens.Medium.height)
                    .onGloballyPositioned { coordinates ->
                        if (!expanded) {
                            anchorBounds = coordinates.boundsInRoot()
                        }
                    }
                    .then(fieldTriggerModifier(enabled, error) {
                        if (expanded) {
                            closeDropdown()
                        } else {
                            openDropdown()
                        }
                    })
                    .padding(horizontal = FieldSizeTokens.Medium.paddingHorizontal),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedOption?.label ?: placeholder,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = Theme.typography.bodyMedium,
                    color = if (selectedOption != null) LocalInputColors.current.foreground else LocalInputColors.current.placeholder
                )

                SelectIndicator(expanded)
            }
        }

        FieldErrorText(error)
    }
}

/**
 * SelectOption - option data class
 */
data class SelectOption<T>(
    val value: T,
    val label: String,
    val disabled: Boolean = false,
    val group: String? = null
)

/**
 * MultiSelect - multi-select dropdown
 */
@Composable
fun <T> MultiSelect(
    values: Set<T>,
    options: List<SelectOption<T>>,
    onValuesChange: (Set<T>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = I18n.strings.field.selectPlaceholder,
    label: String? = null,
    error: String? = null,
    maxSelection: Int? = null,
    panelMode: SelectPanelMode = SelectPanelMode.TRIGGER_OVERLAID
) {
    val colors = Theme.colors
    val overlay = rememberOverlay()
    val density = LocalDensity.current
    val viewport = LocalOverlayViewportSize.current
    val environment = LocalRuntimeEnvironment.current
    val optionsState = rememberUpdatedState(options)
    val enabledState = rememberUpdatedState(enabled)
    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var overlayId by remember { mutableStateOf<Long?>(null) }

    // Wrapped in State so the lambdas can read the current value
    val valuesState = rememberUpdatedState(values)
    val onValuesChangeState = rememberUpdatedState(onValuesChange)
    val maxSelectionState = rememberUpdatedState(maxSelection)

    fun clearDropdownState() {
        overlayId = null
        expanded = false
    }

    fun closeDropdown() {
        overlayId?.let { overlay.dismiss(it) }
    }

    fun openDropdown() {
        if (anchorBounds == null || viewport.height <= 0 || !enabledState.value || expanded) return

        val bounds = anchorBounds!!
        val anchorWidth = bounds.width
        val rows = selectRows(optionsState.value)
        val selectedRow = rows.indexOfFirst { it.option?.value in valuesState.value }
        val layout = with(density) {
            selectPanelLayout(
                rows.size, selectedRow, FieldSizeTokens.Medium.height.value,
                bounds.top.toDp().value, bounds.bottom.toDp().value,
                viewport.height.toDp().value, environment.safeArea.top.value,
                maxOf(environment.safeArea.bottom.value, environment.keyboard.height.value),
                ControlGeometry.selectPanelOffset.value, panelMode == SelectPanelMode.ITEM_ALIGNED,
                contentPadding = ControlGeometry.selectContentPadding.value,
            )
        }
        if (layout.height <= 0f) return

        // A resolved zero-height anchor avoids the generic dropdown rule that
        // forbids covering the trigger. Select owns the real trigger lifecycle.
        val panelTop = with(density) { layout.top.dp.toPx() }
        overlayId = overlay.show(
            anchorBounds = Rect(bounds.left, panelTop, bounds.right, panelTop),
            options = OverlayOptions(
                placement = OverlayPlacement.BottomLeft,
                offsetY = Spacing.none,
                autoFlip = false,
                dismissPolicy = OverlayDismissPolicy.Dropdown
            ),
            onDismiss = {
                clearDropdownState()
            }
        ) {
            // Read the current value straight from the State object
            SelectPanel(
                options = optionsState.value,
                isSelected = { it.value in valuesState.value },
                anchorWidth = anchorWidth,
                layout = layout,
                viewportWidth = viewport.width,
                enabled = enabledState.value,
                multiple = true,
                onOptionClick = { option ->
                    val current = valuesState.value
                    val next = if (option.value in current) current - option.value else current + option.value
                    val limit = maxSelectionState.value
                    if (expanded && enabledState.value && !option.disabled && (limit == null || next.size <= limit || next.size < current.size)) {
                        onValuesChangeState.value(next)
                    }
                }
            )
        }
        expanded = true
    }

    LaunchedEffect(enabled, viewport) {
        if (!enabled || expanded) closeDropdown()
    }

    DisposableEffect(Unit) {
        onDispose {
            overlayId?.let { overlay.dismiss(it) }
        }
    }

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = Theme.typography.bodyMedium,
                color = if (enabled) colors.foreground else colors.mutedForeground,
                modifier = Modifier.padding(bottom = com.gearui.foundation.control.ControlGeometry.fieldLabelGap)
            )
        }

        FieldSurface(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FieldSizeTokens.Medium.height)
                    .onGloballyPositioned { coordinates ->
                        if (!expanded) {
                            anchorBounds = coordinates.boundsInRoot()
                        }
                    }
                    .then(fieldTriggerModifier(enabled, error) {
                        if (expanded) closeDropdown() else openDropdown()
                    })
                    .padding(horizontal = FieldSizeTokens.Medium.paddingHorizontal),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (values.isEmpty()) placeholder
                        else I18n.strings.field.selectedCountFormat.formatArgs("count" to values.size),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = Theme.typography.bodyMedium,
                    color = if (values.isNotEmpty()) LocalInputColors.current.foreground else LocalInputColors.current.placeholder
                )

                SelectIndicator(expanded)
            }
        }

        FieldErrorText(error)
    }
}

/**
 * Select panel mode
 */
enum class SelectPanelMode {
    /** Align the current row with the trigger where viewport bounds allow. */
    ITEM_ALIGNED,
    /** Legacy name: separate anchored panel, automatically choosing above/below. */
    TRIGGER_OVERLAID
}
