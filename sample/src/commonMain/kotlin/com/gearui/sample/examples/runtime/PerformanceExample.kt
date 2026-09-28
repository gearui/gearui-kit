package com.gearui.sample.examples.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.cell.Cell
import com.gearui.components.icon.Icons
import com.gearui.components.switch.Switch
import com.gearui.components.tag.Tag
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.List
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.LocalSettingsState
import com.gearui.sample.pages.ThemeStyle
import com.gearui.sample.perf.StartupMark
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.TimeSource

/**
 * The performance budgets, measured on the kit's own components.
 *
 * Two benchmarks live here; cold start is measured from outside the app
 * (`scripts/perf/android_perf.sh`, `scripts/perf/ios_perf.sh`).
 *
 * **Theme switch** — the whole app flipped between light and dark twenty times
 * through the sample's own setting, with about two hundred kit components on this
 * page. Each sample runs from the
 * state change to the start of the *second* frame after it: the first frame is the
 * one that recomposes, the second starts only once that one — recomposition,
 * layout and the native view updates it queued — is done. Results are drawn on the
 * page, not logged, because MIUI swallows `Log.*`; the scripts read them from the
 * accessibility tree.
 *
 * **Scroll** — a thousand-row kit `List` of real `Cell`s (icon, title, description,
 * arrow, a `Switch` every tenth row). The scripts fling it and read
 * `dumpsys gfxinfo` on Android.
 *
 * Budgets: theme switch ≤ 120 ms, janky frames < 3 %.
 */
@Composable
fun PerformanceExample(
    component: ComponentInfo,
    onBack: () -> Unit,
) {
    val colors = Theme.colors
    val settings = LocalSettingsState.current
    var running by remember { mutableStateOf(false) }
    val samples = remember { mutableStateListOf<Double>() }
    var recording by remember { mutableStateOf(false) }
    var frameSummary by remember { mutableStateOf("PERF frames idle") }
    val scope = rememberCoroutineScope()

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "冷启动",
            description = "从进程启动到首页内容第一帧画完。每个进程只记一次，冷启动后进入本页读取。预算 ≤ 1200 ms（Android）/ 1000 ms（iOS）。",
        ) {
            Text(
                text = StartupMark.contentMillis?.let { "PERF startup page=${StartupMark.pageMillis} content=$it" }
                    ?: "PERF startup n/a",
                style = Theme.typography.bodyMedium,
                color = colors.foreground,
            )
        }

        ExampleSection(
            title = "主题切换",
            description = "整个 App 深浅色连切 20 次（本页约 200 个组件）；每次计到状态改变后的第二帧开始。预算 ≤ 120 ms。",
        ) {
            Button(
                text = if (running) "测量中…" else "运行主题切换测试",
                size = ButtonSize.MEDIUM,
                disabled = running,
                onClick = {
                    scope.launch {
                        running = true
                        samples.clear()
                        val original = settings.themeStyle
                        var dark = original == ThemeStyle.DARK
                        repeat(THEME_RUNS) {
                            withFrameNanos { }               // start on a frame boundary
                            val mark = TimeSource.Monotonic.markNow()
                            dark = !dark
                            settings.themeStyle = if (dark) ThemeStyle.DARK else ThemeStyle.LIGHT
                            withFrameNanos { }               // the frame that recomposes
                            withFrameNanos { }               // starts once that frame is done
                            samples += mark.elapsedNow().inWholeMicroseconds / 1000.0
                            delay(40)
                        }
                        settings.themeStyle = original
                        running = false
                    }
                },
            )
            // The result line has a fixed prefix so the scripts can find it in the AX tree.
            Text(
                text = themeSummary(samples, running),
                style = Theme.typography.bodyMedium,
                color = colors.foreground,
            )
            // The whole app re-themes, as it does for a user: the flip goes through the
            // sample's own setting, not a local Theme around this subtree.
            ThemeWorkload()
        }

        ExampleSection(
            title = "长列表滚动",
            description = "1000 行 List + Cell，每 10 行一个开关。录制 5 秒期间快速滑动。Android 以 gfxinfo 为准；应用内帧间隔用于两端对照。预算：掉帧 < 3%。",
        ) {
            Button(
                text = if (recording) "录制中…" else "录制 5 秒帧间隔",
                size = ButtonSize.MEDIUM,
                disabled = recording,
                onClick = {
                    scope.launch {
                        recording = true
                        frameSummary = "PERF frames recording"
                        val intervals = ArrayList<Double>()
                        var last = -1L
                        val end = TimeSource.Monotonic.markNow()
                        while (end.elapsedNow().inWholeMilliseconds < RECORD_MS) {
                            withFrameNanos { now ->
                                if (last >= 0) intervals += (now - last) / 1_000_000.0
                                last = now
                            }
                        }
                        frameSummary = frameReport(intervals)
                        recording = false
                    }
                },
            )
            Text(text = frameSummary, style = Theme.typography.bodyMedium, color = colors.foreground)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(560.dp)
                    .clip(Theme.shapes.lg)
                    .background(colors.surface),
            ) {
                List(modifier = Modifier.fillMaxWidth().height(560.dp)) {
                    items(count = SCROLL_ROWS, key = { it }) { i -> ScrollRow(i) }
                }
            }
        }
    }
}

private const val THEME_RUNS = 20
private const val SCROLL_ROWS = 1000
private const val RECORD_MS = 5_000L

/**
 * "PERF frames n=… period=… p50=… p95=… janky=…%". The frame period is the median
 * interval, so the same rule holds at 60 and 120 Hz; a frame counts as janky when it
 * took more than one and a half periods, which is a visibly dropped frame.
 */
private fun frameReport(intervals: List<Double>): String {
    if (intervals.size < 10) return "PERF frames n=${intervals.size} too few"
    val sorted = intervals.sorted()
    fun pct(p: Double) = sorted[((sorted.size - 1) * p).toInt()]
    fun ms(v: Double) = (v * 10).toLong() / 10.0
    val period = pct(0.5)
    val janky = intervals.count { it > period * 1.5 }
    val jankPct = (janky * 1000L / intervals.size) / 10.0
    return "PERF frames n=${intervals.size} period=${ms(period)} p95=${ms(pct(0.95))} max=${ms(sorted.last())} janky=$jankPct%"
}

/** "PERF theme n=20 median=… p90=… max=…" — the perf scripts parse this line. */
private fun themeSummary(samples: List<Double>, running: Boolean): String {
    if (samples.isEmpty()) return if (running) "PERF theme running" else "PERF theme idle"
    val sorted = samples.sorted()
    fun pct(p: Double) = sorted[((sorted.size - 1) * p).toInt()]
    fun ms(v: Double) = (v * 10).toLong() / 10.0
    return "PERF theme n=${samples.size} median=${ms(pct(0.5))} p90=${ms(pct(0.9))} max=${ms(sorted.last())}" +
        if (running) " …" else ""
}

/** About two hundred kit components, all of which read the theme. */
@Composable
private fun ThemeWorkload() {
    val colors = Theme.colors
    Column(
        modifier = Modifier.fillMaxWidth().clip(Theme.shapes.lg).background(colors.background),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        repeat(40) { i ->
            Cell(
                title = "设置项 $i",
                description = "说明文字",
                arrow = true,
                leading = { Icon(name = Icons.gear, size = IconSizes.Default.md, tint = colors.primary) },
            )
        }
        repeat(20) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Button(text = "按钮", size = ButtonSize.SMALL, onClick = {})
                Tag(text = "标签 $row")
                Switch(checked = row % 2 == 0, onCheckedChange = {})
            }
        }
    }
}

@Composable
private fun ScrollRow(i: Int) {
    val colors = Theme.colors
    var on by remember { mutableStateOf(i % 20 == 0) }
    Cell(
        title = "联系人 $i",
        description = "最近一条消息的预览，用来占住第二行",
        arrow = i % 10 != 0,
        onClick = {},
        leading = { Icon(name = Icons.user_circle, size = IconSizes.Default.xl, tint = colors.primary) },
        trailing = if (i % 10 == 0) {
            { Switch(checked = on, onCheckedChange = { on = it }) }
        } else null,
    )
}
