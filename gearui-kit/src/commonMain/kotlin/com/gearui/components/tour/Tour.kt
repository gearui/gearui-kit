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
import com.gearui.overlay.GearOverlayOptions
import com.gearui.overlay.GearOverlayPlacement
import com.gearui.overlay.LocalGearOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.theme.Theme

/**
 * Tour step data
 */
data class TourStep(
    val title: String,
    val description: String,
    val targetKey: String? = null
)

/**
 * Tour - 漫游式引导组件
 *
 * 基于 GearOverlay 系统实现
 *
 * Features:
 * - 分步引导
 * - 遮罩层
 * - 导航控制
 *
 * Example:
 * ```
 * val tourState = rememberTourState(
 *     steps = listOf(
 *         TourStep(title = "欢迎", description = "这是第一步"),
 *         TourStep(title = "功能介绍", description = "这是第二步")
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
    val controller = LocalGearOverlayController.current
    var overlayId by remember { mutableStateOf<Long?>(null) }

    val currentStep = state.currentStep
    val isVisible = state.isActive && currentStep != null

    LaunchedEffect(isVisible, state.currentIndex) {
        if (isVisible && currentStep != null) {
            // 先关闭旧的
            overlayId?.let { controller.dismiss(it) }

            // 捕获当前值，避免在 lambda 中引用可能变为 null 的 state
            val stepSnapshot = currentStep
            val indexSnapshot = state.currentIndex
            val totalSteps = state.steps.size
            val hasPrevious = state.hasPrevious()
            val hasNext = state.hasNext()

            overlayId = controller.show(
                anchorBounds = null,
                options = GearOverlayOptions(
                    placement = GearOverlayPlacement.Center,
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
                        // 先关闭 overlay，再调用回调
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
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .padding(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    color = colors.textPrimary
                )

                Text(
                    text = "${currentIndex + 1} / $totalSteps",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }

            // Description
            Text(
                text = step.description,
                style = Typography.BodyMedium,
                color = colors.textSecondary
            )

            // Progress indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(totalSteps) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (index <= currentIndex) colors.primary
                                else colors.surfaceVariant
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
                        text = "跳过",
                        onClick = onSkip
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Navigation buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onPrevious != null) {
                        Button(
                            text = "上一步",
                            onClick = onPrevious
                        )
                    }

                    if (onNext != null) {
                        Button(
                            text = "下一步",
                            onClick = onNext
                        )
                    } else {
                        Button(
                            text = "完成",
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
