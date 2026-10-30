package com.gearui.components.aiblocks

import com.gearui.components.agenttrace.AgentStep
import com.gearui.components.agenttrace.traceOutcome
import com.gearui.components.agenttrace.traceSummary
import com.gearui.components.codeblock.DiffLineKind
import com.gearui.components.codeblock.diffLineKind
import com.gearui.components.codeblock.visibleLineCount
import com.gearui.components.codeblock.preserveSpaces
import com.gearui.components.codeblock.codeWidthEm
import com.gearui.components.reasoning.reasoningTitle
import com.gearui.foundation.status.ActivityStatus
import com.gearui.foundation.status.isFinished
import com.gearui.i18n.ActionStringsPatch
import com.gearui.i18n.ExtendedStringPacks
import com.gearui.i18n.merge
import com.gearui.i18n.resolveExtendedPatch
import com.gearui.i18n.resolveLanguagePack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AiBlocksLogicTest {
    @Test
    fun aLongBlockIsCutUntilExpanded() {
        assertEquals(12, visibleLineCount(total = 40, collapsedLines = 12, expanded = false))
        assertEquals(40, visibleLineCount(total = 40, collapsedLines = 12, expanded = true))
        assertEquals(5, visibleLineCount(total = 5, collapsedLines = 12, expanded = false))
        // Zero means never cut.
        assertEquals(40, visibleLineCount(total = 40, collapsedLines = 0, expanded = false))
    }

    @Test
    fun tabsExpandForDisplay() {
        assertEquals("    x", preserveSpaces("\tx"))
    }

    @Test
    fun codeWidthCountsWideCharactersFull() {
        assertEquals(0f, codeWidthEm(emptyList()))
        assertEquals(6.2f, codeWidthEm(listOf("ab", "abcdefghij")), 0.001f)
        // Two CJK characters weigh about as much as three and a half ASCII ones.
        assertEquals(2.1f, codeWidthEm(listOf("第行")), 0.001f)
    }

    @Test
    fun diffLinesAreClassifiedLikeAUnifiedDiff() {
        assertEquals(DiffLineKind.Added, diffLineKind("+ val x = 1"))
        assertEquals(DiffLineKind.Removed, diffLineKind("-val x = 0"))
        assertEquals(DiffLineKind.Hunk, diffLineKind("@@ -1,3 +1,4 @@"))
        // File headers are not changes.
        assertEquals(DiffLineKind.Context, diffLineKind("+++ b/Main.kt"))
        assertEquals(DiffLineKind.Context, diffLineKind("--- a/Main.kt"))
        assertEquals(DiffLineKind.Context, diffLineKind(" unchanged"))
    }

    @Test
    fun reasoningTitleFollowsTheModel() {
        val zh = ExtendedStringPacks.activityZhHans
        assertEquals("思考中", reasoningTitle(zh, thinking = true, durationSeconds = 9))
        assertEquals("已思考 9 秒", reasoningTitle(zh, thinking = false, durationSeconds = 9))
        assertEquals("思考过程", reasoningTitle(zh, thinking = false, durationSeconds = null))
    }

    private fun steps(vararg s: ActivityStatus) = s.mapIndexed { i, status -> AgentStep("$i", "step $i", status) }

    @Test
    fun aTraceReportsOneOutcome() {
        assertEquals(ActivityStatus.Pending, traceOutcome(emptyList()))
        assertEquals(ActivityStatus.Pending, traceOutcome(steps(ActivityStatus.Pending, ActivityStatus.Pending)))
        assertEquals(ActivityStatus.Running, traceOutcome(steps(ActivityStatus.Succeeded, ActivityStatus.Pending)))
        assertEquals(ActivityStatus.Running, traceOutcome(steps(ActivityStatus.Failed, ActivityStatus.Running)))
        assertEquals(ActivityStatus.Failed, traceOutcome(steps(ActivityStatus.Succeeded, ActivityStatus.Failed)))
        assertEquals(ActivityStatus.Succeeded, traceOutcome(steps(ActivityStatus.Succeeded, ActivityStatus.Cancelled)))
        assertEquals(ActivityStatus.Cancelled, traceOutcome(steps(ActivityStatus.Cancelled)))
    }

    @Test
    fun finishedMeansOverWhateverTheOutcome() {
        assertFalse(ActivityStatus.Pending.isFinished)
        assertFalse(ActivityStatus.Running.isFinished)
        assertTrue(ActivityStatus.Failed.isFinished)
    }

    @Test
    fun aTraceSummaryCountsSteps() {
        val en = ExtendedStringPacks.activityEnUs
        assertEquals("5 steps", traceSummary(en, null, 5))
        assertEquals("Plan · 5 steps", traceSummary(en, "Plan", 5))
    }

    @Test
    fun extendedStringsResolveLikeTheKitsOwn() {
        assertEquals("複製", resolveLanguagePack("zh-Hant-HK", ExtendedStringPacks.actions, "en-US").copy)
        assertEquals("Copy", resolveLanguagePack("fr-FR", ExtendedStringPacks.actions, "en-US").copy)
        val patch = ActionStringsPatch().apply { copy = "拷贝" }
        val overrides = mapOf("zh-Hans" to patch)
        val resolved = resolveLanguagePack("zh-Hans-CN", ExtendedStringPacks.actions, "en-US")
            .merge(resolveExtendedPatch("zh-Hans-CN", "en-US", overrides))
        assertEquals("拷贝", resolved.copy)
        // Unset fields keep the built-in copy.
        assertEquals("已复制", resolved.copied)
        assertNull(resolveExtendedPatch("ja", "en-US", overrides))
    }
}
