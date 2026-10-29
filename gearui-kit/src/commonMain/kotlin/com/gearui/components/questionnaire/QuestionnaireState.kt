package com.gearui.components.questionnaire

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/** One fixed answer to a [QuestionnaireItem]. [value] is what the answers report. */
class QuestionnaireChoice(
    val value: String,
    val label: String,
    /** A line under the label: what choosing it means. */
    val description: String? = null,
    /** A short mark after the label, such as "Recommended". */
    val badge: String? = null,
    val disabled: Boolean = false,
)

/**
 * A free-text answer next to the fixed choices — "Other". [label] names the field for
 * everyone, screen readers included; a placeholder is not a name.
 */
class QuestionnaireInput(
    val label: String,
    val placeholder: String = "",
)

/**
 * One question. [name] keys its answer. With [multiple] the choices are checkboxes,
 * otherwise radios; with [input] the user can also type an answer. A [required] item
 * must be answered to move on; an optional one can be skipped. [appliesWhen] leaves the
 * item out — not asked, not counted, not reported — unless it holds for the answers so far.
 */
class QuestionnaireItem(
    val name: String,
    val prompt: String,
    val description: String? = null,
    val choices: List<QuestionnaireChoice> = emptyList(),
    val multiple: Boolean = false,
    val input: QuestionnaireInput? = null,
    val required: Boolean = true,
    val appliesWhen: ((Map<String, QuestionnaireAnswer>) -> Boolean)? = null,
) {
    init {
        require(choices.isNotEmpty() || input != null) { "item '$name' has neither choices nor an input" }
        require(choices.map { it.value }.distinct().size == choices.size) { "item '$name' repeats a choice value" }
    }
}

/**
 * The answer to one item: the chosen [choices] and the typed [text]. For a single-choice
 * item they exclude each other — choosing clears the text, typing clears the choice.
 */
class QuestionnaireAnswer(
    val choices: Set<String> = emptySet(),
    val text: String = "",
) {
    val isEmpty: Boolean get() = choices.isEmpty() && text.isBlank()

    override fun equals(other: Any?): Boolean =
        other is QuestionnaireAnswer && other.choices == choices && other.text == text

    override fun hashCode(): Int = 31 * choices.hashCode() + text.hashCode()

    override fun toString(): String = "QuestionnaireAnswer(choices=$choices, text=$text)"
}

/**
 * Where a [Questionnaire] stands: the answers, the item on screen, and the item whose
 * missing answer is being reported. Hoist it to read or restore answers (resume a
 * questionnaire), or to move between items from outside.
 */
@Stable
class QuestionnaireState(
    initialAnswers: Map<String, QuestionnaireAnswer> = emptyMap(),
    initialItem: String? = null,
) {
    var answers: Map<String, QuestionnaireAnswer> by mutableStateOf(initialAnswers)
        private set

    /** The name of the item on screen; null means the first one that applies. */
    var currentName: String? by mutableStateOf(initialItem)
        private set

    /** The item whose required answer is missing, until it is answered or left. */
    var invalidName: String? by mutableStateOf(null)
        private set

    /** The items that apply to the answers so far, in order. */
    fun applicable(items: List<QuestionnaireItem>): List<QuestionnaireItem> =
        items.filter { it.appliesWhen?.invoke(answers) ?: true }

    /** The item on screen. */
    fun current(items: List<QuestionnaireItem>): QuestionnaireItem? {
        val live = applicable(items)
        live.firstOrNull { it.name == currentName }?.let { return it }
        // The item on screen stopped applying (an earlier answer changed): stay in place —
        // the next item that applies after it, not back at the start.
        val at = items.indexOfFirst { it.name == currentName }
        if (at >= 0) items.drop(at + 1).firstOrNull { it in live }?.let { return it }
        return if (at >= 0) live.lastOrNull() else live.firstOrNull()
    }

    fun answerOf(name: String): QuestionnaireAnswer = answers[name] ?: QuestionnaireAnswer()

    /** Chooses [value] for [item]: replaces the choice (single), or toggles it (multiple). */
    fun choose(item: QuestionnaireItem, value: String) {
        val before = answerOf(item.name)
        val after = if (item.multiple) {
            QuestionnaireAnswer(if (value in before.choices) before.choices - value else before.choices + value, before.text)
        } else {
            QuestionnaireAnswer(setOf(value), "")
        }
        set(item, after)
    }

    /** Types into [item]'s input. For a single-choice item the typed answer replaces the choice. */
    fun type(item: QuestionnaireItem, text: String) {
        val before = answerOf(item.name)
        set(item, QuestionnaireAnswer(if (item.multiple || text.isEmpty()) before.choices else emptySet(), text))
    }

    private fun set(item: QuestionnaireItem, answer: QuestionnaireAnswer) {
        answers = answers + (item.name to answer)
        if (invalidName == item.name && !answer.isEmpty) invalidName = null
    }

    /** Whether the current item lets the user go on. */
    fun canLeave(item: QuestionnaireItem): Boolean = !item.required || !answerOf(item.name).isEmpty

    /**
     * Moves to the next item that applies, if the current one may be left; a missing
     * required answer is reported instead. Returns whether it moved. On the last item
     * there is nowhere to go: call [submission].
     */
    fun next(items: List<QuestionnaireItem>): Boolean {
        val item = current(items) ?: return false
        if (!canLeave(item)) {
            invalidName = item.name
            return false
        }
        val live = applicable(items)
        val target = live.getOrNull(live.indexOf(item) + 1) ?: return false
        invalidName = null
        currentName = target.name
        return true
    }

    /** Leaves an optional item unanswered and moves on. Returns whether it moved. */
    fun skip(items: List<QuestionnaireItem>): Boolean {
        val item = current(items) ?: return false
        if (item.required) return false
        answers = answers - item.name
        invalidName = null
        val live = applicable(items)
        val target = live.getOrNull(live.indexOf(item) + 1) ?: return false
        currentName = target.name
        return true
    }

    /** Goes back one item. Answers are kept. */
    fun previous(items: List<QuestionnaireItem>): Boolean {
        val item = current(items) ?: return false
        val live = applicable(items)
        val target = live.getOrNull(live.indexOf(item) - 1) ?: return false
        invalidName = null
        currentName = target.name
        return true
    }

    /** Shows [name], answered or not — to let the user revise an earlier answer. */
    fun goTo(name: String) {
        invalidName = null
        currentName = name
    }

    /**
     * The answers to report, or null when a required item that applies is unanswered —
     * that item is then shown and reported. Only items that apply are included, and only
     * those answered.
     */
    fun submission(items: List<QuestionnaireItem>): Map<String, QuestionnaireAnswer>? {
        val live = applicable(items)
        live.firstOrNull { !canLeave(it) }?.let { missing ->
            currentName = missing.name
            invalidName = missing.name
            return null
        }
        return live.mapNotNull { item -> answers[item.name]?.takeUnless { it.isEmpty }?.let { item.name to it } }.toMap()
    }

    /** Starts over: no answers, the first item. */
    fun reset() {
        answers = emptyMap()
        currentName = null
        invalidName = null
    }
}

/** A [QuestionnaireState] kept across recomposition. */
@Composable
fun rememberQuestionnaireState(
    initialAnswers: Map<String, QuestionnaireAnswer> = emptyMap(),
    initialItem: String? = null,
): QuestionnaireState = remember { QuestionnaireState(initialAnswers, initialItem) }
