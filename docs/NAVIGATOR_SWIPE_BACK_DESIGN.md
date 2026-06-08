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
→ Navigator v1 必须解决「参数随 entry 走，pop 自动清」。

### 1.6 nav lock 没普及

`tryAcquireNavLock(450ms)` 只在 `onChatBack`（行 1277）用了一次；其它 28 个 pop 都没保护。多指快速戳「返回」可以触发双 pop。Navigator v1 应该统一接管。

### 1.7 Android BACK 键

`MainActivity.dispatchKeyEvent`（行 215）把 BACK 转给 `kuiklyDelegator.onBackPressed()`，**不进** Compose 层。也就是说：

- 系统 BACK 当前**不会**触发 popPage——只能点 NavBar 左上的箭头或在已 wrap 的页面边缘滑
- 这是个独立的 gap，Navigator v1 需要 own Android BACK

### 1.8 编辑页 dirty 拦截

`EDIT_REMARK` / `PROFILE_NICKNAME` / `PROFILE_USERNAME` 这些都是直接 `onBack = { popPage() }`，没有「未保存提示」拦截。Navigator v1 不强加，但 **API 要预留 `interceptPop` 钩子**。

---

## 2. 页面类型分类

| 类别 | 页面 | 当前包 SwipeBack | 进 pageStack | 备注 |
|---|---|---|---|---|
| **Tab root** | MAIN | 不应 | 是（栈底） | 内部 currentIndex 切 3 个 tab |
| **Push page**（22 个） | CHAT / CHAT_SETTINGS / GROUP_* (5) / FRIEND_* (3) / EDIT_REMARK / APPEARANCE / PROFILE_* (6) / SWITCH_ACCOUNT / MY_QR_CODE / SCAN_QR_CODE / FORWARD_PICKER / SEARCH_USER / FRIEND_REQUEST / USER_PROFILE | 全部 `WithSwipeBack` 包了 | 是 | 默认行为：边缘滑可返回 |
| **Modal-like fullscreen** | VIDEO_PREVIEW / IMAGE_PREVIEW | **没**包（沉浸式） | 是 | pop 时回 CHAT，不回栈顶 |
| **Auth gate**（栈外） | startupBooting splash / LoginPage / RequiredActionFlow / loadingMessage | 不应 | 否 | 在 PrivChatApp 顶层 if/else，**没进** MobileLayout |

Navigator v1 需要表达这三档：**root / push / modal**。Auth gate 不进 Navigator，保持顶层 gate。

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
    content: @Composable NavigatorScope.(NavEntry) -> Unit,
)
```

- `initialRoute`：栈底 route 字符串
- `swipeBackEnabled`：全局开关；个别 entry 还能在 `NavOptions` 里再禁用
- `handleBack`：是否接管系统返回（Android BACK / iOS edge / 桌面 Esc）。**平台无关命名**——内部如何对接 Android `BackHandler` / Kuikly native back / iOS gesture 是 Runtime 适配层的事，调用方不感知
- `content`：根据当前 entry 渲染对应页面；**这一份 lambda 会被 entry list 里的每一个 entry 各自调用一遍**（top 2 entry 同时活在 Composition）

### 3.2 NavigatorScope

```kotlin
interface NavigatorScope {
    val current: NavEntry
    val previous: NavEntry?
    val canPop: Boolean                  // = previous != null
    val isTransitioning: Boolean         // swipe 中或动画中，业务可借此 disable 输入

    fun push(route: String, options: NavOptions = NavOptions.Default)
    fun pop(): Boolean                   // 真 pop；false 表示栈底拦不动
    fun popTo(route: String): Boolean    // 弹到指定 route（resetToMain 用）
    fun replace(route: String, options: NavOptions = NavOptions.Default)
    fun resetTo(route: String)           // 清栈 + push（对齐当前 resetToMain）
}

data class NavOptions(
    val swipeBackEnabled: Boolean = true,
    val transition: NavTransition = NavTransition.SlidePush,
    val interceptPop: (() -> Boolean)? = null,   // true = 吃掉这次 pop（dirty check 钩子）
) {
    companion object { val Default = NavOptions() }
}

enum class NavTransition { SlidePush, FadeIn, ModalSheet }
```

### 3.3 NavEntry（无 params 版）

```kotlin
data class NavEntry(
    val route: String,                              // 业务 dispatch key（"chat", "profile_edit"）
    val key: String = route,                        // entry 唯一身份，默认 = route；同 route 多次 push 时调用方负责传不同 key
    val options: NavOptions = NavOptions.Default,
)
```

**v1 不公开参数模型**。理由：

- gearui-kit 是通用组件库，公开 `Map<String, Any?>` 会留下类型不安全、KMP/Native 调试坑、state restore 收口困难
- 当前 privchat-app 已经用 9 个 outer `selectedX` state 桥接参数，**v1 直接保留**这个模式，只换路由层
- v2 再做 `NavRoute<T>` / `NavArgs` / type-safe route builder，那时才把 `selectedX` 收进 typed args

**SaveableStateHolder 不放在 NavEntry 里**——是 Navigator 内部实现细节，按 `entry.key` 维护 `SaveableStateProvider(entry.key)`，外部不感知。

### 3.4 typical 用法（迁后的 PrivChatApp，v1 风格）

```kotlin
// 参数暂时继续放在 outer state，参考当前 PrivChatApp.kt 的 selectedX 模式
var selectedChannel by remember { mutableStateOf<ChannelListEntry?>(null) }
var selectedFriend by remember { mutableStateOf<FriendEntry?>(null) }

Navigator(
    initialRoute = "main",
    swipeBackEnabled = true,
    handleBack = true,
) { entry ->
    when (entry.route) {
        "main" -> MainTabHost(currentIndex, ...)
        "chat" -> {
            val channel = selectedChannel ?: run { pop(); return@Navigator }
            MessagePage(channel = channel, onBack = ::pop, ...)
        }
        "chat_settings" -> ChatSettingsPage(onBack = ::pop, ...)
        "video_preview" -> VideoPreviewPage(onClose = ::pop, ...)
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
- **手势 Recognized 那一刻** 把 `previous` 层加进 Composition（懒挂载，控制内存）
- 手势期间通过 `onProgress(progress, dragX)` 驱动：

```
current.translationX = dragX                                 // 跟手
previous.translationX = -screenWidth * 0.25f * (1f - progress)   // 视差，越拉越露
scrimAlpha = 0.15f * (1f - progress)                         // 上一页上的暗罩
```

- **松手判断**：复用 `Modifier.swipeBack` 内已有的 commit/cancel 逻辑
  - cancel：current → 0、previous → 起点的 spring 动画，结束后从 Composition 卸掉 previous 层
  - commit：current → screenWidth、previous → 0 的 tween 动画（~220ms），结束后真 `pop()`，卸掉**原 current**层

### 4.2 transition 表

| Transition | push 入场 | pop 出场 | edge swipe 可用 |
|---|---|---|---|
| `SlidePush`（默认） | current 从右滑入；previous 视差左移 | 镜像 | ✓ |
| `FadeIn` | current α 0→1 | α 1→0 | ✗（视差不成立） |
| `ModalSheet` | current 从下滑上；previous 不动 | 镜像 | ✗（手势冲突） |

VIDEO_PREVIEW / IMAGE_PREVIEW 适合 `FadeIn` 或 `ModalSheet`；其它默认 `SlidePush`。

### 4.3 SaveableState（Navigator 内部实现细节）

Navigator 内部按 `entry.key` 维护一个 `SaveableStateHolder`，每个 entry 渲染时套 `SaveableStateProvider(entry.key)`，保证 previous 层挂入挂出 / push 后再 pop 回来时不丢 `rememberSaveable` 的状态（聊天页 LazyColumn 滚动位置、输入框 draft 等）。

**不公开**给业务：不出现在 `NavEntry` data class 上，调用方不感知 SaveableStateHolder 的存在，避免业务层误持有引用导致内存生命周期混乱。

---

## 5. Kuikly 集成边界

Navigator 不是孤立的 Compose 组件——它跑在 KuiklyUI Compose 之上，跟 native render / native event / Kuikly Page 模型都有耦合。**这一层接错了，未来 Kuikly 升版就会爆**。这一节列清现状 + 边界 + 必须做的 spike。

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

- Compose Multiplatform 标准 `BackHandler` 依赖 `LocalOnBackPressedDispatcherOwner`，Kuikly Compose 是否提供这个 owner **未确认**，不能赌
- v1 改走 Kuikly 已有桥：在 `gearui-kit/runtime` 层加一个轻量 back router（`BackPressRouter`），`Navigator` 启动时把自己注册进去；`MainActivity.dispatchKeyEvent` 先问 router「有 entry 要 pop 吗？」，否则才落到 `kuiklyDelegator.onBackPressed()`
- iOS / 桌面 / 鸿蒙的 BACK 等价事件由各自 runtime 适配层注入同一个 router
- 这样 `Navigator(handleBack = true)` API 表面**完全平台无关**，业务调用方零感知

### 5.4 手势事件分发（复用已验证的能力）

- `Modifier.swipeBack` 用 `pointerInput` + `awaitEachGesture` + `awaitHorizontalTouchSlopOrCancellation`——这三个在 Kuikly Compose 上**已经跑通**（gearui-kit/SwipeBack.kt 现实使用中）
- consume 时机：Recognized 之后才 `change.consume()`，避免抢子组件手势——这是当前已 audit 的策略，Navigator transition 容器**只**叠在 SwipeBack 之上，不重新设计 consume 模型
- Kuikly native 横向手势冲突历史坑：LazyColumn 横滑、ScrollView、SwipeCell（消息行的左滑「置顶/静音」）、Slider、地图、WebView
- 规避：**只做左边缘 24dp edge swipe，不做全屏 swipe**；个别页面允许通过 `NavOptions(swipeBackEnabled = false)` 关闭

需要禁用 swipe 的清单（初步）：

- `main`（栈底，没有可返回页）
- `video_preview` / `image_preview`（沉浸式，走 ModalSheet/Fade）
- 未来的横向 Pager / WebView / 摄像头预览 / 全屏图片缩放

### 5.5 Compose Multiplatform 标准 API 的 Kuikly 可用性（必须 spike）

| 标准 Compose API | Navigator 用法 | Kuikly 实现度 | 必做 spike |
|---|---|---|---|
| `Modifier.pointerInput` / `awaitEachGesture` | 已用 (SwipeBack) | ✅ 现实跑通 | — |
| `Modifier.graphicsLayer { translationX = ... }` | transition 跟手 | ⚠ 未验证 60fps native | ✅ Phase 0 |
| `androidx.compose.animation.core.Animatable<Float>` + `spring/tween` | cancel 回弹 / commit 完成动画 | ⚠ 未验证 | ✅ Phase 0 |
| `androidx.compose.runtime.saveable.SaveableStateHolder` | entry 状态保留 | ⚠ 未验证 | ✅ Phase 0 |
| `androidx.activity.compose.BackHandler` | — | ❌ **不依赖** | — |
| `LocalDensity` / `LocalLayoutDirection` | transition 偏移换算 | ✅ 用过 | — |

> **Phase 0 必须先把上面 ⚠ 三条在 Kuikly 真机上跑过 demo**，再开 Phase 1 Navigator 实现——否则会出现「Compose 代码看着对，Kuikly 渲染抖 / 状态不保留 / cancel 不回弹」这类 native 层 bug。

### 5.6 render 性能边界

- transition 期间 Composition 树挂着 2 个 entry（current + previous），翻倍 Kuikly virtual DOM diff
- 当前 ChatList / MessagePage 已经是几百到上千节点，transition 那一帧的 diff 成本是已知关注点
- v1 缓解策略：
  - previous 层**仅在 Recognized 那一刻挂入**，cancel 后立即卸掉
  - previous 层禁用一切非视觉相关的 LaunchedEffect（用 freeze flag 在业务侧表达，或者 Navigator 自动 `CompositionLocalProvider(LocalIsForeground provides false)`）

这一条 Phase 0 spike 一起验证：模拟「会话列表 push 聊天页」的最坏情况，看 transition 60fps 是否能保持。

---

## 6. 迁移计划（privchat-app）

### Phase 0：Kuikly compatibility spike（**必须**先于 Phase 1）

在 gearui-kit/sample 里写一个最小 demo，**不实现 Navigator**，只把以下 4 件事在 Kuikly Compose 真机（Android + iOS）跑通：

1. `Modifier.graphicsLayer { translationX = dragX.value }` 跟随 SwipeBack 的 `onProgress` 平滑动起来，60fps
2. `Animatable<Float>` + `animateTo(0f, spring())` 在 cancel 时把 translationX 回弹到 0，无掉帧
3. `SaveableStateHolder.SaveableStateProvider(key)` 包住一个 `rememberSaveable { mutableStateOf(...) }` 的 demo，挂入→卸出→重挂能保留值
4. 自建 `BackPressRouter`：MainActivity dispatchKeyEvent → router → 一个注册了「back consumer」的 Composable 能正确吃掉 BACK 一次、再次按 BACK 时让出给 `kuiklyDelegator.onBackPressed()`

任何一条不过 → 立即上报，**不**进 Phase 1。这些是 §5.5 表格里 ⚠ 的对应实测。

### Phase 1：Navigator 落地 + 1 个 spike 页面

- gearui-kit 实现 `Navigator` + `NavigatorScope` + `NavTransition.SlidePush` + 内置 BackHandler
- 复用现有 `Modifier.swipeBack`，加 `onProgress` 接到 transition 容器
- privchat-app 选 **1 个最简单的 push page**（如 `APPEARANCE`）作 spike：从 `MobilePage` 隔离出来，单独走 Navigator
- 验收：能滑、能看到上一页、能 cancel 回弹、能 commit pop

### Phase 2：批量迁 push pages

- 把剩下 21 个 push page 全部从 `when (currentPage)` 抽出来挂到 Navigator
- `selectedX` 系列**继续保留**作为外层 state holder——typed params 是 v2 才做的事
- `resetToMain` → `nav.resetTo("main")`
- `MainPage` 内部的 bottom tab `currentIndex` 不动（不进 Navigator）

### Phase 3：modal-like 页面

- VIDEO_PREVIEW / IMAGE_PREVIEW 用 `NavTransition.ModalSheet`，关闭 swipeBack
- ScanQrCode / 后续相机页面如有手势冲突也禁用 swipe

### Phase 4：清理

- 删 `PrivChatApp.kt` 里的 `MobilePage` enum / pageStack / pushPage/popPage/replacePage/resetToMain
- 删 `WithSwipeBack` thin wrapper
- 删 `tryAcquireNavLock`（Navigator 内部接管）
- Android BACK 走 Navigator 的 BackHandler 而非 kuiklyDelegator.onBackPressed()——这一步需要跟 SDK/Kuikly 那边对齐

### Phase 5（远期 v2）

- 引入 typed params 模型：`NavRoute<T>` / `NavArgs` / type-safe route builder
- 9 个 `selected*` state 收进 typed args
- 编辑页加 `interceptPop` 实现 dirty check

---

## 7. 本轮不做的事

明确收口，避免 scope creep：

- ❌ **替代 Kuikly `@Page`**（Navigator 永远跑在单个 ComposeContainer 里，不引入多 Page 切换模型）
- ❌ **依赖 `androidx.activity.compose.BackHandler`** / `LocalOnBackPressedDispatcherOwner`（Kuikly Compose 未确认提供，改用 gearui-kit/runtime 的 BackPressRouter 桥）
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
| 参数传递 | **v1 不公开** | gearui-kit 是通用库，公开 `Map<String, Any?>` 会留下类型不安全 / KMP 调试 / state restore 收口困难三个长期坑。v1 业务自己用 outer state holder 桥（已有的 `selectedX` 直接复用） |
| SaveableStateHolder | **Navigator 内部实现** | 不放进 `NavEntry`，避免业务误持有；按 `entry.key` 自管 `SaveableStateProvider` |
| swipe back 实现 | 复用 `Modifier.swipeBack`，加 transition 容器 | 手势识别已经成熟，只缺渲染层 |
| swipe 范围 | 只左边缘 24dp | 避开 Kuikly 全屏手势冲突历史坑 |
| 系统返回接管 | `handleBack: Boolean` + gearui-kit/runtime `BackPressRouter` | **不**走 `androidx.activity.compose.BackHandler`，Kuikly Compose 未确认提供 OnBackPressedDispatcherOwner；改在 runtime 层桥接 native back → router → 注册的 Composable consumer |
| Navigator vs Kuikly `@Page` | Navigator 是 Compose 树内部栈，不替代 `@Page` | 维持单 Kuikly Page 模型；Navigator entry 切换不触发 Kuikly native render reattach |
| Kuikly 兼容验证 | **Phase 0 必跑** | `graphicsLayer.translationX` / `Animatable` / `SaveableStateHolder` / 自建 `BackPressRouter` 四件套必须先在 sample 真机验过，再开 Phase 1 |
| auth gate | 不进 Navigator | 维持现状，gate 是栈外概念 |

---

## 9. 验收标准

### Phase 0 done

- gearui-kit/sample 跑通 §6 Phase 0 列的 4 件套（translationX / Animatable / SaveableStateHolder / BackPressRouter）
- Android + iOS 真机各跑一遍，无掉帧、无状态丢失、BACK 正确二段让出
- 若任一不过：**停在 Phase 0**，把不通过的现象 + 日志补回本文档，再决定是绕路还是等 Kuikly 升级

### Phase 1 done

- gearui-kit 增加 `Navigator.kt` + 相关 API，编译通过
- 选定 spike 页面（如 APPEARANCE）：边缘右滑能看到 MAIN 露出来；中途松手回弹；过阈值 / 速度 commit 弹出
- 系统 BACK 走 `BackPressRouter` 正确触发 pop；栈底时让出给 `kuiklyDelegator.onBackPressed()`
- 没有出现 swipe 跟 ChatList 左滑/SwipeCell 手势冲突
- `Navigator` API 表上方法在 spike 页面全部实测过：push / pop / canPop / resetTo
- Android + iOS 60fps，无明显掉帧
- 文档 docs/NAVIGATOR_SWIPE_BACK_DESIGN.md 标 Phase 1 完成

---

## 10. 参考

- 当前路由实现：`privchat-app/privchat/src/commonMain/kotlin/com/netonstream/privchat/app/PrivChatApp.kt` 1106–2120
- 当前手势识别：`gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/gestures/SwipeBack.kt`
- Android BACK 桥：`privchat-app/privchat/src/androidMain/kotlin/com/netonstream/privchat/app/MainActivity.kt` 215
