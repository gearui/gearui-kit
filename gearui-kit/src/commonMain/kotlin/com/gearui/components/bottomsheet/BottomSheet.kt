package com.gearui.components.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.gearui.components.closebutton.CloseButton
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.list.CellDefaults
import com.gearui.foundation.motion.rowPressFeedback
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.foundation.sheet.SheetContainer
import com.gearui.foundation.sheet.SheetHeader
import com.gearui.foundation.sheet.SheetOverlay
import com.gearui.i18n.I18n
import com.gearui.primitives.Divider
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.heightIn
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.RectangleShape
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.unit.Dp

/**
 * BottomSheet - a list of options in an iOS sheet.
 *
 * Presented as iOS 26 presents a sheet: a rounded panel rising from the bottom over a
 * scrim, inset from the screen edges, with a grabber; the title centred in its title bar
 * with the [description] under it, and a close button at the end of the bar. The options
 * are an inset-grouped list — rows on a card, divided by hairlines — with a destructive
 * one in red.
 *
 * It closes on the close button, Back, a tap on the scrim (unless [closeOnClickOutside]
 * is false), choosing an option, or a drag down: anywhere on the sheet when the options
 * fit, on the grabber and title bar when they scroll.
 *
 * @param visible whether it is shown
 * @param onDismiss dismissal callback
 * @param title title
 * @param description a line under the title
 * @param items option list
 * @param showCancel whether the title bar has a close button
 * @param cancelText the close button's name for a screen reader
 * @param closeOnClickOutside whether tapping outside dismisses
 * @param maxListHeight height past which the options scroll; 400dp by default
 * @param onItemClick option click callback
 */
@Composable
fun BottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String? = null,
    description: String? = null,
    items: List<BottomSheetItem>,
    showCancel: Boolean = true,
    cancelText: String = I18n.strings.common.cancel,
    closeOnClickOutside: Boolean = true,
    maxListHeight: Dp = ControlGeometry.sheetMaxListHeight,
    onItemClick: (BottomSheetItem, Int) -> Unit
) {
    SheetOverlay(visible, onDismiss, closeOnClickOutside) {
        BottomSheetSurface(
            title = title,
            description = description,
            items = items,
            showCancel = showCancel,
            cancelText = cancelText,
            maxListHeight = maxListHeight,
            onDismiss = onDismiss,
            onItemClick = onItemClick,
        )
    }
}

/**
 * BottomSheet with arbitrary content instead of an option list.
 *
 * The same sheet, title bar and close button as the item-list overload; the caller owns
 * what goes in the body. Use this when the body is not a list of tappable labels — a
 * paginated roster, a form, a chart.
 *
 * The body is responsible for its own height and scrolling: the sheet does not clamp
 * it, because a LazyColumn inside a height-clamped parent stops scrolling. Since the body
 * may scroll, the sheet is dragged away by its grabber and title bar.
 *
 * @param visible whether it is shown
 * @param onDismiss dismissal callback
 * @param title title
 * @param description a line under the title
 * @param showCancel whether the title bar has a close button
 * @param cancelText the close button's name for a screen reader
 * @param closeOnClickOutside whether tapping outside dismisses
 * @param header custom chrome drawn in place of [title]/[description]; it sits in the
 *   sheet's drag region, so dragging down on it dismisses the sheet just like the grabber.
 *   With [showCancel] the close button sits over its end.
 * @param content sheet body
 */
@Composable
fun BottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String? = null,
    description: String? = null,
    showCancel: Boolean = true,
    cancelText: String = I18n.strings.common.cancel,
    closeOnClickOutside: Boolean = true,
    header: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    // Read through State so an open sheet receives async titles and counts along with
    // its body, rather than keeping a read-receipt subtitle at its initial count.
    val currentTitle by rememberUpdatedState(title)
    val currentDescription by rememberUpdatedState(description)
    val currentHeader by rememberUpdatedState(header)
    val currentBody by rememberUpdatedState(content)
    val currentShowCancel by rememberUpdatedState(showCancel)
    val currentCancelText by rememberUpdatedState(cancelText)
    SheetOverlay(visible, onDismiss, closeOnClickOutside) {
        BottomSheetSurface(
            title = currentTitle,
            description = currentDescription,
            showCancel = currentShowCancel,
            cancelText = currentCancelText,
            onDismiss = onDismiss,
            header = currentHeader,
            body = currentBody,
        )
    }
}

/** The option-list sheet's surface. Shared with [BottomSheetSurface]'s free-content form. */
@Composable
internal fun BottomSheetSurface(
    title: String? = null,
    description: String? = null,
    items: List<BottomSheetItem>,
    showCancel: Boolean = true,
    cancelText: String = I18n.strings.common.cancel,
    maxListHeight: Dp = ControlGeometry.sheetMaxListHeight,
    onDismiss: () -> Unit,
    onItemClick: (BottomSheetItem, Int) -> Unit
) {
    val scrolls = ControlGeometry.listMinHeight * items.size > maxListHeight
    SheetContainer(
        onDismiss = onDismiss,
        // An inset-grouped list sits on the grouped background.
        background = Theme.colors.background,
        // Options that fit do not scroll, so the whole sheet can take the drag.
        dragWholeSheet = !scrolls,
        header = { SheetTitleBar(title, description, showCancel, cancelText, onDismiss) },
    ) {
        BottomSheetItemList(items, scrolls, maxListHeight, onDismiss, onItemClick)
    }
}

/**
 * The sheet around free content: grabber, title bar ([header] in its place when given),
 * close button, the [body] on the surface colour.
 */
@Composable
internal fun BottomSheetSurface(
    title: String? = null,
    description: String? = null,
    showCancel: Boolean = true,
    cancelText: String = I18n.strings.common.cancel,
    onDismiss: () -> Unit,
    header: (@Composable () -> Unit)? = null,
    body: @Composable () -> Unit,
) {
    SheetContainer(
        onDismiss = onDismiss,
        background = Theme.colors.surface,
        dragWholeSheet = false,
        header = {
            if (header != null) {
                Box(Modifier.fillMaxWidth()) {
                    header()
                    if (showCancel) {
                        SheetClose(cancelText, onDismiss, Modifier.align(Alignment.TopEnd).padding(end = Spacing.sm))
                    }
                }
            } else {
                SheetTitleBar(title, description, showCancel, cancelText, onDismiss)
            }
        },
        body = body,
    )
}

@Composable
private fun SheetTitleBar(title: String?, description: String?, showCancel: Boolean, cancelText: String, onDismiss: () -> Unit) {
    SheetHeader(
        title = title,
        description = description,
        trailing = if (showCancel) ({ SheetClose(cancelText, onDismiss) }) else null,
    )
}

/** iOS 26's sheet close button: a round × at the end of the title bar. */
@Composable
private fun SheetClose(label: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.heightIn(min = ControlGeometry.selectionTouchTarget), contentAlignment = Alignment.Center) {
        CloseButton(onClick = onDismiss, contentDescription = label)
    }
}

/** The options as an inset-grouped list; scrolling inside the card when they do not fit. */
@Composable
private fun BottomSheetItemList(
    items: List<BottomSheetItem>,
    scrolls: Boolean,
    maxHeight: Dp,
    onDismiss: () -> Unit,
    onItemClick: (BottomSheetItem, Int) -> Unit
) {
    if (items.isEmpty()) return
    val withIcons = items.any { it.icon != null }
    val row: @Composable (Int, BottomSheetItem) -> Unit = { index, item ->
        if (index > 0) Divider(insetStart = separatorInset(withIcons))
        BottomSheetItemRow(item) {
            if (!item.disabled) {
                onItemClick(item, index)
                onDismiss()
            }
        }
    }
    val card = Modifier
        .fillMaxWidth()
        .padding(start = Spacing.lg, end = Spacing.lg, bottom = Spacing.lg)
        .clip(Theme.shapes.lg)
        .background(Theme.colors.surface)
    if (scrolls) {
        LazyColumn(card.height(maxHeight)) { itemsIndexed(items) { index, item -> row(index, item) } }
    } else {
        Column(card) { items.forEachIndexed { index, item -> row(index, item) } }
    }
}

/** A row's separator starts where its label does, past the icon when rows have one. */
private fun separatorInset(withIcons: Boolean): Dp {
    val padding = CellDefaults.Default.paddingHorizontal
    return if (withIcons) padding + ICON_SLOT + Spacing.md else padding
}

private val ICON_SLOT: Dp = IconSizes.Default.xl

/** One option: an icon, the label (red when destructive, muted when disabled), the row's full width tappable. */
@Composable
private fun BottomSheetItemRow(item: BottomSheetItem, onClick: () -> Unit) {
    val colors = Theme.colors
    val interaction = remember { MutableInteractionSource() }
    val color = when {
        item.disabled -> colors.mutedForeground
        item.danger -> colors.destructive
        else -> colors.foreground
    }
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = ControlGeometry.listMinHeight)
            .rowPressFeedback(interaction, RectangleShape, enabled = !item.disabled, danger = item.danger, scale = false)
            .clickable(enabled = !item.disabled, interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
            .padding(horizontal = CellDefaults.Default.paddingHorizontal, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item.icon?.invoke()
        Text(item.label, style = Theme.typography.bodyMedium, color = color)
    }
}

/**
 * BottomSheetItem - bottom sheet option data class
 */
data class BottomSheetItem(
    /** option label */
    val label: String,

    /** optional option icon */
    val icon: (@Composable () -> Unit)? = null,

    /** whether it is a destructive action (red text) */
    val danger: Boolean = false,

    /** whether it is disabled */
    val disabled: Boolean = false,

    /** attached payload */
    val data: Any? = null
)

/**
 * BottomSheet.Host - the sheet around content that draws everything itself.
 *
 * The same iOS sheet — rising, floating, with the grabber, dragged away by the grabber —
 * on the surface colour, with no title bar: the content draws its own.
 */
object BottomSheet {

    @Composable
    fun Host(
        visible: Boolean,
        onDismiss: () -> Unit,
        closeOnClickOutside: Boolean = true,
        content: @Composable () -> Unit
    ) {
        val currentContent by rememberUpdatedState(content)
        SheetOverlay(visible, onDismiss, closeOnClickOutside) {
            SheetContainer(onDismiss = onDismiss, background = Theme.colors.surface, dragWholeSheet = false) {
                currentContent()
            }
        }
    }
}

/**
 * BottomSheetState - convenience state holder
 *
 * Usage:
 * ```
 * val sheetState = remember { BottomSheetState() }
 *
 * BottomSheet(
 *     visible = sheetState.visible,
 *     onDismiss = { sheetState.hide() },
 *     items = listOf(
 *         BottomSheetItem("Share"),
 *         BottomSheetItem("Delete", danger = true)
 *     ),
 *     onItemClick = { item, index ->
 *         // handle the tap
 *     }
 * )
 * ```
 */
class BottomSheetState {
    var visible by mutableStateOf(false)
        private set

    fun show() {
        visible = true
    }

    fun hide() {
        visible = false
    }

    fun toggle() {
        visible = !visible
    }
}
