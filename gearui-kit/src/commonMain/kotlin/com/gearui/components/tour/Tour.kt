package com.gearui.components.tour

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.LocalOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.theme.Theme
import com.gearui.i18n.I18n
import com.gearui.foundation.elevation.Elevation
import com.gearui.overlay.OverlayDefaults
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth

/**
 * Tour step data
 */
data class TourStep(
    val title: String,
    val description: String,
    val targetKey: String? = null
)

/**
 * Tour - guided walkthrough
 *
 * Built on the Overlay system
 *
 * Features:
 * - step by step guidance
 * - a scrim
 * - navigation controls
 *
 * Example:
 * ```
 * val tourState = rememberTourState(
 *     steps = listOf(
 *         TourStep(title = "Welcome", description = "This is step one"),
 *         TourStep(title = "Features", description = "This is step two")
 *     )
 * )
 *
 * Tour(
 *     state = tourState,
 *     onFinish = { tourState.finish() }
 * )
 * ```
 */
@Composable
fun Tour(
    state: TourState,
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {},
    onSkip: (() -> Unit)? = null
) {
    val colors = Theme.colors
    val controller = LocalOverlayController.current
    var overlayId by remember { mutableStateOf<Long?>(null) }

    val currentStep = state.currentStep
    val isVisible = state.isActive && currentStep != null

    LaunchedEffect(isVisible, state.currentIndex) {
        if (isVisible) {
            // Dismiss the previous one first
            overlayId?.let { controller.dismiss(it) }

            // Capture the current value; the state referenced in the lambda may become null
            val stepSnapshot = currentStep
            val indexSnapshot = state.currentIndex
            val totalSteps = state.steps.size
            val hasPrevious = state.hasPrevious()
            val hasNext = state.hasNext()

            overlayId = controller.show(
                anchorBounds = null,
                options = OverlayOptions(
                    placement = OverlayPlacement.Center,
                    modal = true,
                    maskColor = Color.Black.copy(alpha = 0.5f),
                    dismissPolicy = OverlayDismissPolicy.Manual
                )
            ) {
                TourContent(
                    step = stepSnapshot,
                    currentIndex = indexSnapshot,
                    totalSteps = totalSteps,
                    onPrevious = if (hasPrevious) ({ state.previous() }) else null,
                    onNext = if (hasNext) ({ state.next() }) else null,
                    onFinish = {
                        // Dismiss the overlay first, then fire the callback
                        overlayId?.let { controller.dismiss(it) }
                        overlayId = null
                        state.finish()
                        onFinish()
                    },
                    onSkip = onSkip?.let { skip ->
                        {
                            overlayId?.let { controller.dismiss(it) }
                            overlayId = null
                            state.finish()
                            skip()
                        }
                    }
                )
            }
        } else {
            overlayId?.let { controller.dismiss(it) }
            overlayId = null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            overlayId?.let { controller.dismiss(it) }
        }
    }
}

@Composable
private fun TourContent(
    step: TourStep,
    currentIndex: Int,
    totalSteps: Int,
    onPrevious: (() -> Unit)?,
    onNext: (() -> Unit)?,
    onFinish: () -> Unit,
    onSkip: (() -> Unit)?
) {
    val colors = Theme.colors

    Box(
        modifier = Modifier
            .width(400.dp)
            .shadow(Elevation.modal, OverlayDefaults.modalShape)
            .clip(OverlayDefaults.modalShape)
            .background(colors.surface)
            .padding(Spacing.xl)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = step.title,
                    style = Typography.TitleLarge,
                    color = colors.foreground
                )

                Text(
                    text = "${currentIndex + 1} / $totalSteps",
                    style = Typography.BodySmall,
                    color = colors.mutedForeground
                )
            }

            // Description
            Text(
                text = step.description,
                style = Typography.BodyMedium,
                color = colors.mutedForeground
            )

            // Progress indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                repeat(totalSteps) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(Theme.shapes.sm)
                            .background(
                                if (index <= currentIndex) colors.primary
                                else colors.muted
                            )
                    )
                }
            }

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Skip button
                if (onSkip != null) {
                    Button(
                        text = I18n.strings.guide.tourSkip,
                        onClick = onSkip
                    )
                } else {
                    Spacer(modifier = Modifier.width(BorderWidth.thin))
                }

                // Navigation buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    if (onPrevious != null) {
                        Button(
                            text = I18n.strings.guide.tourPrevious,
                            onClick = onPrevious
                        )
                    }

                    if (onNext != null) {
                        Button(
                            text = I18n.strings.guide.tourNext,
                            onClick = onNext
                        )
                    } else {
                        Button(
                            text = I18n.strings.guide.tourFinish,
                            onClick = onFinish
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tour state manager
 */
class TourState(
    val steps: List<TourStep>
) {
    var isActive by mutableStateOf(false)
        private set

    var currentIndex by mutableStateOf(0)
        private set

    val currentStep: TourStep?
        get() = if (isActive && currentIndex < steps.size) steps[currentIndex] else null

    fun start() {
        if (steps.isNotEmpty()) {
            isActive = true
            currentIndex = 0
        }
    }

    fun next() {
        if (currentIndex < steps.size - 1) {
            currentIndex++
        }
    }

    fun previous() {
        if (currentIndex > 0) {
            currentIndex--
        }
    }

    fun finish() {
        isActive = false
        currentIndex = 0
    }

    fun hasNext(): Boolean = currentIndex < steps.size - 1

    fun hasPrevious(): Boolean = currentIndex > 0
}

@Composable
fun rememberTourState(steps: List<TourStep>): TourState {
    return remember(steps) { TourState(steps) }
}
