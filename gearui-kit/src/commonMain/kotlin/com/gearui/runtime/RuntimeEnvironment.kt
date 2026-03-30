package com.gearui.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

@Immutable
data class GearSafeArea(
    val top: Dp = 0.dp,
    val bottom: Dp = 0.dp,
    val left: Dp = 0.dp,
    val right: Dp = 0.dp
)

@Immutable
data class GearRuntimeEnvironment(
    val safeArea: GearSafeArea = GearSafeArea()
)

@Immutable
data class GearRuntimeFlags(
    // Spec phase-1: feature-flag gated rollout.
    val unifiedSafeAreaPipeline: Boolean = false,
    // Component safe-area consumption policy (runtime-owned, app-wide).
    val navBarConsumesTopSafeArea: Boolean = false,
    val bottomNavBarConsumesBottomSafeArea: Boolean = true,
    val drawerConsumesVerticalSafeArea: Boolean = true,
    val actionSheetConsumesBottomSafeArea: Boolean = true
)

val LocalGearRuntimeEnvironment = staticCompositionLocalOf { GearRuntimeEnvironment() }
val LocalGearRuntimeFlags = staticCompositionLocalOf { GearRuntimeFlags() }

@Composable
fun ProvideGearRuntimeEnvironment(
    flags: GearRuntimeFlags = GearRuntimeFlags(),
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val safeInsets = configuration.safeAreaInsets
    val baseSafeArea = GearSafeArea(
        top = safeInsets.top.dp,
        bottom = safeInsets.bottom.dp,
        left = safeInsets.left.dp,
        right = safeInsets.right.dp
    )
    val mergedSafeArea = GearRuntimeInsetsBridge.mergeWith(baseSafeArea)
    val environment = GearRuntimeEnvironment(
        safeArea = mergedSafeArea
    )

    CompositionLocalProvider(
        LocalGearRuntimeEnvironment provides environment,
        LocalGearRuntimeFlags provides flags
    ) {
        content()
    }
}
