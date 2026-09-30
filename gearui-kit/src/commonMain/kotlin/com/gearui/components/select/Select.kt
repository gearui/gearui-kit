package com.gearui.components.select

import com.gearui.foundation.keyboard.keyboardHeight
import com.gearui.foundation.field.FieldSurface
import com.gearui.foundation.field.FieldVariant
import com.gearui.foundation.field.shadowed
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
 * - group labels and selected-item indicators
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
    /** PRIMARY on the page background; SECONDARY on a card, sheet or header. */
    variant: FieldVariant = FieldVariant.PRIMARY,
) {
    val colors = Theme.colors
    val overlay = rememberOverlay()
    val density = LocalDensity.current
    val viewport = LocalOverlayViewportSize.current
    val environment = LocalRuntimeEnvironment.current
    // The host's or the platform's report (iOS: GearUI observes it itself).
    val keyboard = keyboardHeight()
    val optionsState = rememberUpdatedState(options)
    val enabledState = rememberUpdatedState(enabled)
    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var overlayId by remember { mutableStateOf<Long?>(null) }
    var placement by remember { mutableStateOf<DropdownPlacement?>(null) }
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

    fun placementFor(bounds: Rect): DropdownPlacement? {
        val rows = selectRows(optionsState.value)
        return dropdownPlacement(
            bounds, rows.size, rows.indexOfFirst { it.option?.value == valueState.value }, density, viewport.height,
            environment.safeArea.top, dropdownBottomInset(environment.safeArea.bottom, keyboard),
        )
    }

    // Opens the dropdown
    fun openDropdown() {
        if (anchorBounds == null || viewport.height <= 0 || !enabledState.value || expanded) return

        val bounds = anchorBounds!!
        val opened = placementFor(bounds) ?: return
        placement = opened
        overlayId = overlay.show(
            anchorBounds = opened.anchor,
            passThroughBounds = bounds,
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
                anchorWidth = (placement ?: opened).anchorWidth,
                layout = (placement ?: opened).layout,
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

    LaunchedEffect(enabled) {
        if (!enabled) closeDropdown()
    }
    TrackDropdownAnchor(overlay, overlayId, anchorBounds, ::placementFor, { placement = it }, ::closeDropdown)

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
        FieldSurface(Modifier.fillMaxWidth(), shadowed = variant.shadowed) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FieldSizeTokens.Medium.height)
                    .onGloballyPositioned { coordinates ->
                        anchorBounds = coordinates.boundsInRoot()
                    }
                    .then(fieldTriggerModifier(enabled, error, variant) {
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
    /** PRIMARY on the page background; SECONDARY on a card, sheet or header. */
    variant: FieldVariant = FieldVariant.PRIMARY,
    maxSelection: Int? = null,
) {
    val colors = Theme.colors
    val overlay = rememberOverlay()
    val density = LocalDensity.current
    val viewport = LocalOverlayViewportSize.current
    val environment = LocalRuntimeEnvironment.current
    // The host's or the platform's report (iOS: GearUI observes it itself).
    val keyboard = keyboardHeight()
    val optionsState = rememberUpdatedState(options)
    val enabledState = rememberUpdatedState(enabled)
    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var overlayId by remember { mutableStateOf<Long?>(null) }
    var placement by remember { mutableStateOf<DropdownPlacement?>(null) }

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

    fun placementFor(bounds: Rect): DropdownPlacement? {
        val rows = selectRows(optionsState.value)
        return dropdownPlacement(
            bounds, rows.size, rows.indexOfFirst { it.option?.value in valuesState.value }, density, viewport.height,
            environment.safeArea.top, dropdownBottomInset(environment.safeArea.bottom, keyboard),
        )
    }

    fun openDropdown() {
        if (anchorBounds == null || viewport.height <= 0 || !enabledState.value || expanded) return

        val bounds = anchorBounds!!
        val opened = placementFor(bounds) ?: return
        placement = opened
        overlayId = overlay.show(
            anchorBounds = opened.anchor,
            passThroughBounds = bounds,
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
                anchorWidth = (placement ?: opened).anchorWidth,
                layout = (placement ?: opened).layout,
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

    LaunchedEffect(enabled) {
        if (!enabled) closeDropdown()
    }
    TrackDropdownAnchor(overlay, overlayId, anchorBounds, ::placementFor, { placement = it }, ::closeDropdown)

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

        FieldSurface(Modifier.fillMaxWidth(), shadowed = variant.shadowed) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FieldSizeTokens.Medium.height)
                    .onGloballyPositioned { coordinates ->
                        anchorBounds = coordinates.boundsInRoot()
                    }
                    .then(fieldTriggerModifier(enabled, error, variant) {
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
