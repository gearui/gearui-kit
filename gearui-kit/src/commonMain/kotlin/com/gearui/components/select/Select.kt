package com.gearui.components.select

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.items
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.rememberOverlay
import com.gearui.theme.Theme
import com.gearui.i18n.formatArgs
import com.gearui.i18n.I18n
import com.gearui.foundation.elevation.Elevation
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.overlay.OverlayDefaults
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.field.fieldBorderColor
import com.gearui.foundation.field.FieldErrorText
import com.gearui.foundation.typography.IconSizes

/**
 * Select - fully Theme-driven dropdown select
 *
 * Built on the GearUI Overlay system:
 * - a real floating layer, leaving the page layout untouched
 * - no fullscreen scrim (tap outside to dismiss)
 * - automatic direction (opens upwards when there is no room below)
 * - scrollable options
 * - width follows the trigger
 * - supports the triggerOverlaid joined mode
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
    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var overlayId by remember { mutableStateOf<Long?>(null) }
    val selectedOption = options.find { it.value == value }
    val triggerShape = FieldDefaults.shape

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
        if (anchorBounds == null) return

        val bounds = anchorBounds!!
        val anchorWidth = bounds.width

        overlayId = overlay.show(
            anchorBounds = bounds,
            options = OverlayOptions(
                placement = OverlayPlacement.BottomLeft,
                offsetY = panelMode.offsetY(),
                autoFlip = true,
                dismissPolicy = OverlayDismissPolicy.Dropdown
            ),
            onDismiss = {
                // Clear the state whether the close was manual or from a tap outside
                clearDropdownState()
            }
        ) {
            // Read the current value straight from the State object
            SelectDropdownContent(
                options = options,
                selectedValue = valueState.value,
                anchorWidth = anchorWidth,
                panelMode = panelMode,
                onOptionClick = { option ->
                    onValueChangeState.value(option.value)
                    closeDropdown()
                }
            )
        }
        expanded = true
    }

    // Dismiss the Overlay when the component leaves composition
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
                style = Typography.BodyMedium,
                color = if (enabled) colors.foreground else colors.mutedForeground,
                modifier = Modifier.padding(bottom = Spacing.sm)
            )
        }

        // Trigger
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(FieldSizeTokens.Medium.height)
                .onGloballyPositioned { coordinates ->
                    if (!expanded) {
                        anchorBounds = coordinates.boundsInRoot()
                    }
                }
                .clip(triggerShape)
                .border(
                    width = FieldSizeTokens.Medium.borderWidth,
                    color = fieldBorderColor(error = error, enabled = enabled, active = expanded),
                    shape = triggerShape
                )
                .background(if (enabled) colors.surface else colors.muted)
                .clickable(enabled = enabled) {
                    if (expanded) {
                        closeDropdown()
                    } else {
                        openDropdown()
                    }
                }
                .padding(horizontal = FieldSizeTokens.Medium.paddingHorizontal),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedOption?.label ?: placeholder,
                style = Typography.BodyMedium,
                color = if (selectedOption != null) {
                    if (enabled) colors.foreground else colors.mutedForeground
                } else {
                    colors.mutedForeground
                }
            )

            Icon(
                name = if (expanded) Icons.keyboard_arrow_up else Icons.keyboard_arrow_down,
                size = FieldDefaults.trailingIconSize,
                tint = colors.mutedForeground
            )
        }

        FieldErrorText(error)
    }
}

/**
 * SelectDropdownContent - dropdown content (scrollable, via LazyColumn)
 */
@Composable
private fun <T> SelectDropdownContent(
    options: List<SelectOption<T>>,
    selectedValue: T?,
    anchorWidth: Float,
    panelMode: SelectPanelMode,
    onOptionClick: (SelectOption<T>) -> Unit
) {
    val colors = Theme.colors
    val density = LocalDensity.current

    val widthDp = with(density) { anchorWidth.toDp() }
    val panelShape = OverlayDefaults.panelShape
    val panelShadow =
        if (panelMode == SelectPanelMode.TRIGGER_OVERLAID) Elevation.raised else Elevation.floating
    val itemHeight = 44.dp
    val verticalPadding = Spacing.sm
    val rowSpacing = 4.dp
    val totalHeight = (options.size * itemHeight.value + verticalPadding.value * 2f + (options.size - 1).coerceAtLeast(0) * rowSpacing.value).dp
    val panelHeight = if (totalHeight > 240.dp) 240.dp else totalHeight

    Box(
        modifier = Modifier
            .width(widthDp)
            .height(panelHeight)
            .shadow(panelShadow, panelShape)
            .clip(panelShape)
            .background(colors.surface, panelShape)
            .border(BorderWidth.thin, colors.border, panelShape)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            items(options) { option ->
                SelectOptionItem(
                    option = option,
                    isSelected = option.value == selectedValue,
                    onClick = { onOptionClick(option) }
                )
            }
        }
    }
}

/**
 * SelectOptionItem - one option row
 */
@Composable
private fun <T> SelectOptionItem(
    option: SelectOption<T>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = Theme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.lg)
            .background(if (isSelected) colors.muted else colors.surface)
            .clickable(enabled = !option.disabled) { onClick() }
            .height(44.dp)
            .padding(horizontal = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = option.label,
            style = Typography.BodyMedium,
            color = when {
                option.disabled -> colors.mutedForeground
                else -> colors.foreground
            }
        )
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
    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var overlayId by remember { mutableStateOf<Long?>(null) }
    val triggerShape = FieldDefaults.shape

    // Wrapped in State so the lambdas can read the current value
    val valuesState = rememberUpdatedState(values)
    val onValuesChangeState = rememberUpdatedState(onValuesChange)

    fun clearDropdownState() {
        overlayId = null
        expanded = false
    }

    fun closeDropdown() {
        overlayId?.let { overlay.dismiss(it) }
    }

    fun openDropdown() {
        if (anchorBounds == null) return

        val bounds = anchorBounds!!
        val anchorWidth = bounds.width

        overlayId = overlay.show(
            anchorBounds = bounds,
            options = OverlayOptions(
                placement = OverlayPlacement.BottomLeft,
                offsetY = panelMode.offsetY(),
                autoFlip = true,
                dismissPolicy = OverlayDismissPolicy.Dropdown
            ),
            onDismiss = {
                clearDropdownState()
            }
        ) {
            // Read the current value straight from the State object
            MultiSelectDropdownContent(
                options = options,
                selectedValues = valuesState.value,
                anchorWidth = anchorWidth,
                panelMode = panelMode,
                onSelectionChange = { newValues ->
                    if (maxSelection == null || newValues.size <= maxSelection) {
                        onValuesChangeState.value(newValues)
                    }
                }
            )
        }
        expanded = true
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
                style = Typography.BodyMedium,
                color = if (enabled) colors.foreground else colors.mutedForeground,
                modifier = Modifier.padding(bottom = Spacing.sm)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(FieldSizeTokens.Medium.height)
                .onGloballyPositioned { coordinates ->
                    if (!expanded) {
                        anchorBounds = coordinates.boundsInRoot()
                    }
                }
                .clip(triggerShape)
                .border(
                    FieldSizeTokens.Medium.borderWidth,
                    fieldBorderColor(error = error, enabled = enabled, active = expanded),
                    triggerShape
                )
                .background(if (enabled) colors.surface else colors.muted)
                .clickable(enabled = enabled) {
                    if (expanded) closeDropdown() else openDropdown()
                }
                .padding(horizontal = FieldSizeTokens.Medium.paddingHorizontal),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (values.isEmpty()) placeholder
                    else I18n.strings.field.selectedCountFormat.formatArgs("count" to values.size),
                style = Typography.BodyMedium,
                color = if (values.isNotEmpty()) {
                    if (enabled) colors.foreground else colors.mutedForeground
                } else {
                    colors.mutedForeground
                }
            )

            Icon(
                name = if (expanded) Icons.keyboard_arrow_up else Icons.keyboard_arrow_down,
                size = FieldDefaults.trailingIconSize,
                tint = colors.mutedForeground
            )
        }

        FieldErrorText(error)
    }
}

/**
 * Select panel mode
 */
enum class SelectPanelMode {
    ITEM_ALIGNED,
    TRIGGER_OVERLAID
}

private fun SelectPanelMode.offsetY() = when (this) {
    SelectPanelMode.ITEM_ALIGNED -> 4.dp
    SelectPanelMode.TRIGGER_OVERLAID -> 0.dp
}

/**
 * MultiSelectDropdownContent - multi-select dropdown content
 */
@Composable
private fun <T> MultiSelectDropdownContent(
    options: List<SelectOption<T>>,
    selectedValues: Set<T>,
    anchorWidth: Float,
    panelMode: SelectPanelMode,
    onSelectionChange: (Set<T>) -> Unit
) {
    val colors = Theme.colors
    val density = LocalDensity.current

    val widthDp = with(density) { anchorWidth.toDp() }
    val panelShape = OverlayDefaults.panelShape
    val panelShadow =
        if (panelMode == SelectPanelMode.TRIGGER_OVERLAID) Elevation.raised else Elevation.floating
    val itemHeight = 44.dp
    val verticalPadding = Spacing.sm
    val rowSpacing = 4.dp
    val totalHeight = (options.size * itemHeight.value + verticalPadding.value * 2f + (options.size - 1).coerceAtLeast(0) * rowSpacing.value).dp
    val panelHeight = if (totalHeight > 240.dp) 240.dp else totalHeight

    Box(
        modifier = Modifier
            .width(widthDp)
            .height(panelHeight)
            .shadow(panelShadow, panelShape)
            .clip(panelShape)
            .background(colors.surface, panelShape)
            .border(BorderWidth.thin, colors.border, panelShape)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            items(options) { option ->
                val isSelected = option.value in selectedValues

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Theme.shapes.lg)
                        .background(if (isSelected) colors.muted else colors.surface)
                        .clickable(enabled = !option.disabled) {
                            val newValues = if (isSelected) {
                                selectedValues - option.value
                            } else {
                                selectedValues + option.value
                            }
                            onSelectionChange(newValues)
                        }
                        .height(44.dp)
                        .padding(horizontal = Spacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option.label,
                        style = Typography.BodyMedium,
                        color = when {
                            option.disabled -> colors.mutedForeground
                            isSelected -> colors.primary
                            else -> colors.foreground
                        }
                    )

                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(Theme.shapes.sm)
                            .border(BorderWidth.thin,
                                if (isSelected) colors.primary else colors.border,
                                Theme.shapes.sm
                            )
                            .background(if (isSelected) colors.primary else colors.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                name = Icons.check,
                                size = IconSizes.Default.xs,
                                tint = colors.primaryForeground
                            )
                        }
                    }
                }
            }
        }
    }
}
