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
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth

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
 * Transfer component
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
 *     TransferItem(key = "1", label = "Option 1"),
 *     TransferItem(key = "2", label = "Option 2")
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
    titles: Pair<String, String> =
        I18n.strings.field.transferSourceTitle to I18n.strings.field.transferTargetTitle,
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
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
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
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
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
            .border(BorderWidth.thin, colors.border, Theme.shapes.lg)
            .background(colors.surface)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.muted)
                .padding(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
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
                        color = colors.foreground
                    )
                }

                Text(
                    text = "${checkedKeys.size}/${items.size}",
                    style = Typography.BodySmall,
                    color = colors.mutedForeground
                )
            }

            if (searchable) {
                Spacer(modifier = Modifier.height(Spacing.sm))
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
                            text = if (searchValue.isNotEmpty()) I18n.strings.common.noSearchResult
                            else I18n.strings.common.noData,
                            style = Typography.BodyMedium,
                            color = colors.mutedForeground
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
            .padding(horizontal = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = !item.disabled
        )

        Text(
            text = item.label,
            style = Typography.BodyMedium,
            color = if (item.disabled) colors.mutedForeground else colors.foreground,
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
    titles: Pair<String, String> =
        I18n.strings.field.transferSourceTitle to I18n.strings.field.transferTargetTitle,
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
