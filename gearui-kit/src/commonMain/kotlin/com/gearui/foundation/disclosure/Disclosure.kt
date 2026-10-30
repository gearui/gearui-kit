package com.gearui.foundation.disclosure

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.animation.*
import com.tencent.kuikly.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.gearui.components.icon.*
import com.gearui.foundation.interaction.controlSemantics
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.motion.Motion
import com.gearui.foundation.motion.Motions
import com.gearui.foundation.material.TokenTransition
import com.gearui.foundation.motion.feedbackDuration
import com.gearui.foundation.motion.rowPressFeedback
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.typography.IconSizes
import com.gearui.i18n.I18n
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.RowScope
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.heightIn
import com.gearui.foundation.control.ControlGeometry
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.rotate
import com.tencent.kuikly.compose.ui.graphics.RectangleShape
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.graphics.Color
import com.gearui.foundation.layout.Spacing
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.IntSize
import kotlin.math.sqrt

// A disclosure: a header row that opens and closes the body under it. Shared by Collapse,
// Reasoning, ToolCallCard, AgentTrace and CodeBlock, so they open with one motion, one
// press feedback and one way of announcing the state.

/**
 * The header row of a disclosure: the whole row toggles, the chevron turns with the
 * state. With a [label] a screen reader hears "label, expanded/collapsed" and the row's own
 * text is not read twice; without one (a caller's composable header) the row keeps its
 * children's semantics.
 */
@Composable
internal fun DisclosureHeader(
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    padding: PaddingValues,
    gap: Dp,
    shape: Shape = RectangleShape,
    chevronTint: Color = Theme.colors.mutedForeground,
    trailingGap: Dp = Spacing.xs,
    /** Text before the chevron ("3 steps"); hidden from a screen reader when [label] is set. */
    trailing: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val actions = I18n.actions
    val interaction = remember { MutableInteractionSource() }
    val rotation = disclosureRotation(expanded)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = ControlGeometry.selectionTouchTarget)
            .then(
                if (label != null) Modifier.controlSemantics(
                    label, if (expanded) actions.expanded else actions.collapsed, Role.Button,
                    if (enabled) onToggle else null,
                ) else Modifier
            )
            .rowPressFeedback(interaction = interaction, shape = shape, enabled = enabled, scale = false)
            .clickable(enabled = enabled, interactionSource = interaction, indication = null, onClick = onToggle)
            .padding(padding),
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(trailingGap)) {
            trailing?.invoke(this)
            Icon(
                Icons.caretDown,
                size = IconSizes.Default.md,
                tint = chevronTint,
                modifier = Modifier.rotate(rotation),
            )
        }
    }
}

/**
 * The body of a disclosure. While closed it is not in the composition at all: a long
 * reasoning text or tool output costs nothing until it is opened.
 */
@Composable
internal fun DisclosureBody(
    expanded: Boolean,
    padding: PaddingValues,
    content: @Composable () -> Unit,
) {
    val motion = Theme.motion
    val sizeSpec: FiniteAnimationSpec<IntSize> = if (motion.normal <= 0) snap() else spring(
        dampingRatio = disclosureDampingRatio(true), stiffness = disclosureStiffness(motion, true))
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(sizeSpec, expandFrom = Alignment.Top) +
            fadeIn(disclosureFadeSpec(motion, FeedbackDefaults.accordionEnterTransition)),
        exit = shrinkVertically(sizeSpec, shrinkTowards = Alignment.Top) +
            fadeOut(disclosureFadeSpec(motion, FeedbackDefaults.accordionExitTransition)),
    ) {
        Box(Modifier.fillMaxWidth().padding(padding)) { content() }
    }
}

internal fun disclosureDampingRatio(layout: Boolean): Float {
    val stiffness = if (layout) FeedbackDefaults.accordionLayoutStiffness else FeedbackDefaults.accordionIndicatorStiffness
    val damping = if (layout) FeedbackDefaults.accordionLayoutDamping else FeedbackDefaults.accordionIndicatorDamping
    return damping / (2f * sqrt(stiffness * FeedbackDefaults.accordionMass))
}

internal fun disclosureStiffness(motion: Motion, layout: Boolean): Float {
    val stiffness = if (layout) FeedbackDefaults.accordionLayoutStiffness else FeedbackDefaults.accordionIndicatorStiffness
    val speed = Motions.Default.normal.toFloat() / motion.normal.coerceAtLeast(1)
    return stiffness / FeedbackDefaults.accordionMass * speed * speed
}

@Composable
internal fun disclosureRotation(expanded: Boolean): Float {
    val motion = Theme.motion
    val rotation by animateFloatAsState(
        if (expanded) -FeedbackDefaults.accordionRotation else 0f,
        if (motion.normal <= 0) snap() else spring(
            dampingRatio = disclosureDampingRatio(false), stiffness = disclosureStiffness(motion, false)))
    return rotation
}

internal fun disclosureFadeSpec(motion: Motion, transition: TokenTransition): TweenSpec<Float> = tween(
    durationMillis = motion.feedbackDuration(transition.durationMillis),
    delayMillis = motion.feedbackDuration(transition.delayMillis),
    easing = transition.easing,
)
