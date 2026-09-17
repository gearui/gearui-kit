package com.gearui.foundation.interaction

import com.gearui.foundation.motion.FeedbackDefaults
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer

/**
 * Shared disabled appearance for custom controls. Apply once to each visual
 * subtree; this does not disable input, callbacks or accessibility actions.
 * Read-only content should not use this unless it is also explicitly disabled.
 */
fun Modifier.disabledAppearance(disabled: Boolean): Modifier = graphicsLayer {
    alpha = if (disabled) FeedbackDefaults.disabledOpacity else 1f
}
