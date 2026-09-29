package com.gearui.sample.examples.indexbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.gearui.components.avatar.Avatar
import com.gearui.components.cell.Cell
import com.gearui.components.indexbar.IndexBar
import com.gearui.components.navbar.NavBar
import com.gearui.components.scaffold.PageScaffold
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.GearLazyColumn
import com.gearui.foundation.primitives.Text
import com.gearui.runtime.LocalRuntimeEnvironment
import com.gearui.sample.config.ComponentInfo
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import kotlinx.coroutines.launch

/**
 * Contacts with their pinyin initials. A real app derives the initial with a pinyin
 * library and settles surnames with more than one reading (Zeng, Shan) itself.
 */
private val Contacts = listOf(
    "A" to "安然", "A" to "敖丽", "B" to "白雪", "B" to "包明", "C" to "陈晨", "C" to "曹操",
    "C" to "程远", "D" to "邓超", "D" to "丁一", "F" to "方舟", "F" to "冯凯", "G" to "高峰",
    "G" to "郭靖", "H" to "韩梅", "H" to "黄蓉", "J" to "江南", "J" to "金鑫", "L" to "李雷",
    "L" to "林夕", "L" to "刘洋", "M" to "马良", "M" to "孟浩", "P" to "潘安", "Q" to "钱多",
    "Q" to "秦岚", "S" to "孙悟", "S" to "单田芳", "T" to "唐僧", "W" to "王五", "W" to "吴用",
    "X" to "徐凤", "X" to "谢安", "Y" to "杨过", "Y" to "叶孤", "Z" to "张三", "Z" to "曾巩",
    "Z" to "赵敏", "#" to "123 客服",
)

/**
 * The contact list the index bar belongs to: letter headers, the bar down the edge, a
 * tap or a drag jumps to the section, and the bar follows the list as it scrolls.
 */
@Composable
fun IndexBarExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val safeBottom = LocalRuntimeEnvironment.current.safeArea.bottom

    val sections = remember { Contacts.groupBy({ it.first }, { it.second }).toList() }
    val letters = remember { sections.map { it.first } }
    // List position of each section header: one header item plus its rows per section.
    val headerIndex = remember {
        var index = 0
        sections.associate { (letter, names) -> letter to index.also { index += 1 + names.size } }
    }
    val active by remember {
        derivedStateOf {
            val first = listState.firstVisibleItemIndex
            headerIndex.entries.lastOrNull { it.value <= first }?.key
        }
    }

    PageScaffold(
        backgroundColor = colors.background,
        topSafeAreaColor = colors.surface,
        consumeBottomSafeArea = false,
    ) {
        Column(Modifier.fillMaxSize().background(colors.background)) {
            NavBar(
                title = component.nameEn,
                centerTitle = true,
                useDefaultBack = true,
                onBackClick = onBack,
                backgroundColor = colors.surface,
            )
            Box(Modifier.fillMaxSize()) {
                GearLazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = safeBottom),
                ) {
                    sections.forEach { (letter, names) ->
                        item(key = "header-$letter") {
                            Text(
                                text = letter,
                                style = Theme.typography.bodySmall,
                                color = colors.mutedForeground,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Spacing.lg, vertical = Spacing.xs),
                            )
                        }
                        names.forEach { name ->
                            item(key = "$letter-$name") {
                                Cell(title = name, leading = { Avatar(fallback = name.take(1)) })
                            }
                        }
                    }
                }
                IndexBar(
                    indexes = letters,
                    active = active,
                    onSelect = { letter ->
                        headerIndex[letter]?.let { scope.launch { listState.scrollToItem(it) } }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd),
                )
            }
        }
    }
}
