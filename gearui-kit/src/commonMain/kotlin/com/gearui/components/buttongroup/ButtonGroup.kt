package com.gearui.components.buttongroup

import androidx.compose.runtime.Composable
import com.gearui.components.actionsheet.ActionSheet
import com.gearui.components.actionsheet.ActionSheetItem
import com.gearui.components.button.ButtonIconPosition
import com.gearui.components.button.ButtonImpl
import com.gearui.components.button.ButtonShape
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.icon.*
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.connected.SegmentPosition
import com.gearui.foundation.connected.connectedShape
import com.gearui.foundation.connected.segmentPosition
import com.gearui.i18n.I18n
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.Dp

/** One button of a [ButtonGroup]: text, an icon, or both. */
class ButtonGroupItem(
    val text: String = "",
    val onClick: () -> Unit,
    val icon: IconSource? = null,
    val disabled: Boolean = false,
    /** Required for an icon-only item: its name for a screen reader. */
    val contentDescription: String? = null,
)

/**
 * Related actions as one connected row — "Reply · Forward · Delete", "Bold Italic
 * Underline": pill-shaped on the outside, nearly square where neighbours meet, a hairline
 * gap between them. Each segment is a real Button (colours, press feedback, 44 hit
 * region). With [block] the row fills the width and the segments share it equally.
 */
@Composable
fun ButtonGroup(
    items: List<ButtonGroupItem>,
    modifier: Modifier = Modifier,
    theme: ButtonTheme = ButtonTheme.DEFAULT,
    type: ButtonType = ButtonType.OUTLINE,
    size: ButtonSize = ButtonSize.MEDIUM,
    block: Boolean = false,
) {
    val outer = Theme.shapes.full
    val inner = Theme.shapes.sm
    Row(
        modifier.then(if (block) Modifier.fillMaxWidth() else Modifier),
        horizontalArrangement = Arrangement.spacedBy(SEGMENT_GAP),
    ) {
        items.forEachIndexed { index, item ->
            require(item.text.isNotEmpty() || item.contentDescription != null) {
                "an icon-only ButtonGroupItem needs a contentDescription"
            }
            ButtonImpl(
                text = item.text, onClick = item.onClick,
                modifier = if (block) Modifier.weight(1f) else Modifier,
                theme = theme, type = type, size = size,
                // SQUARE makes an icon-only segment as wide as it is tall; the shape itself
                // comes from shapeOverride.
                shape = if (item.text.isEmpty()) ButtonShape.SQUARE else ButtonShape.RECTANGLE,
                disabled = item.disabled, loading = false, block = block, icon = item.icon,
                iconWidget = null, iconPosition = ButtonIconPosition.LEFT, iconTextSpacing = Dp.Unspecified,
                contentDescription = item.contentDescription,
                shapeOverride = connectedShape(segmentPosition(index, items.size), outer, inner),
            )
        }
    }
}

/** One choice in a [SplitButton]'s menu. */
class SplitButtonItem(
    val label: String,
    val onClick: () -> Unit,
    val icon: IconSource? = null,
    /** Shown in the destructive colour, as delete is. */
    val destructive: Boolean = false,
    val disabled: Boolean = false,
)

/**
 * A main action with its alternatives behind it — "Send · ▾ (schedule, send as file)":
 * the left part does [onClick]; the right part opens the [menuItems] in an action sheet,
 * as a phone presents a menu. The two parts are connected like a [ButtonGroup].
 */
@Composable
fun SplitButton(
    text: String,
    onClick: () -> Unit,
    menuItems: List<SplitButtonItem>,
    modifier: Modifier = Modifier,
    theme: ButtonTheme = ButtonTheme.PRIMARY,
    type: ButtonType = ButtonType.FILL,
    size: ButtonSize = ButtonSize.MEDIUM,
    icon: IconSource? = null,
    disabled: Boolean = false,
    /** A line above the choices in the sheet. */
    menuDescription: String? = null,
) {
    val colors = Theme.colors
    val outer = Theme.shapes.full
    val inner = Theme.shapes.sm
    val more = I18n.actions.moreOptions
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(SEGMENT_GAP)) {
        ButtonImpl(
            text = text, onClick = onClick, modifier = Modifier, theme = theme, type = type, size = size,
            shape = ButtonShape.RECTANGLE, disabled = disabled, loading = false, block = false, icon = icon,
            iconWidget = null, iconPosition = ButtonIconPosition.LEFT, iconTextSpacing = Dp.Unspecified,
            contentDescription = null, shapeOverride = connectedShape(SegmentPosition.First, outer, inner),
        )
        ButtonImpl(
            text = "", modifier = Modifier, theme = theme, type = type, size = size,
            shape = ButtonShape.SQUARE, disabled = disabled || menuItems.isEmpty(), loading = false,
            block = false, icon = Icons.caretDown, iconWidget = null, iconPosition = ButtonIconPosition.LEFT,
            iconTextSpacing = Dp.Unspecified, contentDescription = more,
            shapeOverride = connectedShape(SegmentPosition.Last, outer, inner),
            onClick = {
                ActionSheet.showList(
                    items = menuItems.map {
                        ActionSheetItem(label = it.label, icon = it.icon, disabled = it.disabled,
                            textColor = if (it.destructive) colors.destructive else null)
                    },
                    description = menuDescription,
                    onSelected = { _, index -> menuItems[index].onClick() },
                )
            },
        )
    }
}

// The line between segments: the group reads as one control, each segment still its own.
private val SEGMENT_GAP: Dp = BorderWidth.thick
