package com.gearui

import com.gearui.foundation.tag.TagSizeTokens
import com.tencent.kuikly.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TagGeometryTest {
    @Test fun nativeChipHeightsAreDistinctFromButtonHeights() {
        assertEquals(listOf(20.dp, 28.dp, 36.dp), listOf(
            TagSizeTokens.Small.height, TagSizeTokens.Medium.height, TagSizeTokens.Large.height
        ))
    }

    @Test fun horizontalInsetsFollowTheSizeProfile() {
        assertEquals(listOf(8.dp, 12.dp, 16.dp), listOf(
            TagSizeTokens.Small.paddingHorizontal,
            TagSizeTokens.Medium.paddingHorizontal,
            TagSizeTokens.Large.paddingHorizontal
        ))
    }

    @Test fun iconBoxesFitAllSizes() {
        for (size in listOf(TagSizeTokens.Small, TagSizeTokens.Medium, TagSizeTokens.Large)) {
            assertTrue(size.iconSize <= size.iconBoxSize)
            assertTrue(size.iconBoxSize < size.height)
        }
    }
}
