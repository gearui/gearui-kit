package com.gearui.components.dialog

/**
 * How a dialog lays out its actions.
 *
 * The layout is a property of the *question*, not of the screen that asks it —
 * so it is decided here, from the actions' roles and count, and every dialog in
 * an app answers the same way. A caller may name one explicitly for the one case
 * the policy cannot see (a form body whose buttons are secondary to its content);
 * otherwise [resolveDialogActionLayout] picks.
 *
 * Two traditions meet here. HeroUI Native's dialog example puts small buttons on
 * the trailing edge, always; iOS's alert gives its actions the card's full width,
 * one across when there is one, split when there are two, stacked when they do
 * not fit. The trailing row is right for a form that happens to be in a dialog.
 * It is wrong for an alert: one small pill in the corner of a wide card is a
 * decoration, and the thumb has to find it. HeroUI is the floor for this kit,
 * not the ceiling, so an alert takes the platform's answer and a form keeps the
 * reference's — and which one you get is a rule, not a screen's choice.
 */
enum class DialogActionLayout {
    /** One action, the card's full width. A single "OK" is also "I have read this". */
    BLOCK,

    /**
     * Two actions side by side, equal width, the cancelling one on the leading edge
     * and the one the dialog asks for on the trailing edge. A choice between two
     * things is shown as two equal targets, not one big and one small.
     */
    SPLIT,

    /**
     * Every action on its own full-width row. For three or more, for a destructive
     * action (which must not share a row with the safe way out), and for labels too
     * long to split. The action asked for comes first; CANCEL is always last, the
     * platform's convention for the row the eye lands on before leaving.
     */
    STACKED,

    /**
     * Small buttons on the trailing edge — the reference's form footer. Never chosen
     * automatically: it belongs to a dialog whose body is the point and whose
     * buttons only close it, and only the caller knows that.
     */
    TRAILING,
}

/**
 * Longest label, in characters, that still fits half a card.
 *
 * Measured in characters because Kuikly cannot measure text at this point, so the
 * threshold is conservative: a wrong guess must never clip a label. Half of the
 * narrowest dialog, less padding and gap, holds about nine CJK characters at body
 * size; six leaves room for wider Latin labels and a large system font.
 */
internal const val SPLIT_MAX_CHARS = 6

/**
 * The layout for [actions] when the caller has not named one.
 *
 * The rule, in order:
 * 1. exactly one → [DialogActionLayout.BLOCK];
 * 2. more than two, any destructive, or any label longer than [SPLIT_MAX_CHARS]
 *    → [DialogActionLayout.STACKED];
 * 3. otherwise (two ordinary actions) → [DialogActionLayout.SPLIT].
 *
 * Never returns [DialogActionLayout.TRAILING]; that is opt-in only. Empty input
 * resolves to BLOCK, which draws nothing.
 */
internal fun resolveDialogActionLayout(actions: List<DialogAction>): DialogActionLayout = when {
    actions.size <= 1 -> DialogActionLayout.BLOCK
    actions.size > 2 -> DialogActionLayout.STACKED
    actions.any { it.role == DialogActionRole.DESTRUCTIVE } -> DialogActionLayout.STACKED
    actions.any { it.text.length > SPLIT_MAX_CHARS } -> DialogActionLayout.STACKED
    else -> DialogActionLayout.SPLIT
}

/**
 * [actions] in drawing order for [layout].
 *
 * Cancel goes where the platform puts it: on the leading edge of a split or
 * trailing row (the safe way out is read first, the commitment is on the edge the
 * thumb reaches last), and at the bottom of a stack (the last row before leaving).
 * Everything else keeps the caller's order.
 */
internal fun orderDialogActions(
    actions: List<DialogAction>,
    layout: DialogActionLayout,
): List<DialogAction> {
    val cancels = actions.filter { it.role == DialogActionRole.CANCEL }
    val others = actions.filter { it.role != DialogActionRole.CANCEL }
    return when (layout) {
        DialogActionLayout.STACKED -> others + cancels
        DialogActionLayout.SPLIT, DialogActionLayout.TRAILING -> cancels + others
        DialogActionLayout.BLOCK -> actions
    }
}
