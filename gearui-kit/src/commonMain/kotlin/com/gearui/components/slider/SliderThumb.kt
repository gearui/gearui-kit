package com.gearui.components.slider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.motion.Motion
import com.gearui.foundation.motion.Motions
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.animation.core.animateFloatAsState
import com.tencent.kuikly.compose.animation.core.snap
import com.tencent.kuikly.compose.animation.core.spring
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import kotlin.math.sqrt

internal val sliderDampingRatio: Float
    get() = FeedbackDefaults.sliderDamping /
        (2f * sqrt(FeedbackDefaults.sliderStiffness * FeedbackDefaults.sliderMass))

internal fun sliderSpringStiffness(motion: Motion): Float {
    val speed = Motions.Default.normal.toFloat() / motion.normal.coerceAtLeast(1)
    return FeedbackDefaults.sliderStiffness / FeedbackDefaults.sliderMass * speed * speed
}

/** Visual size is independent of the surrounding drag target. */
@Composable
internal fun SliderThumbFace(dragging: Boolean) {
    val colors = Theme.colors
    val shape = Theme.shapes.md
    val motion = Theme.motion
    val scale by animateFloatAsState(if (dragging) FeedbackDefaults.sliderDragScale else 1f,
        if (motion.normal <= 0) snap() else spring(dampingRatio = sliderDampingRatio, stiffness = sliderSpringStiffness(motion)))
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(Modifier.width(ControlGeometry.sliderThumbWidth).height(ControlGeometry.sliderThumbHeight)
            .clip(shape).background(colors.primary).padding(ControlGeometry.sliderThumbInset)) {
            Box(Modifier.fillMaxSize().graphicsLayer { scaleX = scale; scaleY = scale }
                .clip(shape).background(colors.primaryForeground))
        }
    }
}
