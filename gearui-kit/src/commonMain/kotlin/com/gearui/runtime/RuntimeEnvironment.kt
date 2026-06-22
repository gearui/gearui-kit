package com.gearui.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

@Immutable
data class SafeArea(
    val top: Dp = 0.dp,
    val bottom: Dp = 0.dp,
    val left: Dp = 0.dp,
    val right: Dp = 0.dp
)

@Immutable
data class RuntimeEnvironment(
    /**
     * 稳定后的安全区（去抖 + 粘滞 + host override 合并）。**页面/组件布局只用这个**。
     */
    val safeArea: SafeArea = SafeArea(),
    /**
     * 运行时/host 原始安全区，仅用于诊断；可能偶发回 0 或不完整，不要直接用于布局。
     */
    val rawSafeArea: SafeArea = SafeArea()
)

@Immutable
data class RuntimeFlags(
    // Spec phase-1: feature-flag gated rollout.
    val unifiedSafeAreaPipeline: Boolean = false,
    // Component safe-area consumption policy (runtime-owned, app-wide).
    val navBarConsumesTopSafeArea: Boolean = false,
    val bottomNavBarConsumesBottomSafeArea: Boolean = true,
    val drawerConsumesVerticalSafeArea: Boolean = true,
    val actionSheetConsumesBottomSafeArea: Boolean = true
)

val LocalRuntimeEnvironment = staticCompositionLocalOf { RuntimeEnvironment() }
val LocalRuntimeFlags = staticCompositionLocalOf { RuntimeFlags() }

/**
 * 顶部安全区稳定器（粘滞最大值兜底）。
 *
 * Kuikly runtime 的 `safeAreaInsets.top` 在某些 host / 某些导航时机会偶发回 0，导致顶部
 * 留白塌掉、内容压到状态栏。竖屏 status bar 高度不会变小，所以一旦观测到非 0 顶部 inset，
 * 就把它粘滞住，后续偶发的 0 帧用粘滞值兜底。orientation（横竖屏）切换时重置粘滞，避免把
 * 上一方向的过大顶值带到新方向。
 *
 * 注意：bottom 不做粘滞——home indicator / 键盘会真实变化，简单取最大值会把输入框搞乱
 * （留给 Phase 2 的 keyboard/home-indicator 分离处理）。
 *
 * 这是 remember 作用域内对象的普通字段（非 Compose State）；驱动重算的是 LocalConfiguration
 * 提供的反应式 raw 值，字段只是缓存，故在 composition 中调用 [stabilize] 不违反 Compose 规则。
 */
internal class SafeAreaStabilizer {
    private var lastNonZeroTop: Dp = 0.dp
    private var orientationKey: Boolean? = null

    fun stabilize(raw: SafeArea): SafeArea {
        // 用 left/right 是否有 inset 粗略判定横屏；翻转即重置顶部粘滞。
        val landscapeish = raw.left.value > 0f || raw.right.value > 0f
        if (orientationKey != landscapeish) {
            orientationKey = landscapeish
            lastNonZeroTop = 0.dp
        }
        if (raw.top.value > lastNonZeroTop.value) {
            lastNonZeroTop = raw.top
        }
        val stableTop = if (raw.top.value >= lastNonZeroTop.value) raw.top else lastNonZeroTop
        return SafeArea(
            top = stableTop,
            bottom = raw.bottom,
            left = raw.left,
            right = raw.right
        )
    }
}

@Composable
fun ProvideRuntimeEnvironment(
    flags: RuntimeFlags = RuntimeFlags(),
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val safeInsets = configuration.safeAreaInsets
    val baseSafeArea = SafeArea(
        top = safeInsets.top.dp,
        bottom = safeInsets.bottom.dp,
        left = safeInsets.left.dp,
        right = safeInsets.right.dp
    )
    // host override（如 Android 某些机型补测的 inset）先并入 raw。
    val rawSafeArea = RuntimeInsetsBridge.mergeWith(baseSafeArea)
    // 再过稳定器：顶部粘滞兜底，消除偶发 0。
    val stabilizer = remember { SafeAreaStabilizer() }
    val stableSafeArea = stabilizer.stabilize(rawSafeArea)

    val environment = RuntimeEnvironment(
        safeArea = stableSafeArea,
        rawSafeArea = rawSafeArea
    )

    CompositionLocalProvider(
        LocalRuntimeEnvironment provides environment,
        LocalRuntimeFlags provides flags
    ) {
        content()
    }
}
