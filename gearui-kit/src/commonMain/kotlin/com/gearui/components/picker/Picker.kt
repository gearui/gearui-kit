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

/**
 * Picker - 通用选择器
 *
 *
 * 用于一组预设数据中的选择，支持：
 * - 单列选择
 * - 多列独立选择
 * - 多列联动选择
 */
object Picker {

    /**
     * 显示单列选择器
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
     * 显示多列独立选择器
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

        // 当前选中索引
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
                // 头部：取消 - 标题 - 确定
                PickerHeader(
                    title = title,
                    onCancel = onCancel,
                    onConfirm = { onConfirm(currentIndexes.toList()) }
                )

                // 选择器内容
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    // 选中框背景
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(40.dp)
                            .clip(shapes.default)
                            .background(colors.surfaceVariant)
                    )

                    // 滚轮选择列
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
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

                    // 上方渐变遮罩
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

                    // 下方渐变遮罩
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

                // 底部安全区
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    /**
     * 显示多列联动选择器
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

        // 解析联动数据
        val model = remember(data, initialData) {
            LinkedPickerModel(data, columnNum, initialData)
        }

        // 强制刷新计数器
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
                // 头部
                PickerHeader(
                    title = title,
                    onCancel = onCancel,
                    onConfirm = { onConfirm(model.getSelectedData()) }
                )

                // 选择器内容
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    // 选中框背景
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(40.dp)
                            .clip(shapes.default)
                            .background(colors.surfaceVariant)
                    )

                    // 联动滚轮列 - 使用 key 强制刷新
                    key(refreshKey) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 32.dp),
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
                                                // 触发刷新后续列
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

                    // 上方渐变遮罩
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

                    // 下方渐变遮罩
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
 * Picker 头部
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
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 取消按钮
        Text(
            text = "取消",
            style = Typography.BodyLarge,
            color = colors.textSecondary,
            modifier = Modifier.clickable { onCancel() }
        )

        // 标题
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = Typography.TitleMedium,
                    color = colors.textPrimary
                )
            }
        }

        // 确定按钮
        Text(
            text = "确定",
            style = Typography.BodyLarge,
            color = colors.primary,
            modifier = Modifier.clickable { onConfirm() }
        )
    }
}

/**
 * 滚轮选择列 - 带吸附效果
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

    // 添加占位项使选中项可以居中
    val paddedItems = remember(items) {
        val padding = List(centerOffset) { "" }
        padding + items + padding
    }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex
    )
    val coroutineScope = rememberCoroutineScope()

    // 当前选中的实际索引
    var currentSelectedIndex by remember { mutableStateOf(initialIndex) }

    // 监听滚动状态，滚动停止时吸附到最近的项
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            // 滚动停止时，吸附到最近项
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val firstVisibleOffset = listState.firstVisibleItemScrollOffset

            // 计算应该吸附到哪个索引
            val targetIndex = if (firstVisibleOffset > 60) { // 超过一半高度则跳到下一个
                firstVisibleIndex + 1
            } else {
                firstVisibleIndex
            }.coerceIn(0, items.size - 1)

            // 吸附动画
            if (targetIndex != listState.firstVisibleItemIndex || firstVisibleOffset != 0) {
                listState.animateScrollToItem(targetIndex)
            }

            // 更新选中项
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
        contentPadding = PaddingValues(vertical = 0.dp)
    ) {
        itemsIndexed(paddedItems) { index, item ->
            val actualIndex = index - centerOffset
            val distanceFromCenter = abs(index - (listState.firstVisibleItemIndex + centerOffset))

            // 根据距离中心的距离计算透明度
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
                        color = colors.textPrimary.copy(alpha = alpha)
                    )
                }
            }
        }
    }
}

/**
 * 联动选择器数据模型
 */
private class LinkedPickerModel(
    private val data: Map<String, Any>,
    private val columnNum: Int,
    initialData: List<String>
) {
    private val selectedData = mutableStateListOf<String>()
    private val columnDataCache = mutableStateListOf<List<String>>()

    init {
        // 初始化各列数据
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

            // 更新后续列的数据
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
