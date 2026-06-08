# Navigator + 微信式 Edge Swipe Back —— 设计文档

状态：**Draft**（本轮只写设计，不动代码）
所有者：gearui-kit
消费者：privchat-app（首位）、未来其他 Kuikly Compose 业务

---

## 1. 当前 privchat-app 路由审计

只读审计，结论先放：**路由实现是真栈，但页面渲染是单页**——这是为什么「滑动返回看不到上一页」的根因。

### 1.1 实现位置

`privchat-app/privchat/src/commonMain/kotlin/com/netonstream/privchat/app/PrivChatApp.kt`，约 1100–2120 行。

```kotlin
private enum class MobilePage {        // 行 1106：扁平 enum
    MAIN,                              // tab 容器
    CHAT, CHAT_SETTINGS,
    GROUP_*, FRIEND_*, PROFILE_*,
    MY_QR_CODE, SCAN_QR_CODE,
    FORWARD_PICKER,
    VIDEO_PREVIEW, IMAGE_PREVIEW,
    // ……共 28 项
}

var currentPage by remember { mutableStateOf(MobilePage.MAIN) }   // 行 1175
val pageStack = remember { mutableStateListOf<MobilePage>() }     // 行 1176
var navLockUntilMs by remember { mutableStateOf(0L) }             // 行 1177

fun pushPage(page: MobilePage) {                  // 行 1179
    if (currentPage != page) {
        pageStack.add(currentPage)
        currentPage = page
    }
}
fun replacePage(page: MobilePage) { currentPage = page }
fun popPage(fallback: MobilePage = MobilePage.MAIN) {
    currentPage = if (pageStack.isNotEmpty()) pageStack.removeAt(pageStack.lastIndex) else fallback
}
fun resetToMain() { pageStack.clear(); currentPage = MobilePage.MAIN }   // 行 1194
```

### 1.2 栈结构是「真栈」但渲染是「单页」

- `pageStack` 是 `MutableList<MobilePage>`，可以握住任意深度（PROFILE_EDIT → PROFILE_NICKNAME → PROFILE_USERNAME 多层都能正常 pop）
- 渲染路径是 `when (currentPage) { ... }`（行 1271），一次只 emit **一个 case**
- 后果：滑动手势开始时上一页根本不在 Composition 树里，背后是黑屏不是上一页
- 这是当前「滑动返回不像微信」的根因，**不是手势本身的问题**

### 1.3 SwipeBack 当前完成度

- `gearui-kit/src/commonMain/kotlin/com/gearui/gestures/SwipeBack.kt` 的 `Modifier.swipeBack(...)` **手势识别已落地**且质量在线：
  - 边缘热区 24dp、commit 阈值 96dp、fling 阈值 1200dp/s、direction ratio 1.2
  - 状态机 Idle → Tracking → Recognized → Committed/Cancelled，consume 时机仅在 Recognized 之后，不会抢子组件手势
  - 已预留 `onProgress(progress, dragX)` 回调
- privchat-app 的 `WithSwipeBack`（行 1143）只把 `onCommit = onBack` 接出去 → 没用 `onProgress` → **没有跟手位移**
- 结论：手势层 OK，缺的是**转场容器**

### 1.4 Bottom tab 和 push page 是分开的

- MAIN 容器内部有 `currentIndex: Int`（行 303，0/1/2 切「消息 / 联系人 / 我」），**不进 pageStack**
- 切 tab ≠ push；切 tab 也不能滑动返回（也不该）
- 所有真正能 push 的页面都覆盖 MAIN 整层

### 1.5 「页面参数」全靠外层 state holder

route 没参数，参数通过 outer `mutableStateOf` 传：

```kotlin
var selectedChannel by remember { mutableStateOf<ChannelListEntry?>(null) }
var selectedUser by remember { mutableStateOf<UserEntry?>(null) }
var selectedFriendRequest by remember { mutableStateOf<FriendRequestEntry?>(null) }
var selectedFriend by remember { mutableStateOf<FriendEntry?>(null) }
var selectedGroupMembers by remember { mutableStateOf<List<GroupMemberEntry>>(emptyList()) }
var selectedForwardMessage by remember { mutableStateOf<MessageEntry?>(null) }
var selectedVideoMessage by remember { mutableStateOf<MessageEntry?>(null) }
var selectedImageMessage by remember { mutableStateOf<MessageEntry?>(null) }
var profileFriendSource by remember { mutableStateOf<Pair<String, String>?>(null) }
```

→ **9 个状态变量** + 28 个 page enum 一起描述「当前导航上下文」。每次 pop 时如果不显式清掉 selectedX，下次 pushPage(...) 同一个 page 会带上旧参数。
→ Navigator v1 不公开 typed params，但必须提供 `entry.key` + `onEntryRemoved(entry)`，让业务能把参数挂到 entry 生命周期上并在 pop 后自动清。

### 1.6 nav lock 没普及

`tryAcquireNavLock(450ms)` 只在 `onChatBack`（行 1277）用了一次；其它 28 个 pop 都没保护。多指快速戳「返回」可以触发双 pop。Navigator v1 应该统一接管。

### 1.7 Android BACK 键

`MainActivity.dispatchKeyEvent`（行 215）把 BACK 转给 `kuiklyDelegator.onBackPressed()`，**不进** Compose 层。也就是说：

- 系统 BACK 当前**不会**触发 popPage——只能点 NavBar 左上的箭头或在已 wrap 的页面边缘滑
- 这是个独立的 gap，Navigator v1 需要 own Android BACK

### 1.8 编辑页 dirty 拦截

`EDIT_REMARK` / `PROFILE_NICKNAME` / `PROFILE_USERNAME` 这些都是直接 `onBack = { popPage() }`，没有「未保存提示」拦截。Navigator v1 不强加，但 **API 要预留 `onPopRequest(PopRequest): PopDecision` 钩子**。

---

## 2. 页面类型分类

| 类别 | 页面 | 当前包 SwipeBack | 进 pageStack | 备注 |
|---|---|---|---|---|
| **Tab root** | MAIN | 不应 | 是（栈底） | 内部 currentIndex 切 3 个 tab |
| **Push page**（22 个） | CHAT / CHAT_SETTINGS / GROUP_* (5) / FRIEND_* (3) / EDIT_REMARK / APPEARANCE / PROFILE_* (6) / SWITCH_ACCOUNT / MY_QR_CODE / SCAN_QR_CODE / FORWARD_PICKER / SEARCH_USER / FRIEND_REQUEST / USER_PROFILE | 全部 `WithSwipeBack` 包了 | 是 | 默认行为：边缘滑可返回 |
| **Overlay / Modal** | VIDEO_PREVIEW / IMAGE_PREVIEW | **没**包（沉浸式） | 是 | 依赖来源页，关闭时回 CHAT，不参与 edge swipe |
| **Auth gate**（栈外） | startupBooting splash / LoginPage / RequiredActionFlow / loadingMessage | 不应 | 否 | 在 PrivChatApp 顶层 if/else，**没进** MobileLayout |

Navigator v1 需要表达这三档：`NavPresentation.Push` / `NavPresentation.Overlay` / `NavPresentation.Modal`。Auth gate 不进 Navigator，保持顶层 gate。

---

## 3. gearui-kit Navigator v1 API

只覆盖现在用得到的。**不做** deep link、nested navigator、tab navigator、route guard、state restore、URL routing、typed params。

### 3.1 入口 Composable

```kotlin
@Composable
fun Navigator(
    initialRoute: String,
    modifier: Modifier = Modifier,
    swipeBackEnabled: Boolean = true,
    handleBack: Boolean = true,
    onEntryRemoved: ((NavEntry) -> Unit)? = null,
    content: @Composable EntryScope.(NavEntry) -> Unit,
)
```

- `initialRoute`：栈底 route 字符串
- `swipeBackEnabled`：全局开关；个别 entry 还能在 `NavOptions` 里再禁用
- `handleBack`：是否接管系统返回（Android BACK / iOS edge / 桌面 Esc）。**平台无关命名**——内部如何对接 native BACK bridge / Kuikly native back / iOS gesture 是 Runtime 适配层的事，调用方不感知
- `onEntryRemoved`：entry 从栈中最终移除后回调（pop 动画结束、replace/reset 清栈后触发），v1 用于业务清理外部 `selectedX` / external args map，避免旧参数污染
- `content`：根据传入 entry 渲染对应页面；**这一份 lambda 会被 entry list 里的每一个 entry 各自调用一遍**（top 2 entry 同时活在 Composition）

### 3.2 NavigatorController / EntryScope

```kotlin
interface NavigatorController {
    val current: NavEntry
    val previous: NavEntry?
    val canPop: Boolean                  // = previous != null
    val isTransitioning: Boolean         // swipe 中或动画中，业务可借此 disable 输入

    fun push(
        route: String,
        key: String? = null,              // null 时 Navigator 内部生成唯一 key
        options: NavOptions = NavOptions.Default,
    )
    fun pop(): Boolean                   // 真 pop；false 表示栈底拦不动
    fun forcePop(): Boolean              // 跳过 onPopRequest，用于用户确认 dirty 弹窗后的继续返回
    fun popTo(route: String): Boolean    // 弹到指定 route（resetToMain 用）
    fun replace(
        route: String,
        key: String? = null,
        options: NavOptions = NavOptions.Default,
    )
    fun resetTo(route: String)           // 清栈 + push（对齐当前 resetToMain）
}

interface EntryScope {
    val entry: NavEntry
    val controller: NavigatorController
    val isTop: Boolean                   // 当前渲染的 entry 是否为栈顶
    val isForeground: Boolean            // true = 栈顶且非 previous 预渲染层
}

data class NavOptions(
    val swipeBackEnabled: Boolean = true,
    val transition: NavTransition = NavTransition.SlidePush,
    val presentation: NavPresentation = NavPresentation.Push,
    val onPopRequest: ((PopRequest) -> PopDecision)? = null,
) {
    companion object { val Default = NavOptions() }
}

enum class NavTransition { SlidePush, FadeIn, ModalSheet }
enum class NavPresentation { Push, Overlay, Modal }

data class PopRequest(
    val entry: NavEntry,
    val reason: PopReason,
)

enum class PopReason { BackButton, EdgeSwipe, Programmatic }
enum class PopDecision { Allow, Deny, Pending }
```

- `NavigatorController` 是唯一导航操作入口；业务不要从 `EntryScope` 的 `entry` 推断全局 current
- `EntryScope` 是每个被渲染 entry 的局部上下文；渲染 previous 层时 `entry` 就是 previous，`isTop=false`
- `PopDecision.Allow`：继续 pop；`Deny`：直接拦截；`Pending`：本次返回挂起（例如弹未保存确认框），业务确认后调用 `forcePop()`
- `PopDecision.Pending` 不保存 continuation，不自动恢复本次 pop request。Navigator 只负责把本次 back/pop 视为已消费并把内部状态恢复到 idle；业务负责弹确认框，确认后显式调用 `forcePop()`，取消则什么都不做。

### 3.3 NavEntry（无 params 版）

```kotlin
data class NavEntry(
    val route: String,                              // 业务 dispatch key（"chat", "profile_edit"）
    val key: String,                                // entry 唯一身份；默认由 Navigator 生成，业务也可显式传稳定 key
    val options: NavOptions = NavOptions.Default,
)
```

**v1 不公开参数模型**。理由：

- gearui-kit 是通用组件库，公开 `Map<String, Any?>` 会留下类型不安全、KMP/Native 调试坑、state restore 收口困难
- 当前 privchat-app 已经用 9 个 outer `selectedX` state 桥接参数，**v1 直接保留**这个模式，只换路由层
- v2 再做 `NavRoute<T>` / `NavArgs` / type-safe route builder，那时才把 `selectedX` 收进 typed args

但 v1 必须解决旧参数污染：业务可以维护 `externalArgs[entry.key] = args`，Navigator 在 entry 最终移除时调用 `onEntryRemoved(entry)`，业务据此清理 `externalArgs.remove(entry.key)` 或同步清空对应 `selectedX`。

**SaveableStateHolder 不放在 NavEntry 里**——是 Navigator 内部实现细节，按 `entry.key` 维护 `SaveableStateProvider(entry.key)`，外部不感知。entry 被最终移除后，Navigator 负责移除对应 saveable state，避免 pop 后长期持有页面状态。

### 3.4 typical 用法（迁后的 PrivChatApp，v1 风格）

```kotlin
// 参数暂时继续放在 outer state，参考当前 PrivChatApp.kt 的 selectedX 模式
var selectedChannel by remember { mutableStateOf<ChannelListEntry?>(null) }
var selectedFriend by remember { mutableStateOf<FriendEntry?>(null) }

Navigator(
    initialRoute = "main",
    swipeBackEnabled = true,
    handleBack = true,
    onEntryRemoved = { entry -> externalArgs.remove(entry.key) },
) { entry ->
    when (entry.route) {
        "main" -> MainTabHost(currentIndex, ...)
        "chat" -> {
            val args = externalArgs[entry.key] as? ChatArgs
            val channel = args?.channel ?: run { controller.forcePop(); return@Navigator }
            MessagePage(channel = channel, onBack = { controller.pop() }, ...)
        }
        "chat_settings" -> ChatSettingsPage(onBack = { controller.pop() }, ...)
        "video_preview" -> VideoPreviewPage(onClose = { controller.pop() }, ...)
        // ……
    }
}
```

「`WithSwipeBack` 包子页面」彻底消失——swipe 是 Navigator 的事。参数迁移留到 v2。

---

## 4. SwipeBack Transition 设计

### 4.1 渲染层结构

```
NavigatorRoot (Box, fillMaxSize)
  ├── Layer[previous]               ← 仅当 isTransitioning 时挂入树
  │     ├── content(previousEntry)  ← previous 页面 Composable
  │     └── Scrim (Color.Black α=scrimAlpha)
  └── Layer[current]                ← 永远存在
        └── Modifier.swipeBack(...)
              └── content(currentEntry)
```

- **平时** Composition 里只有 `current` 一层，跟现在等价
- previous 层挂载时机有两种候选：**edge-down 预挂载**（手指落在边缘且 `canPop` 时先挂入，但不 consume 手势）与 **Recognized 后挂载**（确认横向返回意图后再挂入）。Phase 0 必须对比两者在 Kuikly 真机上的首帧卡顿、内存和误触成本，再决定 v1 默认策略。
- 手势期间通过 `onProgress(progress, dragX)` 驱动：

```
current.translationX = dragX                                 // 跟手
previous.translationX = -screenWidth * 0.25f * (1f - progress)   // 视差，越拉越露
scrimAlpha = 0.15f * (1f - progress)                         // 上一页上的暗罩
```

- **松手判断**：复用 `Modifier.swipeBack` 内已有的 commit/cancel 逻辑
  - cancel：current → 0、previous → 起点的 spring 动画，结束后从 Composition 卸掉 previous 层
  - commit：current → screenWidth、previous → 0 的 tween 动画（~220ms），结束后真 `pop()`，卸掉**原 current**层

### 4.2 transition / presentation 表

| Transition | push 入场 | pop 出场 | edge swipe 可用 |
|---|---|---|---|
| `SlidePush`（默认） | current 从右滑入；previous 视差左移 | 镜像 | ✓ |
| `FadeIn` | current α 0→1 | α 1→0 | ✗（视差不成立） |
| `ModalSheet` | current 从下滑上；previous 不动 | 镜像 | ✗（手势冲突） |

| Presentation | 语义 | previous 是否保留 | edge swipe 可用 |
|---|---|---|---|
| `Push` | 普通页面入栈，微信式返回 | swipe/动画期间保留 top 2 | ✓（配合 `SlidePush`） |
| `Overlay` | 覆盖在来源页之上的沉浸式层，如图片/视频预览 | 来源页必须保留 | ✗ |
| `Modal` | 独立任务/表单式全屏弹层，关闭即回来源 | 来源页不跟随位移 | ✗ |

VIDEO_PREVIEW / IMAGE_PREVIEW 适合 `NavPresentation.Overlay` 或 `NavPresentation.Modal`，配 `FadeIn` / `ModalSheet`，不参与 edge swipe；其它普通页面默认 `Push + SlidePush`。

### 4.3 SaveableState（Navigator 内部实现细节）

Navigator 内部按 `entry.key` 维护一个 `SaveableStateHolder`，每个 entry 渲染时套 `SaveableStateProvider(entry.key)`，保证 previous 层挂入挂出 / push 后再 pop 回来时不丢 `rememberSaveable` 的状态（聊天页 LazyColumn 滚动位置、输入框 draft 等）。

移除策略：

- cancel 动画：保留 current 和 previous 的 saveable state，动画结束后只卸载 previous layer，不删除 previous state
- commit pop：动画期间保留 old current；动画结束后移除 old current entry，触发 `onEntryRemoved(oldCurrent)`，再删除 old current 的 saveable state
- replace/reset：被移除的 entries 逐个触发 `onEntryRemoved(entry)` 并删除对应 saveable state
- Navigator 必须保证每个被移除 entry 的 `onEntryRemoved(entry)` **exactly once**。commit 动画完成、replace、reset、异常清栈等路径不能重复通知同一个 entry，避免业务 external args 被重复清理或触发重复副作用。

**不公开**给业务：不出现在 `NavEntry` data class 上，调用方不感知 SaveableStateHolder 的存在，避免业务层误持有引用导致内存生命周期混乱。

---

## 5. Kuikly 集成边界

Navigator 不是孤立的 Compose 组件——它跑在 KuiklyUI Compose 之上，跟 native render / native event / Kuikly Page 模型都有耦合。**这一层接错了，未来 Kuikly 升版就会爆**。这一节列清现状 + 边界 + 必须做的 spike。

### 5.0 最新 KuiklyUI 源码对照结论

本轮已对照本地 `KuiklyUI/` 最新源码：

- Kuikly 已有 `com.tencent.kuikly.compose.material3.navigation.NavHost` / `NavHostController`，内部使用 `AnimatedContent`、`rememberSaveableStateHolder()`、`SaveableStateProvider(entry.id)`、`removeState(id)`、`transitionsInProgress` 和 entry lifecycle
- Kuikly 已有 `com.tencent.kuikly.compose.BackHandler`，并通过 `ComposeContainer` 提供 `LocalOnBackPressedDispatcherOwner`
- Kuikly core 的 `BackPressHandler` 本身就是 LIFO：`dispatchOnBackEvent()` 取 `backPressCallbackList.last()`
- Kuikly Android / iOS renderer 都通过 `onBackPressed` 事件把 native back 转给 Kotlin 侧，并能返回是否消费
- `graphicsLayer.translationX` 在 Kuikly Compose node/graphics 层有实现；`com.tencent.kuikly.compose.animation.core.Animatable` 也已存在

结论：Navigator v1 不需要重复造 Kuikly 已经提供的 back/state/lifecycle 基础设施；更稳的做法是参考 Kuikly `material3/navigation/NavHost` 的 entry state 清理和 back 接入方式，只新增 gearui-kit 需要的 edge-swipe interactive transition、`NavPresentation` 和 privchat 迁移所需的极简 API。

边界：Kuikly `NavHost` 是 entry state lifecycle 和 back integration 的参考实现，**不是** GearUI Navigator v1 的 public API surface。GearUI v1 的核心能力是微信式 edge swipe interactive pop，而不是普通 `AnimatedContent` 导航；因此最终仍需要 gearui 自己的 transition container。

### 5.1 当前集成形态（事实陈述）

- 整个 PrivChat 是**单 Kuikly Page**：`@Page("PrivChatApp") class PrivChatAppPage : View() extends com.gearui.View extends com.tencent.kuikly.compose.ComposeContainer`
- 28 个业务页面**全部活在同一个 Compose 树**里，靠 `when (currentPage)` 切换
- Android 侧 `MainActivity` 通过 `KuiklyRenderViewBaseDelegator` 起 Kuikly native render；`onResume`/`onPause`/`onDetach`/`onBackPressed` 全转发给 delegator
- `MainActivity.dispatchKeyEvent` 拦 `KEYCODE_BACK` → `kuiklyDelegator.onBackPressed()` → **不进 Compose 层**

### 5.2 Navigator 与 Kuikly Page 的关系（不变式）

- **Navigator 不替代 `@Page`**。@Page 是「一个 Kuikly native render surface」，Navigator 是「一个 Compose 树内部的栈」。Navigator 永远是 `PrivChatAppPage.Content()` 里的一个普通 Composable，**绝不**对应「多 Kuikly Page 用 native 栈切换」
- 单 Page 模型不变 → Kuikly Page lifecycle (`onResume` / `onPause` / `onDetach`) 跟 Navigator 内部的 entry 不同层，互不感知
- 后果：Navigator entry 切换**不会**触发 Kuikly native render reattach；动画/手势/render 完全在 Compose 层完成

### 5.3 系统 BACK 桥接（**不用** `androidx.activity.compose.BackHandler`）

本地最新版 KuiklyUI 已确认提供自己的 back 链路：

- `ComposeContainer` 通过 `LocalOnBackPressedDispatcherOwner provides this` 向 Compose 树提供 dispatcher
- `com.tencent.kuikly.compose.BackHandler` 使用 `DisposableEffect` 向 dispatcher 注册/注销 callback
- dispatcher 最终继承 `com.tencent.kuikly.core.base.BackPressHandler`
- `BackPressHandler.dispatchOnBackEvent()` 使用 `backPressCallbackList.last()`，天然是 LIFO
- Android `KuiklyRenderViewBaseDelegator.onBackPressed()` 会发送 `onBackPressed` 到 Kotlin 侧，并通过 `KRBackPressModule.isBackConsumed` 返回是否被消费
- iOS `KuiklyRenderViewControllerBaseDelegator.onBackPressedWithCompletion` 也发送同名 `onBackPressed` 事件，并通过 completion 返回消费结果

因此 v1 不需要从零自建一套 native bridge；`BackPressRouter` 应该是 gearui-kit 对 Kuikly back 链路的薄封装 / façade，底层优先复用 `com.tencent.kuikly.compose.BackHandler` 或 `LocalOnBackPressedDispatcherOwner.current.onBackPressedDispatcher`。

约束：

- **不要**依赖 `androidx.activity.compose.BackHandler` / AndroidX `LocalOnBackPressedDispatcherOwner`
- `BackPressRouter` 仍然必须保持 LIFO：后注册者优先吃返回。推荐层级为 **Dialog / Sheet / ActionSheet > Navigator top entry > Kuikly delegator**
- Composable consumer 必须用 `DisposableEffect` 注册/注销，确保页面卸载、Dialog 关闭、Navigator unmount 后不会残留 back consumer
- 当 `onPopRequest` 返回 `Pending` 时，Navigator 本次返回事件视为已消费；业务确认后调用 `forcePop()` 继续
- `MainActivity.dispatchKeyEvent` 继续调用 `kuiklyDelegator.onBackPressed()` 并尊重其 Boolean 返回值；Navigator 不应绕开 Kuikly delegator 直接接管 Android key event

### 5.4 手势事件分发（复用已验证的能力）

- `Modifier.swipeBack` 用 `pointerInput` + `awaitEachGesture` + `awaitHorizontalTouchSlopOrCancellation`——这三个在 Kuikly Compose 上**已经跑通**（gearui-kit/SwipeBack.kt 现实使用中）
- consume 时机：Recognized 之后才 `change.consume()`，避免抢子组件手势——这是当前已 audit 的策略，Navigator transition 容器**只**叠在 SwipeBack 之上，不重新设计 consume 模型
- Kuikly native 横向手势冲突历史坑：LazyColumn 横滑、ScrollView、SwipeCell（消息行的左滑「置顶/静音」）、Slider、地图、WebView
- 规避：**只做左边缘 24dp edge swipe，不做全屏 swipe**；个别页面允许通过 `NavOptions(swipeBackEnabled = false)` 关闭

需要禁用 swipe 的清单（初步）：

- `main`（栈底，没有可返回页）
- `video_preview` / `image_preview`（沉浸式，`Overlay` / `Modal`，走 ModalSheet/Fade）
- 未来的横向 Pager / WebView / 摄像头预览 / 全屏图片缩放

### 5.5 Compose Multiplatform 标准 API 的 Kuikly 可用性（必须 spike）

| 标准 Compose API | Navigator 用法 | Kuikly 实现度 | 必做 spike |
|---|---|---|---|
| `Modifier.pointerInput` / `awaitEachGesture` | 已用 (SwipeBack) | ✅ 现实跑通 | — |
| `Modifier.graphicsLayer { translationX = ... }` | transition 跟手 | ✅ 源码支持；⚠ 需验证大页面 60fps | ✅ Phase 0 |
| `com.tencent.kuikly.compose.animation.core.Animatable` + `spring/tween` | cancel 回弹 / commit 完成动画 | ✅ 源码支持；⚠ 需验证手势中断/回弹流畅度 | ✅ Phase 0 |
| `androidx.compose.runtime.saveable.SaveableStateHolder` | entry 状态保留 | ✅ Kuikly `NavHost` 已使用；⚠ 需验证本场景清理时机 | ✅ Phase 0 |
| `com.tencent.kuikly.compose.BackHandler` | back consumer 注册 | ✅ 源码支持 LIFO + DisposableEffect | ✅ Phase 0 |
| `androidx.activity.compose.BackHandler` | — | ❌ **不依赖** | — |
| `LocalDensity` / `LocalLayoutDirection` | transition 偏移换算 | ✅ 用过 | — |

> **Phase 0 仍必须真机跑 demo**：源码层面已经具备 API，但 Navigator 关注的是大页面过渡、手势中断、状态清理和 back 优先级的组合稳定性，不是单个 API 是否存在。

### 5.6 render 性能边界

- transition 期间 Composition 树挂着 2 个 entry（current + previous），翻倍 Kuikly virtual DOM diff
- 当前 ChatList / MessagePage 已经是几百到上千节点，transition 那一帧的 diff 成本是已知关注点
- v1 缓解策略：
  - previous 层挂载策略在 Phase 0 对比后确定：edge-down pre-mount 更顺滑但更吃内存；recognized mount 更省但可能首帧卡
  - previous 层禁用一切非视觉相关的 LaunchedEffect（用 freeze flag 在业务侧表达，或者 Navigator 自动 `CompositionLocalProvider(LocalIsForeground provides false)`）

这一条 Phase 0 spike 一起验证：模拟「会话列表 push 聊天页」的最坏情况，看 transition 60fps 是否能保持。

---

## 6. 迁移计划（privchat-app）

### Phase 0：Kuikly compatibility spike（**必须**先于 Phase 1）

在 gearui-kit/sample 里写一个最小 demo，**不实现 Navigator**，只把以下 5 件事在 Kuikly Compose 真机（Android + iOS）跑通：

1. `Modifier.graphicsLayer { translationX = dragX.value }` 跟随 SwipeBack 的 `onProgress` 平滑动起来，60fps
2. `com.tencent.kuikly.compose.animation.core.Animatable` + `animateTo(0f, spring())` 在 cancel 时把 translationX 回弹到 0，无掉帧
3. `SaveableStateHolder.SaveableStateProvider(key)` 包住一个 `rememberSaveable { mutableStateOf(...) }` 的 demo，挂入→卸出→重挂能保留值
4. 基于 Kuikly `BackHandler` / `BackPressHandler` 的 `BackPressRouter` façade：`MainActivity.dispatchKeyEvent` → `kuiklyDelegator.onBackPressed()` → Kuikly dispatcher → 一个注册了「back consumer」的 Composable 能正确吃掉 BACK 一次、再次按 BACK 时让出给 `kuiklyDelegator.onBackPressed()` 的默认分支
5. previous layer mount timing 对比：edge-down pre-mount vs recognized mount，记录首帧卡顿、误触成本、内存和大页面 diff 表现

任何一条不过 → 立即上报，**不**进 Phase 1。这些是 §5.5 表格里 ⚠ 的对应实测。

Phase 0 report 必须包含 failure fallback decision matrix。失败时先不实现 fallback，但必须判断下一步走哪条替代路线：

| 失败项 | fallback 方向 | 判定输出 |
|---|---|---|
| `graphicsLayer.translationX` 大页面不稳 / 掉帧 | 尝试 `offset` / layout translation fallback | 记录掉帧场景、节点规模、是否值得牺牲 layer transform |
| Kuikly `Animatable` 手势中断 / 回弹不稳 | 使用 frame clock / manual tween fallback | 记录 cancel/commit 的动画偏差和是否能手写可控 tween |
| `SaveableStateHolder` 状态保留 / 清理不稳 | entry 常驻 top2 或业务手动保存关键状态 | 记录丢失的状态类型、是否必须由业务层保存 |
| Kuikly `BackHandler` / LIFO 优先级不稳 | 退回 runtime `BackPressRouter` 自管 callback 链 | 记录冲突组件、注册顺序、是否需要完全绕开 Kuikly dispatcher |
| previous mount timing 两种策略都不稳 | 放弃 interactive previous layer，降级为非跟手 slide/fade | 记录是否仍保留 edge gesture，只在 commit 后执行动画 |

### Phase 1：Navigator 落地 + 1 个 spike 页面

- gearui-kit 实现 `Navigator` + `NavigatorController` + `EntryScope` + `NavTransition.SlidePush` + `BackPressRouter` 接入
- 复用现有 `Modifier.swipeBack`，加 `onProgress` 接到 transition 容器
- privchat-app 选 `main -> appearance` 做 spike：这两个 route **全部走 Navigator**，不做“旧 currentPage 管 main + Navigator 管子页面”的半套混合路由
- 验收：main push appearance，边缘滑能看到 main，能 cancel 回弹，能 commit pop，系统 BACK 能 pop appearance，栈底让出给 Kuikly delegator

### Phase 2：批量迁 push pages

- 把剩下 21 个 push page 全部从 `when (currentPage)` 抽出来挂到 Navigator
- `selectedX` 系列**继续保留**作为外层 state holder——typed params 是 v2 才做的事
- `resetToMain` → `nav.resetTo("main")`
- `MainPage` 内部的 bottom tab `currentIndex` 不动（不进 Navigator）

### Phase 3：Overlay / Modal 页面

- VIDEO_PREVIEW / IMAGE_PREVIEW 用 `NavPresentation.Overlay` 或 `NavPresentation.Modal`，配 `FadeIn` / `ModalSheet`，关闭 swipeBack
- ScanQrCode / 后续相机页面如有手势冲突也禁用 swipe

### Phase 4：清理

- 删 `PrivChatApp.kt` 里的 `MobilePage` enum / pageStack / pushPage/popPage/replacePage/resetToMain
- 删 `WithSwipeBack` thin wrapper
- 删 `tryAcquireNavLock`（Navigator 内部接管）
- Android BACK 走 Navigator 的 `BackPressRouter` 而非直接落到 `kuiklyDelegator.onBackPressed()`——这一步需要跟 SDK/Kuikly 那边对齐

### Phase 5（远期 v2）

- 引入 typed params 模型：`NavRoute<T>` / `NavArgs` / type-safe route builder
- 9 个 `selected*` state 收进 typed args
- 编辑页加 `onPopRequest(PopRequest): PopDecision` 实现 dirty check；用户确认后调用 `forcePop()`

---

## 7. 本轮不做的事

明确收口，避免 scope creep：

- ❌ **替代 Kuikly `@Page`**（Navigator 永远跑在单个 ComposeContainer 里，不引入多 Page 切换模型）
- ❌ **依赖 `androidx.activity.compose.BackHandler`** / AndroidX `LocalOnBackPressedDispatcherOwner`（Kuikly 已提供自己的 `com.tencent.kuikly.compose.BackHandler`，gearui-kit 只做 façade）
- ❌ deep link / App Link 路由
- ❌ nested navigator（tab 内 push）
- ❌ tab navigator（MAIN 内的 currentIndex 维持手写）
- ❌ route guard / 权限拦截框架（auth gate 维持顶层 if/else）
- ❌ state restoration（进程被杀后恢复栈）
- ❌ URL 化 route（仍是业务字符串 dispatch key）
- ❌ **typed params**（`Map<String, Any?>` / `NavRoute<T>` / `NavArgs` 全部留到 v2）
- ❌ 复杂 result passing（push 后回传值）
- ❌ 公开 SaveableStateHolder 给业务（Navigator 内部实现）
- ❌ 全屏 swipe back
- ❌ 多套 transition 动画曲线（只先做 SlidePush 一条）
- ❌ 直接动业务代码（本轮**只写本文档**）

---

## 8. 决策记录

| 决策点 | 选择 | 理由 |
|---|---|---|
| Navigator 归属 | gearui-kit | 通用导航容器能力，跟 Avatar/Button 同档 |
| 栈结构 | 真栈（List<NavEntry>） | 已有 pageStack 模型对得上 |
| route 标识 | String | 简单可控；type-safe builder 留到 v2 |
| entry key | Navigator 默认生成唯一 key；业务可显式传稳定 key | 避免 `key = route` 导致同 route 多次 push 时 SaveableState 串页 |
| 参数传递 | **v1 不公开 typed params，但提供 `onEntryRemoved`** | gearui-kit 不暴露 `Map<String, Any?>`；业务用 `externalArgs[entry.key]` / selectedX 桥接，entry 移除时清理，避免参数污染 |
| SaveableStateHolder | **Navigator 内部实现** | 不放进 `NavEntry`，避免业务误持有；按 `entry.key` 自管 `SaveableStateProvider`，entry 最终移除后删除对应 state |
| swipe back 实现 | 复用 `Modifier.swipeBack`，加 transition 容器 | 手势识别已经成熟，只缺渲染层 |
| swipe 范围 | 只左边缘 24dp | 避开 Kuikly 全屏手势冲突历史坑 |
| 系统返回接管 | `handleBack: Boolean` + gearui-kit/runtime `BackPressRouter` façade | **不**走 `androidx.activity.compose.BackHandler`；底层复用 Kuikly `BackHandler` / `BackPressHandler`，按 LIFO 注册，Dialog/Sheet/ActionSheet > Navigator top entry > Kuikly delegator |
| pop 拦截 | `onPopRequest(PopRequest): PopDecision` + `forcePop()` | 支持同步放行/拒绝，也支持 dirty 弹窗这类 Pending 后确认继续 |
| Navigator vs Kuikly `@Page` | Navigator 是 Compose 树内部栈，不替代 `@Page` | 维持单 Kuikly Page 模型；Navigator entry 切换不触发 Kuikly native render reattach |
| Presentation | `Push` / `Overlay` / `Modal` | 普通页面、沉浸式预览、全屏模态分别表达；Overlay/Modal 不参与 edge swipe |
| Kuikly 兼容验证 | **Phase 0 必跑** | `graphicsLayer.translationX` / Kuikly `Animatable` / `SaveableStateHolder` / Kuikly BackHandler façade / previous mount timing 必须先在 sample 真机验过，再开 Phase 1 |
| auth gate | 不进 Navigator | 维持现状，gate 是栈外概念 |

---

## 9. 验收标准

### Phase 0 done

- gearui-kit/sample 跑通 §6 Phase 0 列的 5 件套（translationX / Kuikly Animatable / SaveableStateHolder / Kuikly BackHandler façade / previous mount timing）
- Android + iOS 真机各跑一遍，无掉帧、无状态丢失、BACK 正确二段让出
- previous layer 的 edge-down pre-mount vs recognized mount 对比结果写回本文档，并明确 v1 默认策略
- Phase 0 report 写出 failure fallback decision matrix；即使全部通过，也要记录 fallback 是否仍需保留为实现预案
- 若任一不过：**停在 Phase 0**，把不通过的现象 + 日志补回本文档，再决定是绕路还是等 Kuikly 升级

### Phase 1 done

- gearui-kit 增加 `Navigator.kt` + 相关 API，编译通过
- `main -> appearance` 两个 route 全走 Navigator；不保留旧 `currentPage` 与 Navigator 混合驱动这条链路
- APPEARANCE 边缘右滑能看到 MAIN 露出来；中途松手回弹；过阈值 / 速度 commit 弹出
- 系统 BACK 走 `BackPressRouter` 正确触发 pop；栈底时让出给 `kuiklyDelegator.onBackPressed()`
- Dialog/Sheet/ActionSheet back consumer 注册在 Navigator 之后时，按 LIFO 优先消费；Composable 卸载后 consumer 被注销
- `onEntryRemoved(entry)` 对每个 removed entry exactly once；commit pop / replace / reset 路径都覆盖
- `PopDecision.Pending` 不保存 continuation；业务确认后调用 `forcePop()` 才继续 pop，取消则保持当前页面
- 没有出现 swipe 跟 ChatList 左滑/SwipeCell 手势冲突
- `Navigator` API 表上方法在 spike 页面全部实测过：push / pop / forcePop / canPop / resetTo / onEntryRemoved
- Android + iOS 60fps，无明显掉帧
- 文档 docs/NAVIGATOR_SWIPE_BACK_DESIGN.md 标 Phase 1 完成

---

## 10. 参考

- 当前路由实现：`privchat-app/privchat/src/commonMain/kotlin/com/netonstream/privchat/app/PrivChatApp.kt` 1106–2120
- 当前手势识别：`gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/gestures/SwipeBack.kt`
- Android BACK 桥：`privchat-app/privchat/src/androidMain/kotlin/com/netonstream/privchat/app/MainActivity.kt` 215
- Kuikly BackHandler：`KuiklyUI/compose/src/commonMain/kotlin/com/tencent/kuikly/compose/BackHandler.kt`
- Kuikly BackPressHandler：`KuiklyUI/core/src/commonMain/kotlin/com/tencent/kuikly/core/base/BackPressHandler.kt`
- Kuikly ComposeContainer locals：`KuiklyUI/compose/src/commonMain/kotlin/com/tencent/kuikly/compose/ComposeContainer.kt`
- Kuikly material3 NavHost 参考：`KuiklyUI/compose/src/commonMain/kotlin/com/tencent/kuikly/compose/material3/navigation/NavHost.kt`
- Kuikly Android back event：`KuiklyUI/core-render-android/src/main/java/com/tencent/kuikly/core/render/android/expand/KuiklyRenderViewBaseDelegator.kt`
- Kuikly iOS back event：`KuiklyUI/core-render-ios/Extension/KuiklyRenderViewControllerBaseDelegator.m`
