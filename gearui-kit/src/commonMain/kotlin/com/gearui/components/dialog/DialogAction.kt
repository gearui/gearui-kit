package com.gearui.components.dialog

/**
 * Semantic role of a dialog action, which decides how it is drawn.
 *
 * Roles rather than colours: a caller says what an action *means* and the
 * dialog renders it consistently. Passing a Button with its own type/theme is
 * how the old API let every screen invent its own alert.
 */
enum class DialogActionRole {
    /** Ordinary choice. Tinted with the brand colour. */
    NORMAL,

    /** The action the dialog is asking for. Same tint, semibold. */
    PRIMARY,

    /** Deletes, leaves, revokes. Drawn in the destructive colour. */
    DESTRUCTIVE,

    /** Backing out. Semibold, and always placed last — see [DialogContent]. */
    CANCEL,
}

/**
 * One row in a dialog's action list.
 *
 * @param text the label; pass a different string to show progress ("Deleting…")
 * @param role how it is drawn, see [DialogActionRole]
 * @param enabled false greys the row and ignores taps, for in-flight work
 * @param onClick invoked on tap
 */
data class DialogAction(
    val text: String,
    val role: DialogActionRole = DialogActionRole.NORMAL,
    val enabled: Boolean = true,
    val onClick: () -> Unit,
)
