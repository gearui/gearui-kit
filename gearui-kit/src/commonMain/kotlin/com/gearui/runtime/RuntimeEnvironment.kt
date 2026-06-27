package com.gearui.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameNanos
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
 * 冷启动安全区 ticker 的逐帧轮询上限（~2s @60fps）。
 * 防止在真正 0 顶部 inset 的设备（如无刘海旧机的某些形态）上无限逐帧空转。
 */
private const val MAX_INSET_SETTLE_FRAMES = 120

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

    // ── 冷启动安全区竞态修复 ──────────────────────────────────────────────
    // Kuikly Compose 层的 `configuration.safeAreaInsets` 是裸 getter
    // （compose/.../platform/LocalConfiguration.kt：`get() = pageData.safeAreaInsets`），
    // **不是 Compose snapshot state**。同一个 Configuration 里 width/height/fontScale 都用
    // mutableStateOf 包了反应式，唯独 safeAreaInsets 漏了：native 在首帧之后才把真实 inset
    // 投递进 pageData（Pager.handlePagerViewSizeDidChanged），这一步**不会触发 Compose 重组**。
    //
    // 后果：冷启动若 Compose 首帧组合早于 inset 到达，顶部安全区读到 0 后就**卡住不恢复**
    // （NavBar 被状态栏/刘海盖住、内容顶到状态栏下），只有等后续某次尺寸变化顺带触发重组才
    // 自愈——纯竖屏冷启动没有这种事件，于是表现为「偶尔启动顶部塌掉」。
    // （Android 侧靠 host 主动 push RuntimeInsetsBridge 兜底，iOS 没有，故 iOS 高发。）
    //
    // 修法：用一个逐帧 ticker 在冷启动期间强制重组，使下方 fresh 读取能追平真实 inset；
    // 一旦观测到非 0 顶部 inset 即收敛停止。之后 orientation/键盘等仍由 fresh 读取 + 各自
    // 反应式路径覆盖，本 ticker 不再参与。设帧数上限避免在真正 0 顶 inset 的设备上空转。
    var insetSettleTick by remember { mutableStateOf(0) }
    LaunchedEffect(configuration) {
        var frames = 0
        var lastTop = configuration.safeAreaInsets.top
        while (lastTop <= 0f && frames < MAX_INSET_SETTLE_FRAMES) {
            withFrameNanos { }
            frames++
            val now = configuration.safeAreaInsets.top
            if (now != lastTop) {
                lastTop = now
                insetSettleTick++ // 仅在真正变化时强制一次重组
            }
        }
    }
    // 读 tick 建立重组依赖（值本身不使用）；冷启动期间它被 bump 后会让下面重新 fresh 读取。
    @Suppress("UNUSED_EXPRESSION")
    insetSettleTick

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
