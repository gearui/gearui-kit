package com.gearui.primitives.composite

import androidx.compose.runtime.Composable
import com.gearui.components.cell.Cell
import com.tencent.kuikly.compose.ui.Modifier

/**
 * ListItem — a row named after what it holds: a title, an optional subtitle and an
 * optional value on the right.
 *
 * It is [Cell] with the arrow decided for you: a row that does something gets the
 * chevron, a row that only reports a value does not. Everything else — geometry,
 * colours, press feedback — is Cell's, because there is one row in this library.
 *
 * Which of the three to reach for:
 * - [Cell] / ListItem: **one row**.
 * - [com.gearui.components.cellgroup.CellGroup]: **a card of rows**, with the group
 *   title, the surface and the separators between them.
 * - [com.gearui.foundation.primitives.List]: **the scrolling container**, a lazy list
 *   with `item` / `items` / `section`. It says nothing about how a row looks; its rows
 *   are Cells too.
 *
 * ```kotlin
 * ListItem(title = "Notifications", subtitle = "New message alerts", value = "On")
 * ListItem(title = "Account and security", onClick = ::openSecurity)
 * ```
 */
@Composable
fun ListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    value: String? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Cell(
        title = title,
        modifier = modifier,
        note = value,
        description = subtitle,
        // A row that goes somewhere says so; one that shows a value does not.
        arrow = onClick != null && trailing == null,
        enabled = enabled,
        onClick = onClick,
        leading = leading,
        trailing = trailing,
    )
}
