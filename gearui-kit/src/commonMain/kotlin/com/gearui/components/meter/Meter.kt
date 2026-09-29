package com.gearui.components.meter

import androidx.compose.runtime.Composable
import com.gearui.components.progress.LinearProgress
import com.gearui.components.progress.ProgressStatus
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.semantics.*

/** A scalar measurement in a known range. Unlike progress it has no loading or completion lifecycle. */
@Composable
fun Meter(value: Float, label: String, modifier: Modifier = Modifier, min: Float = 0f, max: Float = 100f,
    status: ProgressStatus = ProgressStatus.PRIMARY, format: (Float) -> String = { it.toString() }) {
    val fraction = meterFraction(value, min, max)
    Column(modifier.semantics(mergeDescendants = true) {
        contentDescription = label + ", " + format(value.coerceIn(min, max))
        progressBarRangeInfo = ProgressBarRangeInfo(value.coerceIn(min, max), min..max)
    }, verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(label, style = Theme.typography.bodyMedium, color = Theme.colors.foreground)
        LinearProgress(fraction, status = status, showLabel = false)
        Text(format(value.coerceIn(min, max)), style = Theme.typography.bodySmall, color = Theme.colors.mutedForeground)
    }
}
internal fun meterFraction(value: Float, min: Float, max: Float): Float {
    require(value.isFinite() && min.isFinite() && max.isFinite() && min < max)
    return ((value.toDouble() - min.toDouble()) / (max.toDouble() - min.toDouble())).toFloat().coerceIn(0f, 1f)
}
