package com.gearui.components.select

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.gearui.components.icon.Icons
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.motion.Motion
import com.gearui.foundation.motion.Motions
import com.gearui.foundation.primitives.Icon
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.animation.core.animateFloatAsState
import com.tencent.kuikly.compose.animation.core.snap
import com.tencent.kuikly.compose.animation.core.spring
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import kotlin.math.sqrt

// Compose assumes unit mass. Normalize k/m and c/(2*sqrt(k*m)), not raw damping.
internal val selectIndicatorDampingRatio: Float
    get() = FeedbackDefaults.selectIndicatorDamping /
        (2f * sqrt(FeedbackDefaults.selectIndicatorStiffness * FeedbackDefaults.selectIndicatorMass))

internal fun selectIndicatorStiffness(motion: Motion): Float {
    val speed = Motions.Default.normal.toFloat() / motion.normal.coerceAtLeast(1)
    return FeedbackDefaults.selectIndicatorStiffness / FeedbackDefaults.selectIndicatorMass * speed * speed
}

@Composable
internal fun SelectIndicator(expanded: Boolean) {
    val motion = Theme.motion
    val progress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = if (motion.normal <= 0) snap() else spring(
            dampingRatio = selectIndicatorDampingRatio,
            stiffness = selectIndicatorStiffness(motion),
        ),
    )
    Icon(
        name = Icons.caret_down,
        size = FieldDefaults.trailingIconSize,
        tint = Theme.colors.mutedForeground,
        modifier = Modifier.graphicsLayer {
            rotationZ = -FeedbackDefaults.selectIndicatorRotation * progress
        },
    )
}
