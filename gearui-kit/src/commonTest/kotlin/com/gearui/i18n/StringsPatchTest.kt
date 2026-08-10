package com.gearui.i18n

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Strings is split into semantic domains, so a patch is nested one level
 * deeper than it used to be. These tests pin down that overriding a single
 * field stays a one-liner and never forces callers to rebuild a whole domain.
 */
class StringsPatchTest {

    @Test
    fun singleFieldOverrideLeavesEverythingElseIntact() {
        val base = StringPacks.ChineseSimplified
        val patched = base.merge(
            StringsPatch(common = CommonStringsPatch(confirm = "确定一下")),
        )

        assertEquals("确定一下", patched.common.confirm)
        // Same domain, untouched field.
        assertEquals(base.common.cancel, patched.common.cancel)
        // Untouched domains are carried over as-is.
        assertEquals(base.field.selectPlaceholder, patched.field.selectPlaceholder)
        assertEquals(base.dateTime.weekdaysShort, patched.dateTime.weekdaysShort)
        assertEquals(base.guide.rateDescriptions, patched.guide.rateDescriptions)
    }

    @Test
    fun flatFacadeStillReflectsDomainOverrides() {
        val patched = StringPacks.English.merge(
            StringsPatch(theming = ThemeStringsPatch(theme = "Appearance")),
        )
        assertEquals("Appearance", patched.theme)
        assertEquals("Appearance", patched.theming.theme)
    }

    @Test
    fun overridesAcrossDomainsCompose() {
        val patched = StringPacks.English.merge(
            StringsPatch(
                common = CommonStringsPatch(cancel = "Dismiss"),
                field = FieldStringsPatch(selectPlaceholder = "Pick one"),
            ),
        )
        assertEquals("Dismiss", patched.common.cancel)
        assertEquals("Pick one", patched.field.selectPlaceholder)
        assertEquals("Confirm", patched.common.confirm)
    }

    @Test
    fun emptyPatchIsZeroAllocationAndIdentityPreserving() {
        val base = StringPacks.English
        assertSame(base, base.merge(null))
        assertSame(base, base.merge(StringsPatch()))
        // A domain that is present but all-null must not count as a change.
        assertSame(base, base.merge(StringsPatch(common = CommonStringsPatch())))
        assertTrue(StringsPatch(common = CommonStringsPatch()).isEmpty)
    }

    @Test
    fun everyBuiltInPackIsFullyPopulated() {
        for ((tag, pack) in StringPacks.builtIn) {
            val blanks = listOf(
                "common.confirm" to pack.common.confirm,
                "common.cancel" to pack.common.cancel,
                "field.selectPlaceholder" to pack.field.selectPlaceholder,
                "feedback.notFoundTitle" to pack.feedback.notFoundTitle,
                "media.imageEmpty" to pack.media.imageEmpty,
                "guide.tourFinish" to pack.guide.tourFinish,
            ).filter { it.second.isBlank() }
            assertTrue(blanks.isEmpty(), "$tag has blank keys: ${blanks.map { it.first }}")

            assertEquals(7, pack.dateTime.weekdaysShort.size, "$tag weekdaysShort")
            assertEquals(5, pack.guide.rateDescriptions.size, "$tag rateDescriptions")
        }
    }

    @Test
    fun formatArgsExpandsPlaceholdersAndKeepsUnknownOnesVisible() {
        assertEquals(
            "已选择 3 项",
            "已选择 {count} 项".formatArgs("count" to 3),
        )
        assertEquals(
            "2026年8月",
            "{year}年{month}月".formatArgs("year" to 2026, "month" to 8),
        )
        // A missing placeholder must stay visible rather than silently vanish.
        assertEquals("Image {index}", "Image {index}".formatArgs("wrong" to 1))
    }
}
