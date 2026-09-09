package com.gearui.foundation.keyboard

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.input.pointer.PointerEventPass
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.input.pointer.positionChange
import com.tencent.kuikly.compose.ui.platform.LocalFocusManager
import com.tencent.kuikly.compose.ui.platform.LocalSoftwareKeyboardController
import kotlin.math.abs

enum class KeyboardDismissMode {
    None,
    OnTap,
    OnScroll,
    OnTapOrScroll,
}

private val KeyboardDismissMode.dismissOnTap: Boolean
    get() = this == KeyboardDismissMode.OnTap || this == KeyboardDismissMode.OnTapOrScroll

private val KeyboardDismissMode.dismissOnScroll: Boolean
    get() = this == KeyboardDismissMode.OnScroll || this == KeyboardDismissMode.OnTapOrScroll

@Composable
internal fun KeyboardDismissContainer(
    mode: KeyboardDismissMode,
    content: @Composable () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val gestureModifier = if (mode == KeyboardDismissMode.None) {
        Modifier
    } else {
        Modifier.pointerInput(mode) {
            val dragThreshold = 10f

            fun dismiss() {
                runCatching { focusManager.clearFocus(force = true) }
                keyboardController?.hide()
            }

            awaitEachGesture {
                // 🔴 这个容器铺满整个 App，判定必须放在 Final 传递里。
                //
                // 原来是 `awaitFirstDown(requireUnconsumed = false)` + 默认的 Main 传递：
                // 子节点消费与否完全不看，于是**点在输入框上也会收起键盘**——输入框刚拿到
                // 焦点、键盘弹起，手指一抬这里就 clearFocus + hide，看起来就是"点输入框把
                // 键盘关了"。长按同理：只要位移没超阈值，抬手就被当成一次点击，系统的
                // 粘贴菜单刚要出来就被这里掐掉。
                //
                // Initial 只用来开始跟踪手势（不判定、不消费），Final 在所有子节点处理完
                // 之后才轮到，这时 change.isConsumed 才是可信的。
                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)

                var totalDrag = 0f
                var didDismissForScroll = false

                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Final)
                    val change = event.changes.firstOrNull() ?: break

                    if (!change.pressed) {
                        // 被子节点消费掉的抬手 = 点在了可交互控件上（输入框、按钮、链接、
                        // 长按菜单），不是"点空白处"，不收键盘。
                        if (!didDismissForScroll &&
                            !change.isConsumed &&
                            totalDrag <= dragThreshold &&
                            mode.dismissOnTap
                        ) {
                            dismiss()
                        }
                        break
                    }

                    val delta = change.positionChange()
                    totalDrag += abs(delta.x) + abs(delta.y)
                    // 滑动不看消费：列表滚动本来就会被 LazyColumn 消费掉，
                    // 而"一滚就收键盘"正是想要的行为。
                    if (!didDismissForScroll && totalDrag > dragThreshold && mode.dismissOnScroll) {
                        dismiss()
                        didDismissForScroll = true
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(gestureModifier)
    ) {
        content()
    }
}

