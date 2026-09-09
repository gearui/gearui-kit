package com.gearui.sample.examples.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.icon.Icons
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.navigation.TabHost
import com.gearui.primitives.Tab
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.items
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * TabHost against the bare `when` it replaces.
 *
 * The toggle is the point: both branches render the same three pages, so the
 * difference measured between them is the cost of destroying and rebuilding
 * native views rather than hiding them.
 *
 * How to measure (MIUI swallows `Log.*`, so frame stats are the only honest
 * signal):
 *
 *     adb shell dumpsys gfxinfo com.gearui.kit.sample reset
 *     # switch tabs fast, ~20 times — slow switching does not reproduce it
 *     adb shell dumpsys gfxinfo com.gearui.kit.sample | grep -E 'Janky|percentile'
 */
@Composable
fun TabHostExample(
    component: ComponentInfo,
    onBack: () -> Unit,
) {
    val colors = Theme.colors
    var selected by remember { mutableStateOf(0) }
    var keepAlive by remember { mutableStateOf(true) }

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "保活 vs 重建",
            description = "同样三页，切换 20 次以上再读 gfxinfo。慢速切换测不出差别。"
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Tab(text = "TabHost 保活", selected = keepAlive, onClick = { keepAlive = true }, modifier = Modifier.weight(1f))
                Tab(text = "when 重建", selected = !keepAlive, onClick = { keepAlive = false }, modifier = Modifier.weight(1f))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                listOf("图标", "列表", "表单").forEachIndexed { i, label ->
                    Tab(text = label, selected = selected == i, onClick = { selected = i }, modifier = Modifier.weight(1f))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .clip(Theme.shapes.lg)
                    .background(colors.surface)
            ) {
                if (keepAlive) {
                    TabHost(selectedIndex = selected, count = 3) { HeavyPage(it) }
                } else {
                    // The control. This is what the bottom nav used to be.
                    when (selected) {
                        0 -> HeavyPage(0)
                        1 -> HeavyPage(1)
                        else -> HeavyPage(2)
                    }
                }
            }
        }
    }
}

/**
 * Deliberately node-heavy: in Kuikly each of these is a real native view, and
 * the count is what a rebuild has to replay across the bridge.
 */
@Composable
private fun HeavyPage(index: Int) {
    val colors = Theme.colors
    when (index) {
        0 -> LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(Icons.all.chunked(4)) { row ->
                Row(modifier = Modifier.fillMaxWidth().padding(Spacing.sm)) {
                    row.forEach { name ->
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Icon(name = name, size = IconSizes.Default.xl, tint = colors.foreground)
                        }
                    }
                }
            }
        }
        1 -> LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items((1..120).toList()) { i ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(name = Icons.user_circle, size = IconSizes.Default.xl, tint = colors.primary)
                    Column(modifier = Modifier.weight(1f).padding(start = Spacing.md)) {
                        Text(text = "条目 $i", style = Theme.typography.bodyMedium, color = colors.foreground)
                        Text(
                            text = "副标题，用来把节点数堆上去",
                            style = Theme.typography.bodySmall,
                            color = colors.mutedForeground,
                        )
                    }
                }
            }
        }
        else -> LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items((1..60).toList()) { i ->
                Column(modifier = Modifier.fillMaxWidth().padding(Spacing.md)) {
                    Text(text = "字段 $i", style = Theme.typography.bodySmall, color = colors.mutedForeground)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .clip(Theme.shapes.md)
                            .background(colors.muted)
                    )
                }
            }
        }
    }
}
