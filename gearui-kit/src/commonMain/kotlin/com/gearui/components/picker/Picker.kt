package com.gearui.components.picker

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Brush
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import com.gearui.components.bottomsheet.BottomSheet
import kotlinx.coroutines.delay
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
        val colors = Theme.colors
        val shapes = Theme.shapes

        // Currently selected indices
        val currentIndexes = remember(data, selectedIndexes) {
            mutableStateListOf<Int>().apply {
                data.forEachIndexed { colIndex, _ ->
                    add(selectedIndexes.getOrElse(colIndex) { 0 })
                }
            }
        }

        BottomSheet.Host(
            visible = visible,
            onDismiss = onDismiss
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
            ) {
                // Header: cancel - title - confirm
                PickerHeader(
                    title = title,
                    onCancel = onCancel,
                    onConfirm = { onConfirm(currentIndexes.toList()) }
                )

                // Picker body
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                // Selection band background
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.lg)
                            .height(40.dp)
                            .clip(shapes.md)
                            .background(colors.muted)
                    )

                    // Wheel columns
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Spacing.xxl),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        data.forEachIndexed { colIndex, columnData ->
                            if (columnData.isNotEmpty()) {
                                val initialIndex = selectedIndexes.getOrElse(colIndex) { 0 }
                                    .coerceIn(0, columnData.size - 1)

                                WheelPickerColumn(
                                    items = columnData,
                                    initialIndex = initialIndex,
                                    onSelectedChange = { index ->
                                        if (colIndex < currentIndexes.size) {
                                            currentIndexes[colIndex] = index
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Top gradient mask
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        colors.surface,
                                        colors.surface.copy(alpha = 0f)
                                    )
                                )
                            )
                    )

                    // Bottom gradient mask
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        colors.surface.copy(alpha = 0f),
                                        colors.surface
                                    )
                                )
                            )
                    )
                }

                // Bottom safe area
                Spacer(modifier = Modifier.height(20.dp))
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
        val colors = Theme.colors
        val shapes = Theme.shapes

        // Parse the linked data
        val model = remember(data, initialData) {
            LinkedPickerModel(data, columnNum, initialData)
        }

        // Counter used to force a refresh
        var refreshKey by remember { mutableStateOf(0) }

        BottomSheet.Host(
            visible = visible,
            onDismiss = onDismiss
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
            ) {
                // Header
                PickerHeader(
                    title = title,
                    onCancel = onCancel,
                    onConfirm = { onConfirm(model.getSelectedData()) }
                )

                // Picker body
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                // Selection band background
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.lg)
                            .height(40.dp)
                            .clip(shapes.md)
                            .background(colors.muted)
                    )

                    // Linked wheel columns, keyed to force a refresh
                    key(refreshKey) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = Spacing.xxl),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (colIndex in 0 until columnNum) {
                                val columnData = model.getColumnData(colIndex)
                                val selectedIndex = model.getSelectedIndex(colIndex)

                                if (columnData.isNotEmpty()) {
                                    key(colIndex, columnData.hashCode()) {
                                        WheelPickerColumn(
                                            items = columnData,
                                            initialIndex = selectedIndex.coerceIn(0, columnData.size - 1),
                                            onSelectedChange = { index ->
                                                model.onColumnSelected(colIndex, index)
                                                // Refresh the columns after this one
                                                if (colIndex < columnNum - 1) {
                                                    refreshKey++
                                                }
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Top gradient mask
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        colors.surface,
                                        colors.surface.copy(alpha = 0f)
                                    )
                                )
                            )
                    )

                    // Bottom gradient mask
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        colors.surface.copy(alpha = 0f),
                                        colors.surface
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
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
            .height(56.dp)
            .padding(horizontal = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cancel button
        Text(
            text = I18n.strings.common.cancel,
            style = Typography.BodyLarge,
            color = colors.mutedForeground,
            modifier = Modifier.clickable { onCancel() }
        )

        // Title
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = Typography.TitleMedium,
                    color = colors.foreground
                )
            }
        }

        // Confirm button
        Text(
            text = I18n.strings.common.ok,
            style = Typography.BodyLarge,
            color = colors.primary,
            modifier = Modifier.clickable { onConfirm() }
        )
    }
}

/**
 * Wheel column, with snapping
 */
@Composable
private fun WheelPickerColumn(
    items: List<String>,
    initialIndex: Int,
    onSelectedChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors
    val itemHeight = 40.dp
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
            val targetIndex = if (firstVisibleOffset > 60) { // 超过一半高度则跳到下一个
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
                        style = if (distanceFromCenter == 0) Typography.TitleSmall else Typography.BodyMedium,
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
