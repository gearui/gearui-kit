package com.gearui.sample.examples.navigator

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.gestures.SwipeBackConfig
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.BackHandler
import com.tencent.kuikly.compose.animation.core.Animatable
import com.tencent.kuikly.compose.animation.core.spring
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.tencent.kuikly.compose.ui.input.pointer.PointerInputChange
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.input.pointer.positionChange
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun NavigatorKuiklySpikeExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    ExamplePage(component = component, onBack = onBack) {
        SaveableStateSpikeSection()
        MountTimingSpikeSection()
        BackHandlerLifoSpikeSection()
        OneShotBackHandlerSection()
    }
}

@Composable
private fun SaveableStateSpikeSection() {
    val colors = Theme.colors
    val holder = rememberSaveableStateHolder()
    var activeKey by remember { mutableStateOf("entry-A") }
    var removedA by remember { mutableStateOf(false) }
    var log by remember { mutableStateOf("1. Increment A, switch to B, switch back to A: value should stay.\n2. Switch to B, remove A, switch back to A: value should reset.") }

    ExampleSection(
        title = "SaveableStateHolder + removeState",
        description = "验证 entry 卸载后保留 rememberSaveable；removeState 后清理旧 entry 状态。"
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(text = "Show A", size = ButtonSize.SMALL, onClick = { activeKey = "entry-A" })
            Button(text = "Show B", size = ButtonSize.SMALL, onClick = { activeKey = "entry-B" })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                text = "Remove A",
                size = ButtonSize.SMALL,
                theme = ButtonTheme.DANGER,
                onClick = {
                    if (activeKey == "entry-A") {
                        activeKey = "entry-B"
                        log = "Moved to B before removing active A.\n$log"
                    }
                    holder.removeState("entry-A")
                    removedA = true
                    log = "removeState(entry-A) called. Show A again: count should be 0.\n$log"
                }
            )
            Button(
                text = "Reset Log",
                size = ButtonSize.SMALL,
                theme = ButtonTheme.DEFAULT,
                type = ButtonType.OUTLINE,
                onClick = { log = "Log reset." }
            )
        }

        holder.SaveableStateProvider(activeKey) {
            var count by rememberSaveable { mutableStateOf(0) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Theme.shapes.md)
                    .background(colors.muted)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Active key: $activeKey",
                    style = Typography.BodyMedium,
                    color = colors.foreground
                )
                Text(
                    text = "rememberSaveable count: $count",
                    style = Typography.TitleMedium,
                    color = colors.foreground
                )
                Button(
                    text = "Increment $activeKey",
                    size = ButtonSize.SMALL,
                    onClick = {
                        count += 1
                        log = "$activeKey count incremented to $count.\n$log"
                    }
                )
            }
        }

        val status = if (removedA && activeKey == "entry-A") {
            "Check: entry-A should have reset to 0 after removeState."
        } else {
            "Check: switching A/B without removeState should retain each entry count."
        }
        Text(text = status, style = Typography.BodySmall, color = colors.mutedForeground)
        Text(text = log, style = Typography.BodySmall, color = colors.mutedForeground)
    }
}

private enum class MountTimingMode(val label: String) {
    EdgeDown("A: edge-down pre-mount"),
    Recognized("B: recognized mount")
}

@Composable
private fun MountTimingSpikeSection() {
    val colors = Theme.colors
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var mode by remember { mutableStateOf(MountTimingMode.EdgeDown) }
    var previousMounted by remember { mutableStateOf(false) }
    var widthPx by remember { mutableStateOf(0) }
    var log by remember { mutableStateOf("Swipe inside the probe box from its left side.") }
    var eventTrace by remember { mutableStateOf("No gesture yet") }

    fun appendLog(message: String) {
        eventTrace = if (eventTrace == "No gesture yet") {
            message
        } else {
            "$message | $eventTrace".split(" | ").take(4).joinToString(" | ")
        }
        log = "$message\n$log"
    }

    ExampleSection(
        title = "Previous mount timing A/B",
        description = "A 在 edge down 就挂 previous；B 到 horizontal slop recognized 后才挂 previous。"
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                text = "Mode A",
                size = ButtonSize.SMALL,
                theme = if (mode == MountTimingMode.EdgeDown) ButtonTheme.PRIMARY else ButtonTheme.DEFAULT,
                onClick = {
                    mode = MountTimingMode.EdgeDown
                    appendLog("Mode = edge-down pre-mount")
                }
            )
            Button(
                text = "Mode B",
                size = ButtonSize.SMALL,
                theme = if (mode == MountTimingMode.Recognized) ButtonTheme.PRIMARY else ButtonTheme.DEFAULT,
                onClick = {
                    mode = MountTimingMode.Recognized
                    appendLog("Mode = recognized mount")
                }
            )
        }

        Text(text = "eventTrace=$eventTrace", style = Typography.BodyMedium, color = colors.foreground)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(Theme.shapes.lg)
                .border(1.dp, colors.border, Theme.shapes.lg)
                .background(colors.background)
                .onSizeChanged { widthPx = it.width }
                .navigatorSpikeSwipeProbe(
                    mode = mode,
                    config = SwipeBackConfig(edgeWidthDp = 96f),
                    onEdgeDown = {
                        if (mode == MountTimingMode.EdgeDown) {
                            previousMounted = true
                            appendLog("edge-down: previous mounted before recognition")
                        } else {
                            appendLog("edge-down: previous not mounted yet")
                        }
                    },
                    onRecognized = {
                        if (mode == MountTimingMode.Recognized) {
                            previousMounted = true
                            appendLog("recognized: previous mounted after touch slop")
                        } else {
                            appendLog("recognized: previous already mounted")
                        }
                    },
                    onDrag = { dragX ->
                        scope.launch {
                            offsetX.snapTo(dragX)
                        }
                    },
                    onCancel = {
                        scope.launch {
                            offsetX.animateTo(0f, animationSpec = spring())
                            previousMounted = false
                            appendLog("cancel: spring restore, previous unmounted")
                        }
                    },
                    onCommit = {
                        scope.launch {
                            val target = if (widthPx > 0) widthPx.toFloat() else 480f
                            offsetX.animateTo(target, animationSpec = tween(durationMillis = 180))
                            offsetX.snapTo(0f)
                            previousMounted = false
                            appendLog("commit: tween complete, previous unmounted")
                        }
                    }
                )
        ) {
            if (previousMounted) {
                PreviousLayerCard(mode = mode.label)
            }
            CurrentLayerCard(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { translationX = offsetX.value },
                mode = mode.label,
                offset = offsetX.value
            )
        }

        Text(text = "previousMounted=$previousMounted, dragX=${offsetX.value.toInt()}", style = Typography.BodySmall, color = colors.mutedForeground)
        Text(text = log, style = Typography.BodySmall, color = colors.mutedForeground)
    }
}

private fun Modifier.navigatorSpikeSwipeProbe(
    mode: MountTimingMode,
    config: SwipeBackConfig,
    onEdgeDown: () -> Unit,
    onRecognized: () -> Unit,
    onDrag: (Float) -> Unit,
    onCancel: () -> Unit,
    onCommit: () -> Unit
): Modifier = pointerInput(mode, config) {
    val edgePx = config.edgeWidthDp * density
    val commitPx = config.commitDistanceDp * density

    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        if (down.position.x > edgePx) return@awaitEachGesture
        onEdgeDown()

        val startY = down.position.y
        var totalDx = 0f
        var totalDy: Float
        var recognized = false

        val slopChange: PointerInputChange? = awaitHorizontalTouchSlopOrCancellation(down.id) { change, overSlop ->
            totalDx += overSlop
            totalDy = change.position.y - startY
            if (abs(totalDx) > abs(totalDy) * config.directionRatio && totalDx > 0f) {
                change.consume()
                recognized = true
            }
        }

        if (!recognized || slopChange == null) {
            onCancel()
            return@awaitEachGesture
        }

        onRecognized()
        var dragX = totalDx
        onDrag(dragX)
        var pointer = slopChange.id
        while (true) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull { it.id == pointer } ?: break
            if (!change.pressed) break
            dragX = (dragX + change.positionChange().x).coerceAtLeast(0f)
            change.consume()
            onDrag(dragX)
        }

        if (dragX >= commitPx) {
            onCommit()
        } else {
            onCancel()
        }
    }
}

@Composable
private fun PreviousLayerCard(mode: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF6EF))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Previous layer mounted", style = Typography.TitleMedium, color = Color(0xFF174A2A))
        Text(text = mode, style = Typography.BodyMedium, color = Color(0xFF174A2A))
        Text(text = "This stands in for the examples list / chat list.", style = Typography.BodySmall, color = Color(0xFF38684A))
    }
}

@Composable
private fun CurrentLayerCard(
    modifier: Modifier,
    mode: String,
    offset: Float
) {
    Column(
        modifier = modifier
            .background(Color(0xFFFFFFFF))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Current layer", style = Typography.TitleMedium, color = Color(0xFF111827))
        Text(text = mode, style = Typography.BodyMedium, color = Color(0xFF374151))
        Text(text = "translationX=${offset.toInt()} px", style = Typography.BodySmall, color = Color(0xFF6B7280))
        Spacer(modifier = Modifier.height(60.dp))
        Text(text = "Swipe from the left side of this card.", style = Typography.BodySmall, color = Color(0xFF6B7280))
    }
}

@Composable
private fun BackHandlerLifoSpikeSection() {
    val colors = Theme.colors
    var innerEnabled by remember { mutableStateOf(true) }
    var outerEnabled by remember { mutableStateOf(true) }
    var outerCount by remember { mutableStateOf(0) }
    var innerCount by remember { mutableStateOf(0) }
    var log by remember {
        mutableStateOf(
            "Press Android BACK or tap the iOS Simulate BACK button below.\n" +
                "Expected truth table:\n" +
                "  inner=on,  outer=on  → consumed=YES, inner fires\n" +
                "  inner=off, outer=on  → consumed=YES, outer fires\n" +
                "  inner=off, outer=off → consumed=NO  (let through to native)"
        )
    }

    if (outerEnabled) {
        BackHandler {
            outerCount += 1
            log = "outer handler consumed BACK (#$outerCount).\n$log"
        }
    }
    if (innerEnabled) {
        BackHandler {
            innerCount += 1
            log = "inner handler consumed BACK (#$innerCount).\n$log"
        }
    }

    ExampleSection(
        title = "BackHandler topmost-only",
        description = "验证 dispatchOnBackEvent 只调 callbackList.last()；callback 全部 dispose 后 consumed=false 让出给 native。"
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                text = if (innerEnabled) "Disable Inner" else "Enable Inner",
                size = ButtonSize.SMALL,
                onClick = {
                    innerEnabled = !innerEnabled
                    log = "innerEnabled=$innerEnabled.\n$log"
                }
            )
            Button(
                text = if (outerEnabled) "Disable Outer" else "Enable Outer",
                size = ButtonSize.SMALL,
                onClick = {
                    outerEnabled = !outerEnabled
                    log = "outerEnabled=$outerEnabled.\n$log"
                }
            )
            Button(
                text = "Clear Log",
                size = ButtonSize.SMALL,
                theme = ButtonTheme.DEFAULT,
                type = ButtonType.OUTLINE,
                onClick = { log = "Log reset. Trigger BACK." }
            )
        }
        Text(
            text = "innerEnabled=$innerEnabled, outerEnabled=$outerEnabled, innerCount=$innerCount, outerCount=$outerCount",
            style = Typography.BodyMedium,
            color = colors.foreground
        )
        Text(text = log, style = Typography.BodySmall, color = colors.mutedForeground)
    }
}

/**
 * Scenario D: the callback disposes itself immediately (armed = false triggers recomposition ->
 * DisposableEffect onDispose -> removeCallback). The next BACK must report consumed=false, which is
 * the minimal check that popping to the bottom of the Navigator stack yields to the Kuikly delegator.
 */
@Composable
private fun OneShotBackHandlerSection() {
    val colors = Theme.colors
    var armed by remember { mutableStateOf(true) }
    var fireCount by remember { mutableStateOf(0) }
    var log by remember {
        mutableStateOf(
            "Step 1: 保持 armed=on，触发一次 BACK；期望 consumed=YES，fireCount→1，并自动 disarm。\n" +
                "Step 2: 再次触发 BACK；期望 consumed=NO（list 空，native 拿到 let-through）。"
        )
    }

    if (armed) {
        BackHandler {
            fireCount += 1
            armed = false
            log = "one-shot fired (#$fireCount) and disarmed self. Next BACK should be NOT consumed.\n$log"
        }
    }

    ExampleSection(
        title = "One-shot BackHandler self-dispose",
        description = "验证 callback 内修改 state → recomposition → DisposableEffect dispose → 下次 BACK 让出 native。"
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                text = if (armed) "Disarm" else "Re-arm",
                size = ButtonSize.SMALL,
                onClick = {
                    armed = !armed
                    log = "armed=$armed.\n$log"
                }
            )
            Button(
                text = "Clear Log",
                size = ButtonSize.SMALL,
                theme = ButtonTheme.DEFAULT,
                type = ButtonType.OUTLINE,
                onClick = { log = "Log reset. Trigger BACK." }
            )
        }
        Text(
            text = "armed=$armed, fireCount=$fireCount",
            style = Typography.BodyMedium,
            color = colors.foreground
        )
        Text(text = log, style = Typography.BodySmall, color = colors.mutedForeground)
    }
}
