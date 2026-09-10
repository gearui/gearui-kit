package com.gearui.components.cellgroup

import androidx.compose.runtime.Composable
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.list.CellDefaults
import com.gearui.foundation.primitives.Text
import com.gearui.primitives.Divider
import com.gearui.theme.Theme
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip

/**
 * A run of list rows, with the rules that only a container can enforce.
 *
 * Three of them, and each was previously left to whoever assembled the list:
 *
 * - **The header aligns with the row text**, not with the card's edge. A header
 *   flush against the edge sits one padding step to the left of everything it
 *   labels. The sample's own component list shipped that way, which is what
 *   prompted this component.
 * - **Separators are inset** to where the text begins, so the line reads as
 *   dividing rows rather than boxing them.
 * - **The last row has no separator.** A caller placing dividers between its own
 *   rows can get the first two right and cannot get this one right without
 *   counting, so in practice it either draws a stray line above the card's
 *   bottom edge or drops separators entirely.
 *
 * `Cell` deliberately draws no separator of its own; a row does not know whether
 * it is last. That knowledge lives here.
 *
 * @param separatorInset where the separator starts. Defaults to the row's own
 *   horizontal padding, which aligns it with the text of a row that has no
 *   leading element. A group of rows **with** leading icons or avatars should
 *   pass the larger inset that aligns with their text — the group cannot measure
 *   its children to work this out.
 * @param titleTrailing optional element at the trailing end of the header row,
 *   for a count or an action.
 */
/**
 * Where a separator goes in a run of [count] rows.
 *
 * Extracted so the rule can be tested: composition is where it is applied, not
 * where it is decided. The rule is "before every row but the first", which is
 * the same thing as "after every row but the last" and is the form that needs no
 * count at the call site — the form callers kept getting wrong.
 */
internal fun separatorBeforeRow(index: Int): Boolean = index > 0

@Composable
fun <T> CellGroup(
    items: List<T>,
    modifier: Modifier = Modifier,
    title: String? = null,
    titleTrailing: (@Composable () -> Unit)? = null,
    separatorInset: Dp = CellDefaults.Default.paddingHorizontal,
    itemContent: @Composable (T) -> Unit,
) {
    val colors = Theme.colors

    Column(modifier = modifier.fillMaxWidth()) {
        if (title != null || titleTrailing != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // The alignment rule, in one place: the same inset the rows
                    // give their content.
                    .padding(
                        start = separatorInset,
                        end = separatorInset,
                        top = Spacing.md,
                        bottom = Spacing.sm,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground,
                    )
                }
                titleTrailing?.invoke()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Theme.shapes.lg)
                .background(colors.surface),
        ) {
            items.forEachIndexed { index, item ->
                // Before each row but the first, so "no separator after the last"
                // needs no count and cannot be got wrong.
                if (separatorBeforeRow(index)) {
                    Divider(insetStart = separatorInset)
                }
                itemContent(item)
            }
        }
    }
}
