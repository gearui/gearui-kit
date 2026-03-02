package com.gearui.components.transfer

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.items
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape

import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.checkbox.Checkbox
import com.tencent.kuikly.compose.foundation.text.BasicTextField
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography

import com.gearui.theme.Theme

/**
 * Transfer item data
 */
data class TransferItem(
    val key: String,
    val label: String,
    val disabled: Boolean = false
)

/**
 * Transfer - Dual-list transfer component
 *
 * 穿梭框组件
 *
 * Features:
 * - Dual list selection
 * - Search support
 * - Batch operations
 * - Item count display
 * - Custom render
 *
 * Example:
 * ```
 * val items = listOf(
 *     TransferItem(key = "1", label = "选项 1"),
 *     TransferItem(key = "2", label = "选项 2")
 * )
 *
 * Transfer(
 *     items = items,
 *     selectedKeys = selectedKeys,
 *     onSelectedKeysChange = { selectedKeys = it }
 * )
 * ```
 */
@Composable
fun Transfer(
    items: List<TransferItem>,
    selectedKeys: Set<String>,
    onSelectedKeysChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    titles: Pair<String, String> = "源列表" to "目标列表",
    searchable: Boolean = true,
    height: Dp = 400.dp,
    itemHeight: Dp = 40.dp
) {
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes

    var leftChecked by remember { mutableStateOf<Set<String>>(emptySet()) }
    var rightChecked by remember { mutableStateOf<Set<String>>(emptySet()) }
    var leftSearch by remember { mutableStateOf("") }
    var rightSearch by remember { mutableStateOf("") }

    val leftItems = items.filter { it.key !in selectedKeys }
    val rightItems = items.filter { it.key in selectedKeys }

    val filteredLeftItems = leftItems.filter {
        it.label.contains(leftSearch, ignoreCase = true)
    }
    val filteredRightItems = rightItems.filter {
        it.label.contains(rightSearch, ignoreCase = true)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left list (source)
        TransferList(
            title = titles.first,
            items = filteredLeftItems,
            checkedKeys = leftChecked,
            onCheckedKeysChange = { leftChecked = it },
            searchValue = leftSearch,
            onSearchChange = { leftSearch = it },
            searchable = searchable,
            height = height,
            itemHeight = itemHeight,
            modifier = Modifier.weight(1f)
        )

        // Control buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                text = "›",
                onClick = {
                    onSelectedKeysChange(selectedKeys + leftChecked)
                    leftChecked = emptySet()
                },
                modifier = Modifier.width(48.dp)
            )

            Button(
                text = "‹",
                onClick = {
                    onSelectedKeysChange(selectedKeys - rightChecked)
                    rightChecked = emptySet()
                },
                modifier = Modifier.width(48.dp)
            )
        }

        // Right list (target)
        TransferList(
            title = titles.second,
            items = filteredRightItems,
            checkedKeys = rightChecked,
            onCheckedKeysChange = { rightChecked = it },
            searchValue = rightSearch,
            onSearchChange = { rightSearch = it },
            searchable = searchable,
            height = height,
            itemHeight = itemHeight,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TransferList(
    title: String,
    items: List<TransferItem>,
    checkedKeys: Set<String>,
    onCheckedKeysChange: (Set<String>) -> Unit,
    searchValue: String,
    onSearchChange: (String) -> Unit,
    searchable: Boolean,
    height: Dp,
    itemHeight: Dp,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes

    val allCheckable = items.filter { !it.disabled }
    val isAllChecked = allCheckable.isNotEmpty() &&
            allCheckable.all { it.key in checkedKeys }

    Column(
        modifier = modifier
            .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            .background(colors.surface)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surfaceVariant)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = isAllChecked,
                        onCheckedChange = {
                            if (isAllChecked) {
                                onCheckedKeysChange(emptySet())
                            } else {
                                onCheckedKeysChange(allCheckable.map { it.key }.toSet())
                            }
                        }
                    )
                    Text(
                        text = title,
                        style = Typography.TitleSmall,
                        color = colors.textPrimary
                    )
                }

                Text(
                    text = "${checkedKeys.size}/${items.size}",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }

            if (searchable) {
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = searchValue,
                    onValueChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Item list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            items(items) { item ->
                TransferListItem(
                    item = item,
                    checked = item.key in checkedKeys,
                    onCheckedChange = { checked ->
                        if (checked) {
                            onCheckedKeysChange(checkedKeys + item.key)
                        } else {
                            onCheckedKeysChange(checkedKeys - item.key)
                        }
                    },
                    height = itemHeight
                )
            }

            if (items.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight * 3),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchValue.isNotEmpty()) "无搜索结果" else "暂无数据",
                            style = Typography.BodyMedium,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransferListItem(
    item: TransferItem,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    height: Dp
) {
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clickable(enabled = !item.disabled) {
                onCheckedChange(!checked)
            }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = !item.disabled
        )

        Text(
            text = item.label,
            style = Typography.BodyMedium,
            color = if (item.disabled) colors.textDisabled else colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Transfer with tree structure
 */
@Composable
fun TransferWithGroups(
    groups: List<Pair<String, List<TransferItem>>>,
    selectedKeys: Set<String>,
    onSelectedKeysChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    titles: Pair<String, String> = "源列表" to "目标列表",
    height: Dp = 400.dp
) {
    val allItems = groups.flatMap { it.second }

    Transfer(
        items = allItems,
        selectedKeys = selectedKeys,
        onSelectedKeysChange = onSelectedKeysChange,
        titles = titles,
        height = height,
        modifier = modifier
    )
}
