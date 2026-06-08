package com.gearui.navigation

import androidx.compose.runtime.Stable

/**
 * Navigator v1 公共 API 类型。
 *
 * 设计取向（参见 `gearui-kit/docs/NAVIGATOR_SWIPE_BACK_DESIGN.md`）：
 * - **不**公开 typed params；参数由业务层用 outer state holder + [Navigator.onEntryRemoved] 桥接
 * - **不**公开 SaveableStateHolder；Navigator 内部按 [NavEntry.key] 自管
 * - back 接入复用 Kuikly `BackHandler`，topmost-only 语义；Navigator 栈底必须 dispose 自己的 BackHandler
 */

/**
 * 栈中的一项。
 *
 * @property route 业务 dispatch key（"chat", "profile_edit"）
 * @property key entry 唯一身份；同 route 多次 push 需要业务传不同 key，否则 SaveableState 会串
 * @property options 行为开关（swipeBack / transition / presentation / pop 拦截）
 */
@Stable
data class NavEntry(
    val route: String,
    val key: String,
    val options: NavOptions = NavOptions.Default,
)

/**
 * 单个 entry 的行为开关。默认值跟普通业务页面一致：可滑返回、push 动画、Push 呈现。
 */
@Stable
data class NavOptions(
    val swipeBackEnabled: Boolean = true,
    val transition: NavTransition = NavTransition.SlidePush,
    val presentation: NavPresentation = NavPresentation.Push,
    /**
     * pop 拦截钩子。返回 [PopDecision.Pending] 时本次 BACK 已视为 consumed，
     * Navigator **不**保留 continuation；业务弹确认框后自己调
     * [NavigatorController.forcePop] 继续，或显式 [NavigatorController.pop] 取消。
     */
    val onPopRequest: ((PopRequest) -> PopDecision)? = null,
) {
    companion object {
        val Default = NavOptions()
    }
}

/** 入场/出场动画风格。Commit 1 只实现瞬切；Commit 2 会加 [SlidePush]/[FadeIn]/[ModalSheet] 实现。 */
enum class NavTransition { SlidePush, FadeIn, ModalSheet }

/** 呈现语义。 */
enum class NavPresentation {
    /** 普通页面入栈，参与微信式 edge swipe pop。previous 层在 swipe/动画期间保留。 */
    Push,

    /** 覆盖式沉浸层（图片/视频预览）。previous 层一直保留，但不参与 edge swipe；关闭直接回 previous。 */
    Overlay,

    /** 全屏模态（任务/表单弹层）。previous 层不跟随位移；不参与 edge swipe。 */
    Modal,
}

/** Pop 请求附带的上下文。 */
@Stable
data class PopRequest(
    val entry: NavEntry,
    val reason: PopReason,
)

/** Pop 发起方。 */
enum class PopReason {
    /** 系统返回键（Kuikly BackHandler）。 */
    BackButton,

    /** 左边缘右滑提交。 */
    EdgeSwipe,

    /** 业务代码主动调 [NavigatorController.pop]。 */
    Programmatic,
}

/**
 * [NavOptions.onPopRequest] 的返回值。
 *
 * - [Allow]：放行，Navigator 继续 pop 流程
 * - [Deny]：本次 BACK 被业务吃掉，不 pop（栈结构不变）
 * - [Pending]：本次 BACK 已视为 consumed，业务弹确认框；Navigator 不挂 continuation，
 *   业务必须显式调 [NavigatorController.forcePop] 才能真正 pop，或者什么都不调 = 取消
 */
enum class PopDecision { Allow, Deny, Pending }

/**
 * Navigator 操作入口。Composition 中通过 [EntryScope.controller] 拿到引用。
 *
 * 注意：业务不要从 [EntryScope.entry] 推断「全局当前页」——`entry` 是该层渲染的 entry，
 * Navigator 在 transition 期间会同时渲染 current 和 previous 两层。
 */
@Stable
interface NavigatorController {
    val current: NavEntry
    val previous: NavEntry?
    val canPop: Boolean
    val isTransitioning: Boolean

    fun push(route: String, key: String? = null, options: NavOptions = NavOptions.Default)

    /** 触发 [NavOptions.onPopRequest]；按返回值决定真 pop / 拒绝 / 挂起。栈底返回 false。 */
    fun pop(): Boolean

    /** 跳过 [NavOptions.onPopRequest]，用于业务确认 dirty 后继续返回。栈底返回 false。 */
    fun forcePop(): Boolean

    /** 弹到栈中最近一个 [route] 匹配的 entry。已经在栈顶或栈中无匹配返回 false。 */
    fun popTo(route: String): Boolean

    fun replace(route: String, key: String? = null, options: NavOptions = NavOptions.Default)

    /** 清栈到只剩 [route]。所有被移除的 entry 触发 onEntryRemoved。 */
    fun resetTo(route: String)
}

/**
 * 单个 entry 渲染时的局部作用域。
 *
 * - [entry] 是**这层**渲染的 entry（不一定是栈顶）
 * - [isTop] = 是否为栈顶；transition 中 previous 层渲染时为 false
 * - [isForeground] = top 且无 transition 进行中；业务可借此暂停轮询/动画
 */
@Stable
interface EntryScope {
    val entry: NavEntry
    val controller: NavigatorController
    val isTop: Boolean
    val isForeground: Boolean
}
