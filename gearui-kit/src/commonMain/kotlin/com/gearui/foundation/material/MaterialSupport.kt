package com.gearui.foundation.material

import androidx.compose.runtime.Composable
import com.gearui.runtime.LocalRuntimeFlags
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration

/**
 * Whether frosted glass is used, and who decides.
 */
enum class MaterialPolicy {
    /** Blur where the platform renders it on the GPU; a flat surface everywhere else. See [isMaterialBlurEnabled]. */
    Auto,

    /** Always blur. For demos and for hosts that have measured their own devices. */
    Always,

    /** Never blur. Every material renders as an opaque surface. */
    Never,
}

/**
 * Resolves whether a [MaterialSurface] blurs on this device.
 *
 * Under [MaterialPolicy.Auto] the answer is per-platform, and each branch is a
 * property of KuiklyUI's renderer rather than a guess:
 *
 * - **iOS / macOS** — `KRBlurView` is a `UIVisualEffectView`. Composited by the
 *   system, effectively free. Enabled.
 *
 * - **Android API 31+** — `KRBlurView` picks `RenderEffectBlur`, which is a
 *   GPU render effect. Enabled.
 *
 * - **Android below 31** — it falls back to `RenderScriptBlur`, which draws the
 *   decor view into a bitmap and blurs it on the CPU every frame that the
 *   content behind changes. Under a scrolling list that is the whole screen
 *   re-captured per frame. Disabled; GearUI's `minSdk` is 21, so this is not a
 *   rare device.
 *
 * - **HarmonyOS** — `KRBlurView.ets` is a system effect. Enabled.
 *
 * - **Web** — disabled, and this is the one branch that is a policy choice
 *   rather than a performance one. The web renderer sets CSS
 *   `backdrop-filter`, which *fails open*: a browser without support ignores
 *   the declaration silently and leaves the tint behind as a plain translucent
 *   wash over arbitrary page content. Nothing reaches Kotlin to say it did not
 *   take, so GearUI cannot detect the failure and correct it. Shipping
 *   unreadable text on those browsers is worse than shipping a flat surface on
 *   all of them. A host that knows its browsers can opt in with
 *   [MaterialPolicy.Always].
 */
@Composable
fun isMaterialBlurEnabled(): Boolean {
    return when (LocalRuntimeFlags.current.materialPolicy) {
        MaterialPolicy.Always -> true
        MaterialPolicy.Never -> false
        MaterialPolicy.Auto -> {
            val configuration = LocalConfiguration.current
            when {
                configuration.isIOS || configuration.isMacOS -> true
                configuration.isAndroid ->
                    // Android reports Build.VERSION.SDK_INT here, so this is an
                    // exact test for the RenderEffect backend, not a guess at it.
                    (configuration.osVersion.toIntOrNull() ?: 0) >= ANDROID_RENDER_EFFECT_SDK
                configuration.platform == PLATFORM_OHOS -> true
                // Web, and any renderer added later. Defaulting an unknown
                // renderer to "no blur" degrades to a flat surface, which is
                // always legible; defaulting it to "blur" degrades to a
                // translucent wash, which may not be.
                else -> false
            }
        }
    }
}

/** Android 12. Below it `KRBlurView` blurs on the CPU. */
private const val ANDROID_RENDER_EFFECT_SDK = 31

/** `Configuration.platform` has no `isOhos`; the ohos renderer reports this string. */
private const val PLATFORM_OHOS = "ohos"
