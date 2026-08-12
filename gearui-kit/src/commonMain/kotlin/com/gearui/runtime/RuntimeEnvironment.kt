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
     * Stabilised safe area (debounced + sticky + merged with the host override). **Page and component layout uses only this.**
     */
    val safeArea: SafeArea = SafeArea(),
    /**
     * Raw safe area from the runtime or host, for diagnostics only; it may briefly report 0 or be incomplete, so never lay out against it.
     */
    val rawSafeArea: SafeArea = SafeArea(),
    /** IME geometry is not a system safe area and must never be consumed by page chrome. */
    val keyboard: KeyboardInset = KeyboardInset(),
)

@Immutable
data class RuntimeFlags(
    // 1.0 default: page chrome consumes the stabilised safe area through
    // PageScaffold and the runtime helpers. The flag stays as a rollback switch
    // while host integrations settle, but the legacy path is no longer the model
    // anything is validated against.
    val unifiedSafeAreaPipeline: Boolean = true,
    // Component safe-area consumption policy (runtime-owned, app-wide).
    val navBarConsumesTopSafeArea: Boolean = false,
    val bottomNavBarConsumesBottomSafeArea: Boolean = true,
    val drawerConsumesVerticalSafeArea: Boolean = true,
    val actionSheetConsumesBottomSafeArea: Boolean = true,
    val bottomSheetConsumesBottomSafeArea: Boolean = true
)

val LocalRuntimeEnvironment = staticCompositionLocalOf { RuntimeEnvironment() }
val LocalRuntimeFlags = staticCompositionLocalOf { RuntimeFlags() }

/**
 * Top safe area stabiliser.
 *
 * Kuikly iOS 2.21.0 and later may report 0 while the Scene is not yet active, so within one orientation only that transient 0
 * is filtered; a non-zero change must be accepted immediately (an in-call status bar, split screen and window changes may all
 * legitimately move top). The cache is cleared on orientation change, so a portrait value is never carried into landscape.
 *
 * bottom caches only the last non-keyboard system value. Neither the time the keyboard is shown nor the short window between
 * the hide notification and the system restoring its safe area lets the IME height pollute it.
 *
 * These are plain fields on an object inside a remember scope (not Compose State); recomposition is driven by the reactive raw
 * values from LocalConfiguration and the fields are only a cache, so calling [stabilize] during composition is legal.
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
    // The host override (an inset re-measured on certain Android devices, for instance) is merged into raw first.
    val rawSafeArea = RuntimeInsetsBridge.mergeWith(baseSafeArea)
    // iOS statusBarHeight still has a device-level fallback while keyWindow is unavailable, which at least keeps the
    // first frame clear of the status bar; the host replaces it once it reports the real safeAreaInsets.
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

/** The four edges of the safe area. */
internal enum class SafeAreaEdge { Top, Bottom, Left, Right }

/**
 * Resolves the safe-area inset for one edge. This is the only entry point
 * through which a component consumes an inset.
 *
 * NavBar, BottomNavBar, Drawer, ActionSheet and BottomSheet each used to carry
 * the same three-branch logic — pick the pipeline, check the component's flag,
 * fall back to LocalConfiguration. Five copies meant five places to change, and
 * all five had to sit on the safe-area guard's allowlist. With the policy owned
 * here, that allowlist is back to the runtime and the overlay host.
 *
 * **It resolves, it does not apply.** Where the padding goes is the component's
 * business, because it genuinely differs: an edge-anchored sheet must paint its
 * surface to the viewport edge and inset only its content, or a strip of scrim
 * shows through at the home indicator, whereas Drawer wants the whole content
 * inset. Moving application in here would break the former.
 *
 * @param consume whether this component consumes this edge (app-wide policy
 *                supplied by the runtime).
 * @param extra   added in every branch.
 * @param minimum floor for the result; sheets use it to keep breathing room
 *                even when the system reports no inset.
 */
@Composable
internal fun rememberSafeAreaInset(
    edge: SafeAreaEdge,
    consume: Boolean,
    extra: Dp = 0.dp,
    minimum: Dp = 0.dp,
): Dp {
    val flags = LocalRuntimeFlags.current
    val environment = LocalRuntimeEnvironment.current
    val configuration = LocalConfiguration.current

    val stable = when (edge) {
        SafeAreaEdge.Top -> environment.safeArea.top
        SafeAreaEdge.Bottom -> environment.safeArea.bottom
        SafeAreaEdge.Left -> environment.safeArea.left
        SafeAreaEdge.Right -> environment.safeArea.right
    }
    val legacy = when (edge) {
        SafeAreaEdge.Top -> configuration.safeAreaInsets.top.dp
        SafeAreaEdge.Bottom -> configuration.safeAreaInsets.bottom.dp
        SafeAreaEdge.Left -> configuration.safeAreaInsets.left.dp
        SafeAreaEdge.Right -> configuration.safeAreaInsets.right.dp
    }

    // The legacy branch deliberately ignores [consume]; that is the pre-migration
    // behaviour, kept only as a rollback switch for host integrations. The
    // standard path runs with unifiedSafeAreaPipeline = true, where the
    // per-component consumption policy actually takes effect.
    val resolved = if (flags.unifiedSafeAreaPipeline) {
        if (consume) stable else 0.dp
    } else {
        legacy
    }

    val total = resolved + extra
    return if (total > minimum) total else minimum
}
