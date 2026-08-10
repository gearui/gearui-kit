package com.gearui.overlay

import androidx.compose.runtime.Composable
import com.gearui.foundation.elevation.Elevation
import com.gearui.foundation.layout.Radius
import com.gearui.theme.Theme
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.gearui.runtime.LocalRuntimeEnvironment
import com.gearui.foundation.layout.Spacing

/**
 * Overlay 运行时默认值。
 *
 * scrim（遮罩）是 Runtime 层 token，不属于核心 Colors（见 TOKEN_FREEZE_DECISIONS Decision 1）。
 * Overlay/Dialog/BottomSheet/ActionSheet 等模态层统一从这里取遮罩色。
 *
 * ## 浮层表面契约
 *
 * 浮层按「怎么定位」分三类，每类的形状与高度成对固定，组件不要自己挑档位：
 *
 * | 类别 | 形状 | 高度 | 成员 |
 * |---|---|---|---|
 * | panel（贴触发点/瞬时） | [panelShape] `md` 6dp | [Elevation.raised] / [Elevation.floating] | Select·Cascader·TreeSelect 下拉、Popup、Popover、ContextMenu、Toast、Snackbar、Notification |
 * | modal（居中抢焦点） | [modalShape] `xl` 12dp | [Elevation.modal] | Dialog、Tour |
 * | sheet（贴视口边缘） | [sheetShape] 顶部 12dp | 无阴影（靠 scrim 分离） | BottomSheet、ActionSheet、Drawer |
 *
 * 引入之前这三类是混着来的：Dialog 用 `lg`(8) 而 Select 下拉用 `xl`(12)，
 * 于是模态比下拉还方；Cascader/TreeSelect/Popup/Snackbar 用 `sm`(4)，
 * Popover/ContextMenu/Notification 用 `md`(6) —— 同一类浮层四种圆角。
 *
 * 另外三条运行期规则由 [OverlayHost] 保证，组件不要自己实现：
 *  - scrim 永远覆盖整个 viewport，不受安全区影响；
 *  - 安全区只作用于弹层**内容**，通过 `OverlayOptions.safeArea*` 声明；
 *  - sheet / drawer / actionSheet 从 viewport 边缘起算，不从安全区边缘起算。
 */
object OverlayDefaults {
    /** 贴触发点或瞬时浮层的表面形状。 */
    val panelShape: Shape
        @Composable get() = Theme.shapes.md

    /** 居中模态卡片的表面形状。 */
    val modalShape: Shape
        @Composable get() = Theme.shapes.xl

    /** 贴边 sheet 的圆角半径（只圆朝向内容的两个角）。 */
    val sheetCornerRadius: Dp = Radius.xl

    /** 从底部升起的 sheet：只圆上面两个角。 */
    val sheetShape: Shape =
        RoundedCornerShape(topStart = sheetCornerRadius, topEnd = sheetCornerRadius)

    /**
     * 模态遮罩色：纯黑 + ~55% 透明度。
     *
     * 必须用纯黑做底色而非近黑（如 09090B）——暗色主题下页面背景本身接近 09090B，
     * 若遮罩底色也是近黑则几乎不产生压暗，弹层无法与背景分离。纯黑遮罩在明暗两种
     * 主题下都能真正压暗背景。
     */
    val scrimColor: Color = Color(0x8C000000)
}

/**
 * 顶部浮动反馈（Snackbar / Notification）的实际顶部距离。
 *
 * 这类组件对外暴露一个 `topOffset`，语义是「至少离顶部这么远」，而不是绝对位置：
 * 写死的 48dp 在灵动岛机型（top inset ≈ 59pt）上会让横幅压在灵动岛下面。这里取
 * `safeArea.top + Spacing.sm` 与调用方给的下限中的较大者。
 *
 * 读的是 [com.gearui.runtime.RuntimeEnvironment.safeArea]（稳定后的值）而不是
 * `rawSafeArea`：raw 值会偶发回 0，横幅会因此跳动。
 *
 * `internal` —— 这是浮层契约的实现细节，不是对外 API。
 */
@Composable
internal fun rememberTopFloatingOffset(minOffset: Dp): Dp {
    val safeTop = LocalRuntimeEnvironment.current.safeArea.top
    val safeOffset = safeTop + Spacing.sm
    return if (safeOffset > minOffset) safeOffset else minOffset
}
