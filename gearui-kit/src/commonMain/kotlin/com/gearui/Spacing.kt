package com.gearui

/**
 * Legacy spacing constants (Float-typed, `Spacing.spacer16.dp` call shape).
 *
 * Replaced by [com.gearui.foundation.layout.Spacing] (Dp-typed) as the
 * single source of truth for GearUI spacing tokens
 * (`docs/TOKEN_FREEZE_DECISIONS.md` Decision 3).
 *
 * Existing call sites compile unchanged. Will be removed before 1.0 RC.
 *
 * Migration map (legacy spacer* → `com.gearui.foundation.layout.Spacing`):
 *
 *   spacer4   → xs       (4.dp)
 *   spacer8   → sm       (8.dp)
 *   spacer12  → md       (12.dp)
 *   spacer16  → lg       (16.dp)
 *   spacer24  → xl       (24.dp)
 *   spacer32  → xxl      (32.dp)
 *   spacer40  → xxxl     (40.dp)
 *   spacer48  → huge     (48.dp)
 *   spacer64  → massive  (64.dp)
 *
 * Legacy values without a current canonical name (`spacer96`, `spacer160`)
 * will not be promoted to canonical spacing tokens; if a component needs
 * such a large value, declare it as a component token instead.
 */
@Deprecated(
    "Use com.gearui.foundation.layout.Spacing (Dp values). " +
        "Will be removed before 1.0 RC."
)
object Spacing {
    /** 4dp - 最小间距 */
    const val spacer4 = 4f

    /** 8dp - 基础间距单位 */
    const val spacer8 = 8f

    /** 12dp - 紧凑间距 */
    const val spacer12 = 12f

    /** 16dp - 标准间距 */
    const val spacer16 = 16f

    /** 24dp - 中等间距 */
    const val spacer24 = 24f

    /** 32dp - 较大间距 */
    const val spacer32 = 32f

    /** 40dp - 大间距 */
    const val spacer40 = 40f

    /** 48dp - 超大间距 */
    const val spacer48 = 48f

    /** 64dp - 特大间距 */
    const val spacer64 = 64f

    /** 96dp - 巨大间距 */
    const val spacer96 = 96f

    /** 160dp - 超级间距 */
    const val spacer160 = 160f
}
