package com.gearui.components.cellgroup

import com.gearui.foundation.material.surfaceShadowStyles
import com.gearui.foundation.material.DecoratedSurface
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.gearui.foundation.list.LocalRowInteractionSource
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.interaction.collectIsPressedAsState
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.height
import com.gearui.foundation.control.ControlGeometry
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip

/**
 * CellGroup — a card of rows: an optional group title above it, the surface with its
 * radius and shadow, and a separator between each pair of rows (never after the last).
 *
 * It owns the grouping only; the rows are [com.gearui.components.cell.Cell]s, which is
 * why a row looks and presses the same inside a group as outside one. For a long or
 * scrolling list use [com.gearui.foundation.primitives.List], the lazy container, and
 * keep the rows the same.
 *
 * Three rules only a container can enforce, each previously left to whoever assembled
 * the list:
 *
 * - **The header aligns with the row text**, not with the card's edge. A header flush
 *   against the edge sits one padding step to the left of everything it labels.
 * - **Separators are inset** to where the text begins, so the line reads as dividing
 *   rows rather than boxing them.
 * - **The last row has no separator.** A caller placing its own dividers gets the first
 *   two right and cannot get this one right without counting, so it either draws a stray
 *   line above the card's bottom edge or drops separators entirely.
 *
 * `Cell` draws no separator of its own on purpose: a row does not know whether it is
 * last. That knowledge lives here.
 *
 * @param separatorInset where the separator starts. Defaults to the row's own horizontal
 *   padding, which aligns it with the text of a row that has no leading element. A group
 *   of rows **with** leading icons or avatars should pass the larger inset that aligns
 *   with their text — the group cannot measure its children to work this out.
 * @param titleTrailing optional element at the trailing end of the header row, for a
 *   count or an action.
 */
/**
 * Where a separator goes in a run of rows.
 *
 * Extracted so the rule can be tested: composition is where it is applied, not where it
 * is decided. The rule is "before every row but the first", which is the same thing as
 * "after every row but the last" and is the form that needs no count at the call site.
 */
internal fun separatorBeforeRow(index: Int): Boolean = index > 0

/**
 * Whether the separator before row [index] is covered by a press.
 *
 * A line belongs to the pair of rows it sits between, so it disappears when either of
 * them is pressed — otherwise the highlight of a pressed row is cut in two by a line
 * the row itself cannot reach. Extracted for the same reason as [separatorBeforeRow]:
 * the rule is what needs testing, not the composition that applies it.
 */
internal fun separatorCoveredByPress(index: Int, pressed: (Int) -> Boolean): Boolean =
    pressed(index) || pressed(index - 1)

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
                    // The title lines up with the rows' content edge, not with the
                    // separator. A group whose rows lead with an icon or avatar moves
                    // its separators past it, and tying the title to that inset pushed
                    // the title right with them, out of line with every other group.
                    // The platform aligns section titles to the content edge whatever
                    // the rows hold.
                    .padding(
                        start = CellDefaults.Default.paddingHorizontal,
                        end = CellDefaults.Default.paddingHorizontal,
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

        // Reference ListGroup is a Surface: surface colour, radius 24 and the surface
        // shadow stack.
        DecoratedSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = Theme.shapes.xl,
            shadows = surfaceShadowStyles().surface,
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface),
        ) {
            // One source per row, so the group can see which row is pressed. The
            // separators on either side of it are then filled with the press colour
            // instead of ruling across it: the platform's lists hide the lines that
            // touch a pressed row, and a line left showing cuts the highlight in two.
            val interactions = remember(items.size) {
                List(items.size) { MutableInteractionSource() }
            }
            val pressed = interactions.map { it.collectIsPressedAsState().value }

            items.forEachIndexed { index, item ->
                // Before each row but the first, so "no separator after the last"
                // needs no count and cannot be got wrong.
                if (separatorBeforeRow(index)) {
                    val covered = separatorCoveredByPress(index) { pressed.getOrElse(it) { false } }
                    if (covered) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(ControlGeometry.separatorThickness)
                                .background(colors.muted)
                        )
                    } else {
                        Divider(thickness = ControlGeometry.separatorThickness, insetStart = separatorInset)
                    }
                }
                CompositionLocalProvider(LocalRowInteractionSource provides interactions[index]) {
                    itemContent(item)
                }
            }
        }
        }
    }
}
