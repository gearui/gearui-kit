package com.gearui.components.actionsheet

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.LocalOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.OverlayDefaults
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.runtime.LocalRuntimeFlags
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing
import com.gearui.i18n.I18n
import com.gearui.foundation.border.BorderWidth
import com.gearui.runtime.rememberSafeAreaInset
import com.gearui.runtime.SafeAreaEdge

/**
 * ActionSheet - action panel
 *
 * A modal panel raised by a user action, offering two or more choices relevant to the current context.
 *
 * - list style (scrollable)
 * - grid style
 * - descriptions, icons and badges
 * - centred or left aligned
 * - item states (disabled, warning)
 * - press feedback
 */

/**
 * ActionSheet style
 */
enum class ActionSheetTheme {
    /** List */
    LIST,
    /** Grid */
    GRID
}

/**
 * ActionSheet alignment
 */
enum class ActionSheetAlign {
    /** Centred */
    CENTER,
    /** Left aligned */
    LEFT
}

/**
 * ActionSheet item
 */
data class ActionSheetItem(
    /** Item label */
    val label: String,
    /** Item description */
    val description: String? = null,
    /** Icon; may be an emoji */
    val icon: String? = null,
    /** Badge text */
    val badge: String? = null,
    /** Whether to show a red dot */
    val showRedPoint: Boolean = false,
    /** Whether the item is disabled */
    val disabled: Boolean = false,
    /** Custom label colour */
    val textColor: Color? = null
)

/**
 * ActionSheet state
 */
object ActionSheet {
    private var currentVisible = mutableStateOf(false)
    private var currentItems = mutableStateOf<List<ActionSheetItem>>(emptyList())
    private var currentTheme = mutableStateOf(ActionSheetTheme.LIST)
    private var currentAlign = mutableStateOf(ActionSheetAlign.CENTER)
    private var currentDescription = mutableStateOf<String?>(null)
    private var currentShowCancel = mutableStateOf(true)
    private var currentCancelText = mutableStateOf<String?>(null)
    private var currentOnSelected = mutableStateOf<((ActionSheetItem, Int) -> Unit)?>(null)
    private var currentOnCancel = mutableStateOf<(() -> Unit)?>(null)
    private var currentGridColumns = mutableStateOf(4)

    /**
     * Shows a list-style action panel
     */
    fun showList(
        items: List<ActionSheetItem>,
        description: String? = null,
        align: ActionSheetAlign = ActionSheetAlign.CENTER,
        showCancel: Boolean = true,
        cancelText: String? = null,
        onSelected: ((ActionSheetItem, Int) -> Unit)? = null,
        onCancel: (() -> Unit)? = null
    ) {
        currentItems.value = items
        currentTheme.value = ActionSheetTheme.LIST
        currentAlign.value = align
        currentDescription.value = description
        currentShowCancel.value = showCancel
        currentCancelText.value = cancelText
        currentOnSelected.value = onSelected
        currentOnCancel.value = onCancel
        currentVisible.value = true
    }

    /**
     * Shows a grid-style action panel
     */
    fun showGrid(
        items: List<ActionSheetItem>,
        description: String? = null,
        columns: Int = 4,
        showCancel: Boolean = true,
        cancelText: String? = null,
        onSelected: ((ActionSheetItem, Int) -> Unit)? = null,
        onCancel: (() -> Unit)? = null
    ) {
        currentItems.value = items
        currentTheme.value = ActionSheetTheme.GRID
        currentDescription.value = description
        currentGridColumns.value = columns
        currentShowCancel.value = showCancel
        currentCancelText.value = cancelText
        currentOnSelected.value = onSelected
        currentOnCancel.value = onCancel
        currentVisible.value = true
    }

    /**
     * Dismisses the action panel
     */
    fun dismiss() {
        currentVisible.value = false
    }

    /**
     * ActionSheet host.
     * Must be placed at the root of the page.
     */
    @Composable
    fun Host() {
        val visible by currentVisible
        val items by currentItems
        val theme by currentTheme
        val align by currentAlign
        val description by currentDescription
        val showCancel by currentShowCancel
        val cancelTextOverride by currentCancelText
        val cancelText = cancelTextOverride ?: I18n.strings.common.cancel
        val onSelected by currentOnSelected
        val onCancel by currentOnCancel
        val gridColumns by currentGridColumns

        if (visible) {
            ActionSheetContent(
                visible = true,
                items = items,
                theme = theme,
                align = align,
                description = description,
                showCancel = showCancel,
                cancelText = cancelText,
                gridColumns = gridColumns,
                onSelected = { item, index ->
                    onSelected?.invoke(item, index)
                    dismiss()
                },
                onCancel = {
                    onCancel?.invoke()
                    dismiss()
                },
                onDismiss = { dismiss() }
            )
        }
    }
}

/**
 * ActionSheet content
 */
@Composable
fun ActionSheetContent(
    visible: Boolean,
    items: List<ActionSheetItem>,
    theme: ActionSheetTheme = ActionSheetTheme.LIST,
    align: ActionSheetAlign = ActionSheetAlign.CENTER,
    description: String? = null,
    showCancel: Boolean = true,
    cancelText: String = I18n.strings.common.cancel,
    gridColumns: Int = 4,
    maxListHeight: Dp = 400.dp,
    onSelected: ((ActionSheetItem, Int) -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onDismiss: () -> Unit = {}
) {
    val colors = Theme.colors
    val controller = LocalOverlayController.current
    var overlayId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(visible) {
        if (visible) {
            overlayId = controller.show(
                anchorBounds = null,
                options = OverlayOptions(
                    placement = OverlayPlacement.Fullscreen,
                    modal = true,
                    maskColor = OverlayDefaults.scrimColor,
                    dismissPolicy = OverlayDismissPolicy.Sheet.copy(
                        outsideClick = true
                    )
                ),
                onDismiss = onDismiss
            ) {
                ActionSheetSurface(
                    items = items,
                    theme = theme,
                    align = align,
                    description = description,
                    showCancel = showCancel,
                    cancelText = cancelText,
                    gridColumns = gridColumns,
                    maxListHeight = maxListHeight,
                    onSelected = onSelected,
                    onCancel = onCancel,
                    onDismiss = onDismiss
                )
            }
        } else {
            overlayId?.let { controller.dismiss(it) }
            overlayId = null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            overlayId?.let { controller.dismiss(it) }
        }
    }
}

@Composable
private fun ActionSheetSurface(
    items: List<ActionSheetItem>,
    theme: ActionSheetTheme,
    align: ActionSheetAlign,
    description: String?,
    showCancel: Boolean,
    cancelText: String,
    gridColumns: Int,
    maxListHeight: Dp,
    onSelected: ((ActionSheetItem, Int) -> Unit)?,
    onCancel: (() -> Unit)?,
    onDismiss: () -> Unit
) {
    val colors = Theme.colors
    val runtimeFlags = LocalRuntimeFlags.current
    val bottomInset = rememberSafeAreaInset(
        edge = SafeAreaEdge.Bottom,
        consume = runtimeFlags.actionSheetConsumesBottomSafeArea,
        minimum = Spacing.lg,
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(OverlayDefaults.sheetShape)
                .background(colors.surface)
                .clickable { /* 阻止点击穿透 */ }
        ) {
            // Description
            if (description != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface)
                        .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                    contentAlignment = when (align) {
                        ActionSheetAlign.CENTER -> Alignment.Center
                        ActionSheetAlign.LEFT -> Alignment.CenterStart
                    }
                ) {
                    Text(
                        text = description,
                        style = Typography.BodyMedium,
                        color = colors.mutedForeground
                    )
                }
                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BorderWidth.hairline)
                        .background(colors.border)
                )
            }

            // Content area
            when (theme) {
                ActionSheetTheme.LIST -> {
                    ActionSheetList(
                        items = items,
                        align = align,
                        maxHeight = maxListHeight,
                        onSelected = onSelected
                    )
                }
                ActionSheetTheme.GRID -> {
                    ActionSheetGrid(
                        items = items,
                        columns = gridColumns,
                        onSelected = onSelected
                    )
                }
            }

            // Cancel button
            if (showCancel) {
                // Gap
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Spacing.sm)
                        .background(colors.muted)
                )

                // Cancel button, with press feedback
                var cancelPressed by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            if (cancelPressed) colors.muted else colors.surface
                        )
                        .clickable {
                            cancelPressed = true
                            onCancel?.invoke()
                            onDismiss()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cancelText,
                        style = Typography.BodyLarge,
                        color = colors.foreground
                    )
                }
            }

            // Bottom safe area
            Spacer(modifier = Modifier.height(bottomInset))
        }
    }
}

/**
 * List-style content, scrollable
 */
@Composable
private fun ActionSheetList(
    items: List<ActionSheetItem>,
    align: ActionSheetAlign,
    maxHeight: Dp,
    onSelected: ((ActionSheetItem, Int) -> Unit)?
) {
    val colors = Theme.colors

    // List height: 56dp per item, 78dp when it has a description, capped at maxHeight
    val itemHeightNormal = 56
    val itemHeightWithDesc = 78
    var totalHeightValue = 0
    items.forEach { item ->
        totalHeightValue += if (item.description != null) itemHeightWithDesc else itemHeightNormal
    }
    val totalHeight = totalHeightValue.dp
    val listHeight = if (totalHeight.value > maxHeight.value) maxHeight else totalHeight

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(listHeight)
            .background(colors.surface)
    ) {
        itemsIndexed(items) { index, item ->
            ActionSheetListItem(
                item = item,
                align = align,
                showDivider = index < items.size - 1,
                onClick = {
                    if (!item.disabled) {
                        onSelected?.invoke(item, index)
                    }
                }
            )
        }
    }
}

/**
 * List item, with press feedback
 */
@Composable
private fun ActionSheetListItem(
    item: ActionSheetItem,
    align: ActionSheetAlign,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    var isPressed by remember { mutableStateOf(false) }

    val textColor = when {
        item.disabled -> colors.mutedForeground
        item.textColor != null -> item.textColor
        else -> colors.foreground
    }

    val horizontalArrangement = when (align) {
        ActionSheetAlign.CENTER -> Arrangement.Center
        ActionSheetAlign.LEFT -> Arrangement.Start
    }

    val itemHeight = if (item.description != null) 78.dp else 56.dp

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(
                    if (isPressed && !item.disabled) colors.muted else colors.surface
                )
                .clickable(enabled = !item.disabled) {
                    isPressed = true
                    onClick()
                }
                .padding(horizontal = Spacing.lg),
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            if (item.icon != null) {
                Text(
                    text = item.icon,
                    style = Typography.TitleMedium,
                    color = if (item.disabled) colors.mutedForeground else colors.foreground
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
            }

            // Text content
            Column(
                horizontalAlignment = when (align) {
                    ActionSheetAlign.CENTER -> Alignment.CenterHorizontally
                    ActionSheetAlign.LEFT -> Alignment.Start
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.label,
                        style = Typography.BodyLarge,
                        color = textColor
                    )

                    // Badge
                    if (item.badge != null) {
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Box(
                            modifier = Modifier
                                .clip(shapes.lg)
                                .background(colors.destructive)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.badge,
                                style = Typography.BodyExtraSmall,
                                color = colors.destructiveForeground
                            )
                        }
                    }

                    // Red dot
                    if (item.showRedPoint && item.badge == null) {
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Box(
                            modifier = Modifier
                                .size(Spacing.sm)
                                .clip(CircleShape)
                                .background(colors.destructive)
                        )
                    }
                }

                // Description
                if (item.description != null) {
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = item.description,
                        style = Typography.BodyMedium,
                        color = colors.mutedForeground
                    )
                }
            }
        }

        // Divider
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .height(BorderWidth.hairline)
                    .background(colors.border)
            )
        }
    }
}

/**
 * Grid-style content, scrollable
 */
@Composable
private fun ActionSheetGrid(
    items: List<ActionSheetItem>,
    columns: Int,
    onSelected: ((ActionSheetItem, Int) -> Unit)?
) {
    val colors = Theme.colors
    val rows = (items.size + columns - 1) / columns

    // Rows are 96dp; at most two are shown before it scrolls
    val rowHeight = 96.dp
    val maxRows = 2
    val displayRows = minOf(rows, maxRows)
    val needScroll = rows > maxRows

    if (needScroll) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight * maxRows)
                .padding(vertical = Spacing.sm)
        ) {
            items(rows) { rowIndex ->
                ActionSheetGridRow(
                    items = items,
                    rowIndex = rowIndex,
                    columns = columns,
                    onSelected = onSelected
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.sm)
        ) {
            for (rowIndex in 0 until rows) {
                ActionSheetGridRow(
                    items = items,
                    rowIndex = rowIndex,
                    columns = columns,
                    onSelected = onSelected
                )
            }
        }
    }
}

/**
 * Grid row
 */
@Composable
private fun ActionSheetGridRow(
    items: List<ActionSheetItem>,
    rowIndex: Int,
    columns: Int,
    onSelected: ((ActionSheetItem, Int) -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (colIndex in 0 until columns) {
            val itemIndex = rowIndex * columns + colIndex
            if (itemIndex < items.size) {
                val item = items[itemIndex]
                ActionSheetGridItem(
                    item = item,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (!item.disabled) {
                            onSelected?.invoke(item, itemIndex)
                        }
                    }
                )
            } else {
                // Empty placeholder
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

/**
 * Grid item, with press feedback
 */
@Composable
private fun ActionSheetGridItem(
    item: ActionSheetItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    var isPressed by remember { mutableStateOf(false) }
    val textColor = if (item.disabled) colors.mutedForeground else colors.foreground

    val shapes = Theme.shapes

    Column(
        modifier = modifier
            .clip(shapes.lg)
            .background(
                if (isPressed && !item.disabled) colors.muted else Color.Transparent
            )
            .clickable(enabled = !item.disabled) {
                isPressed = true
                onClick()
            }
            .padding(Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon area
        Box {
            if (item.icon != null) {
                Box(
                    modifier = Modifier
                        .size(Spacing.huge)
                        .clip(shapes.lg)
                        .background(colors.muted),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.icon,
                        style = Typography.HeadlineSmall,
                        color = if (item.disabled) colors.mutedForeground else colors.foreground
                    )
                }
            }

            // Badge
            if (item.badge != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = Spacing.xs, y = -Spacing.xs)
                        .clip(shapes.lg)
                        .background(colors.destructive)
                        .padding(horizontal = Spacing.xs, vertical = 1.dp)
                ) {
                    Text(
                        text = item.badge,
                        style = Typography.BodyExtraSmall,
                        color = colors.primaryForeground
                    )
                }
            }

            // Red dot
            if (item.showRedPoint && item.badge == null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                        .size(Spacing.sm)
                        .clip(CircleShape)
                        .background(colors.destructive)
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Label
        Text(
            text = item.label,
            style = Typography.BodySmall,
            color = textColor
        )
    }
}
