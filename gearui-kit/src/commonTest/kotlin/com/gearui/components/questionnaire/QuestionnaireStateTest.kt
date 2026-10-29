package com.gearui.components.questionnaire

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class QuestionnaireStateTest {
    private val approach = QuestionnaireItem(
        name = "approach",
        prompt = "Which approach?",
        choices = listOf(QuestionnaireChoice("min", "Minimal"), QuestionnaireChoice("full", "Rewrite")),
        input = QuestionnaireInput("Other"),
    )
    private val platforms = QuestionnaireItem(
        name = "platforms",
        prompt = "Which platforms?",
        choices = listOf(QuestionnaireChoice("ios", "iOS"), QuestionnaireChoice("android", "Android")),
        multiple = true,
        input = QuestionnaireInput("Other"),
    )
    private val notes = QuestionnaireItem(
        name = "notes",
        prompt = "Anything else?",
        input = QuestionnaireInput("Notes"),
        required = false,
    )
    // Asked only after choosing the rewrite.
    private val migration = QuestionnaireItem(
        name = "migration",
        prompt = "Migrate in steps?",
        choices = listOf(QuestionnaireChoice("yes", "Yes"), QuestionnaireChoice("no", "No")),
        appliesWhen = { "full" in (it["approach"]?.choices ?: emptySet()) },
    )

    @Test
    fun aSingleChoiceAndTheTypedAnswerExcludeEachOther() {
        val s = QuestionnaireState()
        s.choose(approach, "min")
        s.type(approach, "my own")
        assertEquals(QuestionnaireAnswer(emptySet(), "my own"), s.answerOf("approach"))
        s.choose(approach, "full")
        assertEquals(QuestionnaireAnswer(setOf("full"), ""), s.answerOf("approach"))
        // Choosing again keeps it: a radio is not toggled off.
        s.choose(approach, "full")
        assertEquals(setOf("full"), s.answerOf("approach").choices)
    }

    @Test
    fun multipleChoicesToggleAndKeepTheTypedAnswer() {
        val s = QuestionnaireState()
        s.choose(platforms, "ios")
        s.type(platforms, "web")
        s.choose(platforms, "android")
        assertEquals(QuestionnaireAnswer(setOf("ios", "android"), "web"), s.answerOf("platforms"))
        s.choose(platforms, "ios")
        assertEquals(setOf("android"), s.answerOf("platforms").choices)
    }

    @Test
    fun aRequiredItemBlocksNextUntilAnswered() {
        val items = listOf(approach, notes)
        val s = QuestionnaireState()
        assertFalse(s.next(items))
        assertEquals("approach", s.invalidName)
        s.choose(approach, "min")
        assertNull(s.invalidName)
        assertTrue(s.next(items))
        assertEquals("notes", s.current(items)?.name)
    }

    @Test
    fun aBlankTypedAnswerDoesNotCount() {
        val s = QuestionnaireState()
        s.type(approach, "   ")
        assertFalse(s.canLeave(approach))
    }

    @Test
    fun skippingAnOptionalItemDropsItsAnswer() {
        val items = listOf(approach, notes, platforms)
        val s = QuestionnaireState()
        s.choose(approach, "min")
        s.next(items)
        s.type(notes, "draft")
        assertTrue(s.skip(items))
        assertEquals("platforms", s.current(items)?.name)
        assertFalse("notes" in s.answers)
        // A required item cannot be skipped.
        assertFalse(s.skip(items))
    }

    @Test
    fun previousKeepsTheAnswers() {
        val items = listOf(approach, platforms)
        val s = QuestionnaireState()
        s.choose(approach, "min")
        s.next(items)
        assertTrue(s.previous(items))
        assertEquals("approach", s.current(items)?.name)
        assertEquals(setOf("min"), s.answerOf("approach").choices)
        assertFalse(s.previous(items))
    }

    @Test
    fun aConditionalItemIsAskedOnlyWhenItApplies() {
        val items = listOf(approach, migration, platforms)
        val s = QuestionnaireState()
        s.choose(approach, "min")
        assertEquals(listOf("approach", "platforms"), s.applicable(items).map { it.name })
        s.choose(approach, "full")
        s.next(items)
        assertEquals("migration", s.current(items)?.name)
    }

    @Test
    fun anItemThatStopsApplyingDoesNotSendTheUserBackToTheStart() {
        val items = listOf(approach, migration, platforms)
        val s = QuestionnaireState(initialAnswers = mapOf("approach" to QuestionnaireAnswer(setOf("full"))), initialItem = "migration")
        s.choose(approach, "min")
        assertEquals("platforms", s.current(items)?.name)
    }

    @Test
    fun submissionReportsOnlyAnsweredItemsThatApply() {
        val items = listOf(approach, migration, notes)
        val s = QuestionnaireState()
        s.choose(approach, "full")
        s.choose(migration, "yes")
        // Changing the earlier answer leaves the migration item out, answer and all.
        s.choose(approach, "min")
        val sent = s.submission(items)
        assertEquals(mapOf("approach" to QuestionnaireAnswer(setOf("min"))), sent)
    }

    @Test
    fun submissionShowsTheFirstMissingRequiredItem() {
        val items = listOf(approach, platforms)
        val s = QuestionnaireState(initialItem = "platforms")
        assertNull(s.submission(items))
        assertEquals("approach", s.current(items)?.name)
        assertEquals("approach", s.invalidName)
    }

    @Test
    fun resetStartsOver() {
        val items = listOf(approach, platforms)
        val s = QuestionnaireState()
        s.choose(approach, "min")
        s.next(items)
        s.reset()
        assertTrue(s.answers.isEmpty())
        assertEquals("approach", s.current(items)?.name)
    }
}
