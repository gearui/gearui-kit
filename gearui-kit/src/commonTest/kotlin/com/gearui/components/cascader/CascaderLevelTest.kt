package com.gearui.components.cascader

import kotlin.test.Test
import kotlin.test.assertEquals

class CascaderLevelTest {
    private val tree = listOf(
        CascaderOption("44", "广东省", children = listOf(
            CascaderOption("4401", "广州市", children = listOf(CascaderOption("440106", "天河区"))),
            CascaderOption("4403", "深圳市", isLeaf = false),
        )),
        CascaderOption("81", "香港特别行政区"),
    )

    @Test
    fun givenChildrenAreUsedDirectly() {
        val level = cascaderLevel(tree, listOf("44", "4401"), 2) { null }
        assertEquals(Level.Options(listOf(CascaderOption("440106", "天河区"))), level)
    }

    @Test
    fun aLeafHasNoNextLevel() {
        assertEquals(Level.None, cascaderLevel(tree, listOf("81"), 1) { null })
    }

    @Test
    fun aLazyNodeReportsItsLoadState() {
        val path = listOf("44", "4403")
        assertEquals(Level.None, cascaderLevel(tree, path, 2) { null })
        assertEquals(Level.Loading, cascaderLevel(tree, path, 2) { LevelState.Loading })
        assertEquals(Level.Failed, cascaderLevel(tree, path, 2) { LevelState.Failed })
        val districts = listOf(CascaderOption("440305", "南山区"))
        assertEquals(Level.Options(districts), cascaderLevel(tree, path, 2) { LevelState.Loaded(districts) })
    }

    @Test
    fun sameNamedNodesAreTellByValue() {
        val twins = listOf(CascaderOption("a", "朝阳区"), CascaderOption("b", "朝阳区"))
        val labels = cascaderLabels(listOf(CascaderOption("root", "市", children = twins)), listOf("root", "b")) { null }
        assertEquals(listOf("市", "朝阳区"), labels)
    }

    @Test
    fun labelsFollowLoadedLevels() {
        val districts = listOf(CascaderOption("440305", "南山区"))
        val labels = cascaderLabels(tree, listOf("44", "4403", "440305")) { key ->
            if (key == pathKey(listOf("44", "4403"))) districts else null
        }
        assertEquals(listOf("广东省", "深圳市", "南山区"), labels)
    }

    @Test
    fun anUnknownValueShowsItself() {
        assertEquals(listOf("广东省", "9999"), cascaderLabels(tree, listOf("44", "9999")) { null })
    }
}
