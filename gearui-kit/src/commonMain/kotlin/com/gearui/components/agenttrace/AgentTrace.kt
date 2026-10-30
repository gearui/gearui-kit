package com.gearui.components.agenttrace

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.foundation.disclosure.DisclosureBody
import com.gearui.foundation.disclosure.DisclosureHeader
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.rail.RailRow
import com.gearui.foundation.status.ActivityStatus
import com.gearui.foundation.status.StatusGlyph
import com.gearui.foundation.status.isFinished
import com.gearui.foundation.status.of
import com.gearui.i18n.ActivityStrings
import com.gearui.i18n.I18n
import com.gearui.i18n.formatArgs
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.text.font.FontWeight

/**
 * One step an agent takes. [id] keys it, so a step keeps its place and state while the
 * list grows during a run. [detail] is any content under the step — a ToolCallCard, a
 * CodeBlock.
 */
class AgentStep(
    val id: String,
    val title: String,
    val status: ActivityStatus,
    val description: String? = null,
    val detail: (@Composable () -> Unit)? = null,
)

/**
 * The steps of an agent's run, each with its status on a rail. While it runs the list is
 * open, and only the running step animates. When every step is over and
 * [collapseWhenDone] is set, it closes into one line — the overall outcome and "5 steps"
 * — that opens the list again.
 */
@Composable
fun AgentTrace(
    steps: List<AgentStep>,
    modifier: Modifier = Modifier,
    title: String? = null,
    collapseWhenDone: Boolean = true,
) {
    val colors = Theme.colors
    val strings = I18n.activity
    val done = steps.isNotEmpty() && steps.all { it.status.isFinished }
    var open by remember(done) { mutableStateOf(!(done && collapseWhenDone)) }
    val outcome = traceOutcome(steps)
    val summary = traceSummary(strings, title, steps.size)
    Column(modifier.fillMaxWidth()) {
        DisclosureHeader(
            expanded = open,
            onToggle = { open = !open },
            label = "$summary, ${strings.of(outcome)}",
            padding = PaddingValues(vertical = Spacing.xs),
            gap = Spacing.sm,
        ) {
            StatusGlyph(outcome)
            Text(summary, style = Theme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = colors.foreground, modifier = Modifier.weight(1f))
        }
        DisclosureBody(expanded = open, padding = PaddingValues(top = Spacing.xs)) {
            Column {
                steps.forEachIndexed { index, step ->
                    key(step.id) {
                        RailRow(isLast = index == steps.lastIndex, marker = { StatusGlyph(step.status) }) {
                            Text(step.title, style = Theme.typography.bodySmall,
                                color = if (step.status == ActivityStatus.Pending) colors.mutedForeground else colors.foreground)
                            step.description?.let {
                                Text(it, style = Theme.typography.bodyExtraSmall, color = colors.mutedForeground)
                            }
                            step.detail?.invoke()
                        }
                    }
                }
            }
        }
    }
}

/** The run as one status: running while any step runs, failed if any failed, else done. */
internal fun traceOutcome(steps: List<AgentStep>): ActivityStatus = when {
    steps.isEmpty() -> ActivityStatus.Pending
    steps.any { it.status == ActivityStatus.Running } -> ActivityStatus.Running
    steps.any { it.status == ActivityStatus.Failed } -> ActivityStatus.Failed
    steps.all { it.status == ActivityStatus.Cancelled } -> ActivityStatus.Cancelled
    steps.all { it.status.isFinished } -> ActivityStatus.Succeeded
    steps.any { it.status != ActivityStatus.Pending } -> ActivityStatus.Running
    else -> ActivityStatus.Pending
}

/** "Plan the change · 5 steps", or just "5 steps" without a title. */
internal fun traceSummary(strings: ActivityStrings, title: String?, count: Int): String {
    val steps = strings.stepsSummaryFormat.formatArgs("count" to count)
    return if (title.isNullOrBlank()) steps else "$title · $steps"
}
