package com.gearui.components.actionsheet

import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.motion.menuItemFeedback
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.draw.alpha
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonShape
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.foundation.control.ControlGeometry
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.interaction.collectIsPressedAsState
import com.gearui.foundation.sheet.ActionSheetDefaults
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
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.typography.IconSizes
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.LocalOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.OverlayDefaults
import com.gearui.foundation.primitives.Text
import com.gearui.runtime.LocalRuntimeFlags
import com.gearui.foundation.material.MaterialSurface
import com.gearui.foundation.material.Materials
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
    /**
     * Icon name — an `Icons.*` key, not a glyph.
     *
     * This used to render through `Text()`, which meant the field only worked
     * if you passed an emoji: anything else printed its own name. The type is
     * `String` because that is what `Icons.*` constants are, and the rendering
     * now goes through the [com.gearui.foundation.primitives.Icon] primitive
     * like every other family, so it follows the theme tint and draws the same
     * picture on every platform.
     */
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
        align: ActionSheetAlign = ActionSheetAlign.LEFT,
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
     * ActionSheet host. Mounted globally ONCE by [com.gearui.App]; pages must not mount another:
     * several hosts show the one singleton state as multiple stacked overlays.
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
    align: ActionSheetAlign = ActionSheetAlign.LEFT,
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

    val currentDismiss = rememberUpdatedState(onDismiss)
    val currentOverlayContent = rememberUpdatedState<@Composable () -> Unit>({
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
    })

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
                onDismiss = { currentDismiss.value() }
            ) {
                currentOverlayContent.value()
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
        // One sheet, as in the reference (HeroUI Native bottom sheet holding a menu):
        // radius 32, overlay surface, menu rows inset by 12, and Cancel as a neutral
        // button inside the same surface rather than a separate card.
        MaterialSurface(
            material = Materials.Sheet,
            modifier = Modifier.fillMaxWidth(),
            shape = OverlayDefaults.sheetShape,
            fallback = colors.popover,
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Consume surface taps. */ }
                .padding(top = ControlGeometry.menuPaddingBlock)
        ) {
            // Description: reference `.menu__label` — small, medium weight, muted.
            if (description != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = ControlGeometry.sheetMenuPaddingInline + ControlGeometry.menuItemPaddingInline,
                            end = ControlGeometry.sheetMenuPaddingInline + ControlGeometry.menuItemPaddingInline,
                            bottom = ControlGeometry.menuItemPaddingBlock,
                        ),
                    contentAlignment = when (align) {
                        ActionSheetAlign.CENTER -> Alignment.Center
                        ActionSheetAlign.LEFT -> Alignment.CenterStart
                    }
                ) {
                    Text(
                        text = description,
                        style = Theme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = colors.mutedForeground
                    )
                }
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

            if (showCancel) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = ControlGeometry.overlayPadding,
                            end = ControlGeometry.overlayPadding,
                            top = ControlGeometry.dialogActionGap,
                        )
                ) {
                    Button(
                        text = cancelText,
                        onClick = {
                            onCancel?.invoke()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        theme = ButtonTheme.DEFAULT,
                        type = ButtonType.FILL,
                        size = ButtonSize.MEDIUM,
                        shape = ButtonShape.ROUND,
                        block = true,
                    )
                }
            }

            // The sheet owns the bottom inset, so no scrim shows at the home indicator.
            Spacer(modifier = Modifier.height(ControlGeometry.menuPaddingBlock))
            Spacer(modifier = Modifier.height(bottomInset))
        }
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
    val itemHeightNormal = ControlGeometry.actionSheetRow.value
    val itemHeightWithDesc = ControlGeometry.actionSheetDescriptionRow.value
    var totalHeightValue = 0f
    items.forEach { item ->
        totalHeightValue += if (item.description != null) itemHeightWithDesc else itemHeightNormal
    }
    val totalHeight = totalHeightValue.dp
    val listHeight = if (totalHeight.value > maxHeight.value) maxHeight else totalHeight

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(listHeight)
            .padding(horizontal = ControlGeometry.sheetMenuPaddingInline)
    ) {
        itemsIndexed(items) { index, item ->
            ActionSheetListItem(
                item = item,
                align = align,
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
    onClick: () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    val sheetTokens = ActionSheetDefaults.Default
    val interaction = remember { MutableInteractionSource() }

    val textColor = when {
        item.disabled -> colors.mutedForeground
        item.textColor != null -> item.textColor
        else -> colors.foreground
    }

    val horizontalArrangement = when (align) {
        ActionSheetAlign.CENTER -> Arrangement.Center
        ActionSheetAlign.LEFT -> Arrangement.Start
    }

    val itemHeight = if (item.description != null) ControlGeometry.actionSheetDescriptionRow else ControlGeometry.actionSheetRow
    val danger = item.textColor == colors.destructive

    // Reference `.menu__item`: radius 16, padding 10, gap 10, animated press fill.
    // No separators between rows.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight)
            .menuItemFeedback(
                interaction = interaction,
                shape = RoundedCornerShape(ControlGeometry.radiusMenuItem),
                enabled = !item.disabled,
                danger = danger,
            )
            .clickable(enabled = !item.disabled, interactionSource = interaction, indication = null) {
                onClick()
            }
            .padding(horizontal = ControlGeometry.menuItemPaddingInline)
            .alpha(if (item.disabled) FeedbackDefaults.disabledOpacity else 1f),
        horizontalArrangement = when (align) {
            ActionSheetAlign.CENTER -> Arrangement.spacedBy(ControlGeometry.menuItemGap, Alignment.CenterHorizontally)
            ActionSheetAlign.LEFT -> Arrangement.spacedBy(ControlGeometry.menuItemGap)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (item.icon != null) {
            Icon(
                name = item.icon,
                size = IconSizes.Default.lg,
                tint = if (item.textColor != null && !item.disabled) textColor else colors.foreground,
            )
        }

        Column(
            horizontalAlignment = when (align) {
                ActionSheetAlign.CENTER -> Alignment.CenterHorizontally
                ActionSheetAlign.LEFT -> Alignment.Start
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.label,
                    style = Theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = if (item.disabled) colors.foreground else textColor
                )

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
                            style = Theme.typography.bodyExtraSmall,
                            color = colors.destructiveForeground
                        )
                    }
                }

                if (item.showRedPoint && item.badge == null) {
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Box(
                        modifier = Modifier
                            .size(sheetTokens.redDot)
                            .clip(CircleShape)
                            .background(colors.destructive)
                    )
                }
            }

            // Reference `.menu__item-description`: small, muted.
            if (item.description != null) {
                Text(
                    text = item.description,
                    style = Theme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
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
    val safeColumns = columns.coerceAtLeast(1)
    val rows = actionSheetRowCount(items.size, safeColumns)

    // Rows are 96dp; at most two are shown before it scrolls
    val rowHeight = ControlGeometry.actionSheetGridRow
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
                    columns = safeColumns,
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
                    columns = safeColumns,
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
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val textColor = if (item.disabled) colors.mutedForeground else colors.foreground

    val shapes = Theme.shapes
    val sheetTokens = ActionSheetDefaults.Default

    Column(
        modifier = modifier
            .clip(shapes.lg)
            .background(
                if (isPressed && !item.disabled) colors.muted else Color.Transparent
            )
            .clickable(enabled = !item.disabled, interactionSource = interaction, indication = null) {
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
                        .size(sheetTokens.gridIconTile)
                        .clip(shapes.lg)
                        .background(colors.muted),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        name = item.icon,
                        size = IconSizes.Display.sm,
                        tint = if (item.disabled) colors.mutedForeground else colors.foreground,
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
                        style = Theme.typography.bodyExtraSmall,
                        color = colors.primaryForeground
                    )
                }
            }

            // Red dot
            if (item.showRedPoint && item.badge == null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = sheetTokens.redDotOverhang, y = -sheetTokens.redDotOverhang)
                        .size(sheetTokens.redDot)
                        .clip(CircleShape)
                        .background(colors.destructive)
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Label
        Text(
            text = item.label,
            style = Theme.typography.bodySmall,
            color = textColor
        )
    }
}

internal fun actionSheetRowCount(count: Int, columns: Int): Int =
    if (count <= 0) 0 else 1 + (count - 1) / columns.coerceAtLeast(1)
