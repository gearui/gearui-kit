package com.gearui.foundation.swipecell

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.layout.Spacing

/**
 * SwipeCellTokens - design tokens for SwipeCell
 *
 * Reference: internal component spec swipe_cell/td_swipe_cell_style.dart
 *
 * Design rules:
 * - every size and gap comes from a semantic token
 * - colours come from Theme.colors and are not defined here
 * - several size profiles are supported
 */
data class SwipeCellTokens(
    /** Action button width - base width of one flex unit */
    val actionWidth: Dp,

    /** Minimum action button width */
    val actionMinWidth: Dp,

    /** Action button horizontal padding */
    val actionPaddingHorizontal: Dp,

    /** Action button vertical padding */
    val actionPaddingVertical: Dp,

    /** Gap between icon and text */
    val iconSpacing: Dp,

    /** Open threshold ratio (0-1); dragging past this opens automatically */
    val openThreshold: Float,

    /** Fling velocity threshold (pixels/second) */
    val velocityThreshold: Float,

    /** Damping factor - resistance past the bounds */
    val dampingRatio: Float,

    /** Spring animation damping ratio */
    val springDampingRatio: Float,

    /** Spring animation stiffness */
    val springStiffness: Float
)

/**
 * Default SwipeCell token profiles
 */
object SwipeCellDefaults {

    /**
     * Standard - fits most cases
     *
     * Action button width 80dp, enough for 2 CJK characters plus padding
     */
    val Default = SwipeCellTokens(
        actionWidth = 80.dp,
        actionMinWidth = 64.dp,
        actionPaddingHorizontal = Spacing.md,  // 12.dp
        actionPaddingVertical = Spacing.sm,    // 8.dp
        iconSpacing = Spacing.xs,              // 4.dp
        openThreshold = 0.4f,
        velocityThreshold = 500f,
        dampingRatio = 0.3f,
        springDampingRatio = 0.7f,             // DampingRatioMediumBouncy
        springStiffness = 400f                 // StiffnessMedium
    )

    /**
     * Compact - for rows with several action buttons
     *
     * Action button width 64dp, suited to icon-only or short labels
     */
    val Compact = SwipeCellTokens(
        actionWidth = 64.dp,
        actionMinWidth = 56.dp,
        actionPaddingHorizontal = Spacing.sm,  // 8.dp
        actionPaddingVertical = Spacing.xs,    // 4.dp
        iconSpacing = Spacing.xs,              // 4.dp
        openThreshold = 0.35f,
        velocityThreshold = 500f,
        dampingRatio = 0.3f,
        springDampingRatio = 0.7f,
        springStiffness = 400f
    )

    /**
     * Loose - for a single button or longer text
     *
     * Action button width 96dp, enough for 3-4 CJK characters
     */
    val Large = SwipeCellTokens(
        actionWidth = 96.dp,
        actionMinWidth = 80.dp,
        actionPaddingHorizontal = Spacing.lg,  // 16.dp
        actionPaddingVertical = Spacing.md,    // 12.dp
        iconSpacing = Spacing.sm,              // 8.dp
        openThreshold = 0.4f,
        velocityThreshold = 500f,
        dampingRatio = 0.3f,
        springDampingRatio = 0.7f,
        springStiffness = 400f
    )
}
