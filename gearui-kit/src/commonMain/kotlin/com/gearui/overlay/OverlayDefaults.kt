package com.gearui.overlay

import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * Overlay 运行时默认值。
 *
 * scrim（遮罩）是 Runtime 层 token，不属于核心 Colors（见 TOKEN_FREEZE_DECISIONS Decision 1）。
 * Overlay/Dialog/BottomSheet/ActionSheet 等模态层统一从这里取遮罩色。
 */
object OverlayDefaults {
    /**
     * 模态遮罩色：纯黑 + ~55% 透明度。
     *
     * 必须用纯黑做底色而非近黑（如 09090B）——暗色主题下页面背景本身接近 09090B，
     * 若遮罩底色也是近黑则几乎不产生压暗，弹层无法与背景分离。纯黑遮罩在明暗两种
     * 主题下都能真正压暗背景。
     */
    val scrimColor: Color = Color(0x8C000000)
}
