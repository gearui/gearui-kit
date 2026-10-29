package com.gearui.components.questionnaire

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.checkbox.CheckboxMark
import com.gearui.components.checkbox.CheckboxSize
import com.gearui.components.input.Input
import com.gearui.components.progress.LinearProgress
import com.gearui.components.radio.RadioMark
import com.gearui.components.radio.RadioSize
import com.gearui.components.tag.Tag
import com.gearui.components.tag.TagSize
import com.gearui.components.tag.TagTheme
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.field.FieldErrorText
import com.gearui.foundation.interaction.choiceSemantics
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.motion.rowPressFeedback
import com.gearui.foundation.primitives.Text
import com.gearui.i18n.I18n
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.interaction.collectIsPressedAsState
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.heightIn
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Questions asked one at a time, each answered by choosing, typing, or both — what an
 * agent shows before it acts ("Which approach?"), a setup wizard, a short survey.
 *
 * One item is a question card: its prompt, its choices as cards (radios, or checkboxes
 * with [QuestionnaireItem.multiple]), an optional "Other" field, and the action. With
 * more items there is progress, Previous, Skip for optional items, and Next until the
 * last item, where the action submits. Items whose [QuestionnaireItem.appliesWhen] does
 * not hold are not asked and not counted.
 *
 * A required item blocks Next and Submit until it is answered, and says so under the
 * item. [onSubmit] receives the answers of the items that apply, keyed by name; what
 * happens next — send them to the agent, close the sheet — is the caller's.
 *
 * It does not scroll by itself: place it in the page's scroll, a card or a BottomSheet.
 *
 * @param state the answers and the item on screen; hoist it to restore or read them.
 * @param advanceOnChoice choosing an answer of a single-choice item goes on at once (the
 *   last item submits) — for quick decisions in a conversation. Such an item, when it has
 *   no text field, shows no Next or Submit: the choice is the answer. Typing never advances.
 * @param submitLabel the last item's action.
 */
@Composable
fun Questionnaire(
    items: List<QuestionnaireItem>,
    onSubmit: (Map<String, QuestionnaireAnswer>) -> Unit,
    modifier: Modifier = Modifier,
    state: QuestionnaireState = rememberQuestionnaireState(),
    advanceOnChoice: Boolean = false,
    submitLabel: String = I18n.strings.guide.tourFinish,
    enabled: Boolean = true,
) {
    require(items.map { it.name }.distinct().size == items.size) { "questionnaire item names repeat" }
    val colors = Theme.colors
    val strings = I18n.strings
    val scope = rememberCoroutineScope()
    val motion = Theme.motion
    val live = state.applicable(items)
    val item = state.current(items) ?: return
    val index = live.indexOf(item)
    val last = index == live.lastIndex

    fun submit() {
        state.submission(items)?.let(onSubmit)
    }
    fun forward() {
        if (last) submit() else state.next(items)
    }

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        if (live.size > 1) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text("${index + 1} / ${live.size}", style = Theme.typography.bodySmall, color = colors.mutedForeground)
                LinearProgress(progress = (index + 1f) / live.size, showLabel = false, animated = true)
            }
        }
        key(item.name) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(item.prompt, style = Theme.typography.titleMedium, color = colors.foreground)
                    item.description?.let {
                        Text(it, style = Theme.typography.bodySmall, color = colors.mutedForeground)
                    }
                }
                val answer = state.answerOf(item.name)
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    item.choices.forEach { choice ->
                        ChoiceCard(
                            choice = choice,
                            selected = choice.value in answer.choices,
                            multiple = item.multiple,
                            enabled = enabled && !choice.disabled,
                        ) {
                            state.choose(item, choice.value)
                            if (advanceOnChoice && !item.multiple) {
                                // Long enough to see the choice land before the item changes.
                                scope.launch {
                                    delay(motion.slow.toLong())
                                    if (state.current(items)?.name == item.name) forward()
                                }
                            }
                        }
                    }
                    item.input?.let { input ->
                        Input(
                            value = answer.text,
                            onValueChange = { state.type(item, it) },
                            label = input.label,
                            placeholder = input.placeholder,
                            enabled = enabled,
                        )
                    }
                }
                FieldErrorText(if (state.invalidName == item.name) strings.feedback.fieldRequired else null)
            }
        }
        // With advanceOnChoice a single-choice item without a text field is answered by the
        // choice itself, so it has no forward action.
        val showForward = !(advanceOnChoice && !item.multiple && item.input == null)
        if (index > 0 || !item.required || showForward) Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (index > 0) {
                Button(strings.guide.tourPrevious, { state.previous(items) }, theme = ButtonTheme.DEFAULT,
                    type = ButtonType.OUTLINE, disabled = !enabled)
            }
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalAlignment = Alignment.CenterVertically) {
                if (!item.required) {
                    Button(strings.guide.tourSkip, { if (!state.skip(items) && last) submit() },
                        theme = ButtonTheme.DEFAULT, type = ButtonType.TEXT, disabled = !enabled)
                }
                if (showForward) Button(if (last) submitLabel else strings.guide.tourNext, ::forward, disabled = !enabled)
            }
        }
    }
}

/** One answer as a card: the mark, the label and its description, an optional badge. */
@Composable
private fun ChoiceCard(
    choice: QuestionnaireChoice,
    selected: Boolean,
    multiple: Boolean,
    enabled: Boolean,
    onChoose: () -> Unit,
) {
    val colors = Theme.colors
    val shape = Theme.shapes.md
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val base = if (selected) colors.primarySoft else colors.card
    // Read as one control: "label, description, selected".
    val spoken = listOfNotNull(choice.label, choice.badge, choice.description).joinToString(", ")
    Row(
        Modifier.fillMaxWidth()
            .heightIn(min = ControlGeometry.selectionTouchTarget)
            .clip(shape)
            .choiceSemantics(spoken, selected, if (multiple) Role.Checkbox else Role.RadioButton, if (enabled) onChoose else null)
            .rowPressFeedback(interaction, shape, enabled = enabled, scale = false, base = base)
            .border(if (selected) BorderWidth.thick else BorderWidth.thin, if (selected) colors.primary else colors.border, shape)
            .clickable(enabled = enabled, interactionSource = interaction, indication = null, onClick = onChoose)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        if (multiple) CheckboxMark(selected, false, enabled, pressed, CheckboxSize.MEDIUM)
        else RadioMark(selected, enabled, pressed, RadioSize.MEDIUM)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    choice.label,
                    style = Theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = if (enabled) colors.foreground else colors.mutedForeground,
                    modifier = Modifier.weight(1f, fill = false),
                )
                choice.badge?.let { Tag(it, theme = TagTheme.PRIMARY, size = TagSize.SMALL) }
            }
            choice.description?.let {
                Text(it, style = Theme.typography.bodySmall, color = colors.mutedForeground)
            }
        }
    }
}
