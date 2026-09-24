package com.gearui.components

import com.gearui.components.dialog.DialogAction
import com.gearui.components.dialog.DialogActionLayout
import com.gearui.components.dialog.DialogActionRole
import com.gearui.components.dialog.SPLIT_MAX_CHARS
import com.gearui.components.dialog.orderDialogActions
import com.gearui.components.dialog.resolveDialogActionLayout
import kotlin.test.Test
import kotlin.test.assertEquals

/** Which layout a dialog's actions get, and in what order they are drawn. */
class DialogActionLayoutTest {

    private fun action(text: String, role: DialogActionRole = DialogActionRole.NORMAL) =
        DialogAction(text = text, role = role, onClick = {})

    private val ok = action("OK", DialogActionRole.PRIMARY)
    private val cancel = action("Cancel", DialogActionRole.CANCEL)
    private val delete = action("Delete", DialogActionRole.DESTRUCTIVE)
    private val other = action("Later")

    @Test
    fun oneActionFillsTheCard() {
        assertEquals(DialogActionLayout.BLOCK, resolveDialogActionLayout(listOf(ok)))
    }

    @Test
    fun twoOrdinaryActionsSplitTheRow() {
        assertEquals(DialogActionLayout.SPLIT, resolveDialogActionLayout(listOf(ok, cancel)))
    }

    @Test
    fun aDestructiveActionNeverSharesARowWithCancel() {
        assertEquals(DialogActionLayout.STACKED, resolveDialogActionLayout(listOf(delete, cancel)))
    }

    @Test
    fun threeActionsStack() {
        assertEquals(DialogActionLayout.STACKED, resolveDialogActionLayout(listOf(ok, other, cancel)))
    }

    @Test
    fun aLabelTooLongForHalfACardStacks() {
        val long = action("x".repeat(SPLIT_MAX_CHARS + 1), DialogActionRole.PRIMARY)
        assertEquals(DialogActionLayout.STACKED, resolveDialogActionLayout(listOf(long, cancel)))
        val fits = action("x".repeat(SPLIT_MAX_CHARS), DialogActionRole.PRIMARY)
        assertEquals(DialogActionLayout.SPLIT, resolveDialogActionLayout(listOf(fits, cancel)))
    }

    @Test
    fun trailingIsNeverChosenAutomatically() {
        val cases = listOf(listOf(ok), listOf(ok, cancel), listOf(ok, other, cancel), listOf(delete, cancel))
        cases.forEach { assertEquals(false, resolveDialogActionLayout(it) == DialogActionLayout.TRAILING, "$it") }
    }

    @Test
    fun emptyResolvesToBlockWhichDrawsNothing() {
        assertEquals(DialogActionLayout.BLOCK, resolveDialogActionLayout(emptyList()))
    }

    @Test
    fun cancelLeadsASplitRowAndEndsAStack() {
        // Caller passes the commitment first, as ConfirmDialog does.
        val given = listOf(delete, cancel)
        assertEquals(listOf(cancel, delete), orderDialogActions(given, DialogActionLayout.SPLIT))
        assertEquals(listOf(cancel, delete), orderDialogActions(given, DialogActionLayout.TRAILING))
        assertEquals(listOf(delete, cancel), orderDialogActions(given, DialogActionLayout.STACKED))
    }

    @Test
    fun blockKeepsTheCallerOrder() {
        assertEquals(listOf(ok), orderDialogActions(listOf(ok), DialogActionLayout.BLOCK))
    }

    @Test
    fun nonCancelActionsKeepTheirRelativeOrder() {
        val given = listOf(ok, other, cancel)
        assertEquals(listOf(ok, other, cancel), orderDialogActions(given, DialogActionLayout.STACKED))
        assertEquals(listOf(cancel, ok, other), orderDialogActions(given, DialogActionLayout.TRAILING))
    }
}
