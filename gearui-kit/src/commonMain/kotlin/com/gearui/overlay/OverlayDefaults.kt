package com.gearui.overlay

import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * Overlay 运行时默认值。
 *
 * scrim（遮罩）是 Runtime 层 token，不属于核心 Colors（见 TOKEN_FREEZE_DECISIONS Decision 1）。
 * Overlay/Dialog/BottomSheet/ActionSheet 等模态层统一从这里取遮罩色。
 */
object OverlayDefaults {
    /** 模态遮罩色：近黑 + ~40% 透明度，移动端克制的压暗强度。 */
    val scrimColor: Color = Color(0x6609090B)
}
