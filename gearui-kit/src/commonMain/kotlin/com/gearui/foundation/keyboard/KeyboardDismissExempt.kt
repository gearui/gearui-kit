package com.gearui.foundation.keyboard

import androidx.compose.runtime.staticCompositionLocalOf
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.composed
import com.tencent.kuikly.compose.ui.input.pointer.PointerEventPass
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput

/**
 * 「这次触摸落在输入区域里」的认领台。
 *
 * 判据必须是**落点在不在输入区域内**，不是「触摸有没有被消费」——按钮同样消费触摸，
 * 拿消费当代理会让点「登录」也不收键盘（2026-09-10 报上来的 bug）。
 *
 * 但也不能靠比对坐标：Kuikly 里 `boundsInRoot()` 与 pointerInput 的落点不在同一个坐标
 * 空间，实测判定会恒为真，结果变成哪儿都不收键盘。
 *
 * 所以改成在**同一次手势内**传递：Compose 的 Initial 传递自上而下，容器先拿到 down 并
 * 复位，输入框随后在自己的 Initial 里认领；容器再到 Final（子节点都处理完）读结果。
 * 全程不涉及坐标换算。
 */
class KeyboardDismissExemptions {
    private var claimed = false

    /** 容器在每次手势开始时复位。 */
    internal fun beginGesture() {
        claimed = false
    }

    /** 输入区域在收到 down 时认领本次手势。 */
    internal fun claim() {
        claimed = true
    }

    internal fun isClaimed(): Boolean = claimed
}

val LocalKeyboardDismissExemptions = staticCompositionLocalOf { KeyboardDismissExemptions() }

/**
 * 把这块区域标为「点它不收键盘」。
 *
 * GearUI 的文本输入组件默认带上。**组合式输入栏**（聊天页那种"输入框 + 表情 + 发送"
 * 一整条）应该整条标上——发送按钮在文本框外面，但它属于输入这件事，发一条就收键盘
 * 不是用户要的。
 */
fun Modifier.keyboardDismissExempt(): Modifier = composed {
    val exemptions = LocalKeyboardDismissExemptions.current
    pointerInput(exemptions) {
        awaitEachGesture {
            // 不消费，只登记：这次手势起点在输入区域里。
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            exemptions.claim()
        }
    }
}
