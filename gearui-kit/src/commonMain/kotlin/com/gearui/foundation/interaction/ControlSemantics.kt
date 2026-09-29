package com.gearui.foundation.interaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.gearui.i18n.I18n
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.semantics.clearAndSetSemantics
import com.tencent.kuikly.compose.ui.semantics.contentDescription
import com.tencent.kuikly.compose.ui.semantics.onClick
import com.tencent.kuikly.compose.ui.semantics.role

/**
 * The visible text that names the control placed beside it.
 *
 * A switch in a Cell's trailing slot, or a checkbox in a FormItem, has its name on screen
 * already — the row title, the field label — but in a different view, so a screen reader
 * focusing the control would hear only its state. The container hands its text down
 * through this local and the bare controls (Switch, Checkbox, RadioButton) use it when the
 * caller gave no `contentDescription`. This is the role HeroUI Native's Label plays for
 * the control it is attached to.
 *
 * Buttons do not read it: a button carries its own text, and a row title is not its name.
 */
internal val LocalControlLabel = compositionLocalOf<String?> { null }

/** Names a bare control: [explicit] if given, else the enclosing container's label. */
@Composable
internal fun controlLabel(explicit: String?): String? = explicit ?: LocalControlLabel.current

/**
 * Screen-reader semantics for a stateful control — a switch, a checkbox, a radio, a tab —
 * read as "label, extra, state", the order VoiceOver and TalkBack use for their own
 * controls ("Wi-Fi, on"; "Daily, selected").
 *
 * The state is spoken text because Kuikly bridges nothing else: `toggleableState` is
 * dropped, `selected` becomes a hardcoded Chinese suffix, and a `stateDescription` is read
 * *before* the label ("Unselected, Daily"). So the parts are joined here, the descendants'
 * text is cleared so nothing is read twice, and the click action is declared explicitly
 * so clearing the subtree does not leave the control impossible to activate by
 * double-tap. Pass a null [onClick] for a disabled control.
 *
 * Callers must not also apply `selectable` (its `selected` flag adds Kuikly's suffix).
 * Do not use it on a node whose children must stay reachable on their own, such as a tag
 * with a remove button — clearing would hide them.
 */
internal fun Modifier.controlSemantics(
    label: String?,
    state: String,
    role: Role,
    onClick: (() -> Unit)?,
    extra: String? = null,
): Modifier {
    val text = listOfNotNull(label?.takeIf { it.isNotBlank() }, extra?.takeIf { it.isNotBlank() }, state)
        .joinToString(", ")
    return clearAndSetSemantics {
        this.role = role
        contentDescription = text
        if (onClick != null) onClick(label = null) { onClick(); true }
    }
}

/** [controlSemantics] for one choice in a set: selected or not. */
@Composable
internal fun Modifier.choiceSemantics(
    label: String?,
    selected: Boolean,
    role: Role,
    onClick: (() -> Unit)?,
    extra: String? = null,
): Modifier = controlSemantics(
    label = label,
    state = if (selected) I18n.strings.common.selected else I18n.strings.common.unselected,
    role = role,
    onClick = onClick,
    extra = extra,
)
