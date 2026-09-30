package com.gearui.components.picker

import com.gearui.foundation.control.ControlGeometry
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import androidx.compose.runtime.remember
import com.gearui.foundation.interaction.pressScale
import com.tencent.kuikly.compose.foundation.interaction.collectIsPressedAsState
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.gearui.foundation.interaction.disabledAppearance
import com.tencent.kuikly.compose.ui.draw.clip
import com.gearui.foundation.material.MaterialDefaults
import com.gearui.foundation.material.tintedMask
import com.gearui.foundation.material.verticalBrush
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme
import com.gearui.components.bottomsheet.BottomSheet
import kotlinx.coroutines.launch
import kotlin.math.abs
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing

/**
 * Picker - general-purpose picker
 *
 *
 * Selects from a preset set of values. Supports:
 * - single column
 * - multiple independent columns
 * - multiple linked columns
 */
object Picker {

    /** Single column whose value survives translations and duplicate labels. */
    @Composable
    fun Single(visible: Boolean, options: List<PickerOption>, selectedValue: String? = null,
        title: String? = null, onConfirm: (PickerOption) -> Unit, onCancel: () -> Unit, onDismiss: () -> Unit) {
        StablePicker(visible, title, listOf(options), listOfNotNull(selectedValue), { onConfirm(it.first()) }, onCancel, onDismiss)
    }

    /** Independent stable-value columns. The legacy index-based Multi remains available. */
    @Composable
    fun MultiValues(visible: Boolean, options: List<List<PickerOption>>, selectedValues: List<String> = emptyList(),
        title: String? = null, onConfirm: (List<PickerOption>) -> Unit, onCancel: () -> Unit, onDismiss: () -> Unit) {
        StablePicker(visible, title, options, selectedValues, onConfirm, onCancel, onDismiss)
    }

    /**
     * Linked columns over a tree: province, city, district. Nodes are told apart by
     * [PickerOption.value], so two districts with the same name stay distinct; a parent
     * change resets only the columns below it.
     *
     * A branch shallower than [columnNum] — a region with no districts — is complete at
     * its leaf: Confirm is enabled and [onConfirm] gets the shorter path.
     */
    @Composable
    fun Linked(visible: Boolean, options: List<PickerOption>, selectedValues: List<String> = emptyList(),
        columnNum: Int = 3, title: String? = null, onConfirm: (List<PickerOption>) -> Unit,
        onCancel: () -> Unit, onDismiss: () -> Unit) {
        var requested by remember(visible, options, selectedValues) { mutableStateOf(selectedValues) }
        val path = pickerPath(options, requested, columnNum)
        val complete = pickerPathComplete(path, columnNum)
        PickerSheet(visible, title, onCancel, { if (complete) onConfirm(path) }, onDismiss,
            confirmEnabled = complete) {
            PickerWheels(columnNum) { depth ->
                val column = (if (depth == 0) options else path.getOrNull(depth - 1)?.children.orEmpty()).distinctByValue()
                if (column.isNotEmpty()) key(depth, column) {
                    WheelPickerColumn(column.map { it.label }, column.indexOf(path.getOrNull(depth)).coerceAtLeast(0),
                        { index -> requested = path.take(depth).map { it.value } + column[index].value }, Modifier.weight(1f))
                }
            }
        }
    }

    /**
     * Shows a single-column picker
     */
    @Composable
    fun Single(
        visible: Boolean,
        title: String? = null,
        data: List<String>,
        selectedIndex: Int = 0,
        onConfirm: (Int, String) -> Unit,
        onCancel: () -> Unit,
        onDismiss: () -> Unit
    ) {
        Multi(
            visible = visible,
            title = title,
            data = listOf(data),
            selectedIndexes = listOf(selectedIndex),
            onConfirm = { indexes ->
                val index = indexes.firstOrNull() ?: 0
                val value = data.getOrElse(index) { "" }
                onConfirm(index, value)
            },
            onCancel = onCancel,
            onDismiss = onDismiss
        )
    }

    /**
     * Shows a picker with independent columns
     */
    @Composable
    fun Multi(
        visible: Boolean,
        title: String? = null,
        data: List<List<String>>,
        selectedIndexes: List<Int> = emptyList(),
        onConfirm: (List<Int>) -> Unit,
        onCancel: () -> Unit,
        onDismiss: () -> Unit
    ) {
        // Currently selected indices
        val currentIndexes = remember(visible, data, selectedIndexes) {
            mutableStateListOf<Int>().apply {
                data.forEachIndexed { colIndex, items ->
                    add(if (items.isEmpty()) 0 else selectedIndexes.getOrElse(colIndex) { 0 }.coerceIn(0, items.lastIndex))
                }
            }
        }

        PickerSheet(
            visible = visible,
            title = title,
            onCancel = onCancel,
            onConfirm = { onConfirm(currentIndexes.toList()) },
            onDismiss = onDismiss,
            confirmEnabled = data.isNotEmpty() && data.all { it.isNotEmpty() },
        ) {
            PickerWheels(columnCount = data.size) { colIndex ->
                val columnData = data[colIndex]
                if (columnData.isNotEmpty()) {
                    WheelPickerColumn(
                        items = columnData,
                        initialIndex = currentIndexes[colIndex],
                        onSelectedChange = { index ->
                            if (colIndex < currentIndexes.size) currentIndexes[colIndex] = index
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

/**
 * The sheet every wheel picker opens in — Picker, DatePicker, TimePicker: a header with
 * Cancel, the title and OK, above the wheels.
 */
@Composable
internal fun PickerSheet(
    visible: Boolean,
    title: String?,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    BottomSheet.Host(visible = visible, onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().background(Theme.colors.surface)) {
            PickerHeader(title = title, onCancel = onCancel, onConfirm = onConfirm, confirmEnabled = confirmEnabled)
            content()
        }
    }
}

/**
 * The wheel area: one selection band behind all the columns, as on iOS, and the fades
 * at top and bottom. Each column draws only its items, never a band of its own.
 */
@Composable
internal fun PickerWheels(
    columnCount: Int,
    column: @Composable RowScope.(index: Int) -> Unit,
) {
    val colors = Theme.colors
    Box(modifier = Modifier.fillMaxWidth().height(ControlGeometry.pickerWheelHeight)) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .height(ControlGeometry.pickerItemHeight)
                .clip(Theme.shapes.md)
                .background(colors.muted)
        )
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.xxl),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            for (index in 0 until columnCount) column(index)
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(ControlGeometry.pickerFadeHeight)
                .background(brush = MaterialDefaults.pickerTopMask.tintedMask(colors.surface).verticalBrush())
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(ControlGeometry.pickerFadeHeight)
                .background(brush = MaterialDefaults.pickerBottomMask.tintedMask(colors.surface).verticalBrush())
        )
    }
}

/**
 * Picker header
 */
@Composable
private fun PickerHeader(
    title: String?,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    confirmEnabled: Boolean = true,
) {
    val colors = Theme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ControlGeometry.controlLarge)
            .padding(horizontal = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cancel button
        val cancelInteraction = remember { MutableInteractionSource() }
        val cancelPressed by cancelInteraction.collectIsPressedAsState()
        Text(
            text = I18n.strings.common.cancel,
            style = Theme.typography.bodyLarge,
            color = colors.mutedForeground,
            modifier = Modifier
                .pressScale(cancelPressed)
                .clickable(interactionSource = cancelInteraction, indication = null) { onCancel() }
        )

        // Title
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = Theme.typography.titleMedium,
                    color = colors.foreground
                )
            }
        }

        // Confirm button
        val confirmInteraction = remember { MutableInteractionSource() }
        val confirmPressed by confirmInteraction.collectIsPressedAsState()
        Text(
            text = I18n.strings.common.ok,
            style = Theme.typography.bodyLarge,
            color = colors.primary,
            modifier = Modifier
                .pressScale(confirmPressed)
                .disabledAppearance(!confirmEnabled)
                .clickable(enabled = confirmEnabled, interactionSource = confirmInteraction, indication = null) { onConfirm() }
        )
    }
}

/**
 * Wheel column, with snapping
 */
@Composable
internal fun WheelPickerColumn(
    items: List<String>,
    initialIndex: Int,
    onSelectedChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return
    val colors = Theme.colors
    val itemHeight = ControlGeometry.pickerItemHeight
    val itemPixels = with(LocalDensity.current) { itemHeight.toPx() }
    val onSelected by rememberUpdatedState(onSelectedChange)
    val visibleItems = 5
    val centerOffset = visibleItems / 2

    // Padding items so the selected row can sit in the centre
    val paddedItems = remember(items) {
        val padding = List(centerOffset) { "" }
        padding + items + padding
    }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex
    )
    val coroutineScope = rememberCoroutineScope()

    // Actual selected index
    var currentSelectedIndex by remember(items) { mutableStateOf(initialIndex.coerceIn(items.indices)) }
    LaunchedEffect(initialIndex, items) {
        val index = initialIndex.coerceIn(items.indices)
        if (index != currentSelectedIndex) {
            currentSelectedIndex = index
            listState.scrollToItem(index)
        }
    }

    // Watch the scroll state and snap to the nearest item when it stops
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            // Scrolling stopped: snap to the nearest item
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val firstVisibleOffset = listState.firstVisibleItemScrollOffset

            // Work out which index to snap to
            val targetIndex = wheelSnapIndex(firstVisibleIndex, firstVisibleOffset, itemPixels, items.size)

            // Snap animation
            if (targetIndex != listState.firstVisibleItemIndex || firstVisibleOffset != 0) {
                listState.animateScrollToItem(targetIndex)
            }

            // Update the selection
            if (targetIndex != currentSelectedIndex && targetIndex in items.indices) {
                currentSelectedIndex = targetIndex
                onSelected(targetIndex)
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = Spacing.none)
    ) {
        itemsIndexed(paddedItems) { index, item ->
            val actualIndex = index - centerOffset
            val distanceFromCenter = abs(index - (listState.firstVisibleItemIndex + centerOffset))

    // Opacity derived from distance to the centre
            val alpha = when (distanceFromCenter) {
                0 -> 1f
                1 -> 0.6f
                2 -> 0.3f
                else -> 0.15f
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .clickable(enabled = item.isNotEmpty()) {
                        if (actualIndex in items.indices) {
                            coroutineScope.launch {
                                listState.animateScrollToItem(actualIndex)
                                currentSelectedIndex = actualIndex
                                onSelected(actualIndex)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (item.isNotEmpty()) {
                    Text(
                        text = item,
                        style = if (distanceFromCenter == 0) Theme.typography.titleSmall else Theme.typography.bodyMedium,
                        color = colors.foreground.copy(alpha = alpha)
                    )
                }
            }
        }
    }
}

internal fun wheelSnapIndex(index: Int, offset: Int, itemPixels: Float, count: Int): Int {
    require(count > 0 && itemPixels > 0)
    return (index + if (offset > itemPixels / 2f) 1 else 0).coerceIn(0, count - 1)
}
