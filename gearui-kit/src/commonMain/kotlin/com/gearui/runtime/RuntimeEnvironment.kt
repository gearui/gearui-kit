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
data class KeyboardInset(
    val height: Dp = 0.dp,
) {
    val visible: Boolean get() = height > 0.dp
}

@Immutable
data class RuntimeEnvironment(
    /**
     * 稳定后的安全区（去抖 + 粘滞 + host override 合并）。**页面/组件布局只用这个**。
     */
    val safeArea: SafeArea = SafeArea(),
    /**
     * 运行时/host 原始安全区，仅用于诊断；可能偶发回 0 或不完整，不要直接用于布局。
     */
    val rawSafeArea: SafeArea = SafeArea(),
    /** IME geometry is not a system safe area and must never be consumed by page chrome. */
    val keyboard: KeyboardInset = KeyboardInset(),
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
 * 顶部安全区稳定器。
 *
 * Kuikly iOS 2.21.0 及以后在 Scene 尚未 active 时可能先上报 0；同一方向内只过滤这种瞬时 0，
 * 非零变化必须立即接受（通话状态栏、分屏和窗口变化都可能合法改变 top）。方向改变时清空
 * 缓存，避免把竖屏值带入横屏。
 *
 * bottom 只缓存最后一个非键盘系统值。键盘显示期间以及隐藏通知与系统 safe-area 恢复之间的
 * 短暂窗口，都不会让 IME 高度污染系统安全区。
 *
 * 这是 remember 作用域内对象的普通字段（非 Compose State）；驱动重算的是 LocalConfiguration
 * 提供的反应式 raw 值，字段只是缓存，故在 composition 中调用 [stabilize] 不违反 Compose 规则。
 */
internal class SafeAreaStabilizer {
    private var lastNonZeroTop: Dp = 0.dp
    private var lastSystemBottom: Dp = 0.dp
    private var awaitingBottomAfterKeyboard = false
    private var portrait: Boolean? = null

    fun stabilize(
        raw: SafeArea,
        isPortrait: Boolean,
        fallbackTop: Dp,
        keyboardHeight: Dp = 0.dp,
    ): SafeArea {
        if (portrait != isPortrait) {
            portrait = isPortrait
            lastNonZeroTop = 0.dp
            lastSystemBottom = 0.dp
            awaitingBottomAfterKeyboard = false
        }
        if (raw.top > 0.dp) {
            lastNonZeroTop = raw.top
        }
        val stableTop = when {
            raw.top > 0.dp -> raw.top
            lastNonZeroTop > 0.dp -> lastNonZeroTop
            else -> fallbackTop
        }
        if (keyboardHeight > 0.dp) {
            awaitingBottomAfterKeyboard = true
        } else if (!awaitingBottomAfterKeyboard || lastSystemBottom <= 0.dp || raw.bottom <= lastSystemBottom) {
            lastSystemBottom = raw.bottom
            awaitingBottomAfterKeyboard = false
        }
        val stableBottom = if (keyboardHeight > 0.dp) {
            if (lastSystemBottom > 0.dp) minDp(raw.bottom, lastSystemBottom) else 0.dp
        } else if (awaitingBottomAfterKeyboard) {
            lastSystemBottom
        } else {
            raw.bottom
        }
        return SafeArea(
            top = stableTop,
            bottom = stableBottom,
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
    // iOS 的 statusBarHeight 在 keyWindow 暂不可用时仍有设备级 fallback，可确保首帧至少不
    // 与状态栏重叠；宿主随后上报真实 safeAreaInsets 后会替换它。
    val isPortrait = configuration.pageViewHeight >= configuration.pageViewWidth
    val fallbackTop = if (configuration.isIOS && isPortrait && configuration.statusBarHeight > 0f) {
        configuration.statusBarHeight.dp
    } else {
        0.dp
    }
    val stabilizer = remember { SafeAreaStabilizer() }
    val keyboardHeight = RuntimeInsetsBridge.keyboardHeightOverride
    val stableSafeArea = stabilizer.stabilize(
        raw = rawSafeArea,
        isPortrait = isPortrait,
        fallbackTop = fallbackTop,
        keyboardHeight = keyboardHeight,
    )

    val environment = RuntimeEnvironment(
        safeArea = stableSafeArea,
        rawSafeArea = rawSafeArea,
        keyboard = KeyboardInset(height = keyboardHeight)
    )

    CompositionLocalProvider(
        LocalRuntimeEnvironment provides environment,
        LocalRuntimeFlags provides flags
    ) {
        content()
    }
}
private fun minDp(a: Dp, b: Dp): Dp = if (a <= b) a else b
