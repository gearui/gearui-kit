package com.gearui.components

import com.gearui.components.actionsheet.actionSheetRowCount
import kotlin.test.Test
import kotlin.test.assertEquals

class ActionSheetLayoutTest {
    @Test fun emptyGridHasNoRows() = assertEquals(0, actionSheetRowCount(0, 4))
    @Test fun incompleteRowIsIncluded() = assertEquals(2, actionSheetRowCount(5, 4))
    @Test fun zeroColumnsFallsBackToSingleColumn() = assertEquals(3, actionSheetRowCount(3, 0))
    @Test fun negativeColumnsFallsBackToSingleColumn() = assertEquals(3, actionSheetRowCount(3, -1))
    @Test fun largeCountDoesNotOverflow() = assertEquals(1073741824, actionSheetRowCount(Int.MAX_VALUE, 2))
}
