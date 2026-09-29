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
        val currentIndexes = remember(data, selectedIndexes) {
            mutableStateListOf<Int>().apply {
                data.forEachIndexed { colIndex, _ ->
                    add(selectedIndexes.getOrElse(colIndex) { 0 })
                }
            }
        }

        PickerSheet(
            visible = visible,
            title = title,
            onCancel = onCancel,
            onConfirm = { onConfirm(currentIndexes.toList()) },
            onDismiss = onDismiss,
        ) {
            PickerWheels(columnCount = data.size) { colIndex ->
                val columnData = data[colIndex]
                if (columnData.isNotEmpty()) {
                    WheelPickerColumn(
                        items = columnData,
                        initialIndex = selectedIndexes.getOrElse(colIndex) { 0 }.coerceIn(0, columnData.size - 1),
                        onSelectedChange = { index ->
                            if (colIndex < currentIndexes.size) currentIndexes[colIndex] = index
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }

    /**
     * Shows a picker with linked columns
     */
    @Composable
    fun Linked(
        visible: Boolean,
        title: String? = null,
        data: Map<String, Any>,
        columnNum: Int = 3,
        initialData: List<String> = emptyList(),
        onConfirm: (List<String>) -> Unit,
        onCancel: () -> Unit,
        onDismiss: () -> Unit
    ) {
        // Parse the linked data
        val model = remember(data, initialData) {
            LinkedPickerModel(data, columnNum, initialData)
        }

        // Counter used to force a refresh
        var refreshKey by remember { mutableStateOf(0) }

        PickerSheet(
            visible = visible,
            title = title,
            onCancel = onCancel,
            onConfirm = { onConfirm(model.getSelectedData()) },
            onDismiss = onDismiss,
        ) {
            // Keyed to rebuild the columns after a parent column changes.
            key(refreshKey) {
                PickerWheels(columnCount = columnNum) { colIndex ->
                    val columnData = model.getColumnData(colIndex)
                    if (columnData.isNotEmpty()) {
                        key(colIndex, columnData.hashCode()) {
                            WheelPickerColumn(
                                items = columnData,
                                initialIndex = model.getSelectedIndex(colIndex).coerceIn(0, columnData.size - 1),
                                onSelectedChange = { index ->
                                    model.onColumnSelected(colIndex, index)
                                    if (colIndex < columnNum - 1) refreshKey++
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
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
    content: @Composable () -> Unit,
) {
    BottomSheet.Host(visible = visible, onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().background(Theme.colors.surface)) {
            PickerHeader(title = title, onCancel = onCancel, onConfirm = onConfirm)
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
    onConfirm: () -> Unit
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
                .clickable(interactionSource = confirmInteraction, indication = null) { onConfirm() }
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
    val colors = Theme.colors
    val itemHeight = ControlGeometry.pickerItemHeight
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
    var currentSelectedIndex by remember { mutableStateOf(initialIndex) }

    // Watch the scroll state and snap to the nearest item when it stops
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            // Scrolling stopped: snap to the nearest item
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val firstVisibleOffset = listState.firstVisibleItemScrollOffset

            // Work out which index to snap to
            val targetIndex = if (firstVisibleOffset > 60) { // past half an item, snap to the next
                firstVisibleIndex + 1
            } else {
                firstVisibleIndex
            }.coerceIn(0, items.size - 1)

            // Snap animation
            if (targetIndex != listState.firstVisibleItemIndex || firstVisibleOffset != 0) {
                listState.animateScrollToItem(targetIndex)
            }

            // Update the selection
            if (targetIndex != currentSelectedIndex && targetIndex in items.indices) {
                currentSelectedIndex = targetIndex
                onSelectedChange(targetIndex)
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
                                onSelectedChange(actualIndex)
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

/**
 * Linked picker data model
 */
private class LinkedPickerModel(
    private val data: Map<String, Any>,
    private val columnNum: Int,
    initialData: List<String>
) {
    private val selectedData = mutableStateListOf<String>()
    private val columnDataCache = mutableStateListOf<List<String>>()

    init {
        // Initialise each column
        for (i in 0 until columnNum) {
            val columnData = getColumnDataInternal(i)
            columnDataCache.add(columnData)

            val initialValue = initialData.getOrNull(i)
            val selectedValue = if (initialValue != null && columnData.contains(initialValue)) {
                initialValue
            } else {
                columnData.firstOrNull() ?: ""
            }
            selectedData.add(selectedValue)
        }
    }

    fun getColumnData(colIndex: Int): List<String> {
        return columnDataCache.getOrElse(colIndex) { emptyList() }
    }

    fun getSelectedIndex(colIndex: Int): Int {
        val columnData = getColumnData(colIndex)
        val selectedValue = selectedData.getOrNull(colIndex) ?: ""
        return columnData.indexOf(selectedValue).coerceAtLeast(0)
    }

    fun getSelectedData(): List<String> {
        return selectedData.toList()
    }

    fun onColumnSelected(colIndex: Int, index: Int) {
        val columnData = getColumnData(colIndex)
        if (index in columnData.indices) {
            selectedData[colIndex] = columnData[index]

            // Update the data of the columns after this one
            for (i in (colIndex + 1) until columnNum) {
                val newColumnData = getColumnDataInternal(i)
                if (i < columnDataCache.size) {
                    columnDataCache[i] = newColumnData
                }
                val newSelectedValue = newColumnData.firstOrNull() ?: ""
                if (i < selectedData.size) {
                    selectedData[i] = newSelectedValue
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getColumnDataInternal(colIndex: Int): List<String> {
        var currentData: Any? = data

        for (i in 0 until colIndex) {
            val key = selectedData.getOrNull(i) ?: return emptyList()
            currentData = when (currentData) {
                is Map<*, *> -> currentData[key]
                else -> return emptyList()
            }
        }

        return when (currentData) {
            is Map<*, *> -> currentData.keys.mapNotNull { it?.toString() }
            is List<*> -> currentData.mapNotNull { it?.toString() }
            else -> emptyList()
        }
    }
}
