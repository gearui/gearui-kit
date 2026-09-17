package com.gearui.components.collapse

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.motion.Motion
import com.gearui.foundation.motion.Motions
import com.gearui.foundation.motion.feedbackDuration
import com.gearui.foundation.material.TokenTransition
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.animation.*
import com.tencent.kuikly.compose.animation.core.*
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.foundation.control.ControlGeometry
import com.tencent.kuikly.compose.ui.unit.IntSize
import kotlin.math.sqrt

internal fun collapseDampingRatio(layout: Boolean): Float {
    val stiffness = if (layout) FeedbackDefaults.accordionLayoutStiffness else FeedbackDefaults.accordionIndicatorStiffness
    val damping = if (layout) FeedbackDefaults.accordionLayoutDamping else FeedbackDefaults.accordionIndicatorDamping
    return damping / (2f * sqrt(stiffness * FeedbackDefaults.accordionMass))
}

internal fun collapseStiffness(motion: Motion, layout: Boolean): Float {
    val stiffness = if (layout) FeedbackDefaults.accordionLayoutStiffness else FeedbackDefaults.accordionIndicatorStiffness
    val speed = Motions.Default.normal.toFloat() / motion.normal.coerceAtLeast(1)
    return stiffness / FeedbackDefaults.accordionMass * speed * speed
}

@Composable
internal fun collapseRotation(expanded: Boolean): Float {
    val motion = Theme.motion
    val rotation by animateFloatAsState(
        if (expanded) -FeedbackDefaults.accordionRotation else 0f,
        if (motion.normal <= 0) snap() else spring(
            dampingRatio = collapseDampingRatio(false), stiffness = collapseStiffness(motion, false)))
    return rotation
}

internal fun collapseFadeSpec(motion: Motion, transition: TokenTransition): TweenSpec<Float> = tween(
    durationMillis = motion.feedbackDuration(transition.durationMillis),
    delayMillis = motion.feedbackDuration(transition.delayMillis),
    easing = transition.easing,
)

@Composable
internal fun CollapseContent(expanded: Boolean, surface: Boolean, content: @Composable () -> Unit) {
    val motion = Theme.motion
    val sizeSpec: FiniteAnimationSpec<IntSize> = if (motion.normal <= 0) snap() else spring(
        dampingRatio = collapseDampingRatio(true), stiffness = collapseStiffness(motion, true))
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(sizeSpec, expandFrom = Alignment.Top) +
            fadeIn(collapseFadeSpec(motion, FeedbackDefaults.accordionEnterTransition)),
        exit = shrinkVertically(sizeSpec, shrinkTowards = Alignment.Top) +
            fadeOut(collapseFadeSpec(motion, FeedbackDefaults.accordionExitTransition))
    ) {
        Box(Modifier.fillMaxWidth().padding(
            start = if (surface) ControlGeometry.accordionSurfacePadding else ControlGeometry.accordionPadding,
            end = if (surface) ControlGeometry.accordionSurfacePadding else ControlGeometry.accordionPadding,
            bottom = ControlGeometry.accordionVerticalPadding)) { content() }
    }
}
