# I18n 分层架构 - 接入指南

> 面向**依赖 gearui-kit 的上层库**（`privchat-ui`、业务 SDK、品牌主题包等），说明如何接入 GearUI Kit 的语言运行时并提供本库自己的强类型 strings。
>
> 业务应用通常不需要读本文——`GearApp(languageTag = ...)` 一处声明语言即可。

## 设计原则

GearUI Kit 不集中托管所有库的语言包。每个库**自己定义**强类型 strings，**共享** GearUI Kit 提供的语言环境：

```
GearApp (languageTag, fallbackLanguageTag, stringsOverrides)
  └── I18nRoot                                                  ← gearui-kit 提供
        ├── LocalLanguageTag         (BCP47, normalized)
        └── LocalFallbackLanguageTag (BCP47, normalized)
              ├── I18nProvider   (LocalStrings)         ← gearui-kit 自己用
              ├── PrivChatUiI18nProvider (LocalPrivChatUiStrings) ← 上层库自己挂
              ├── ...                                           ← 任意层
              └── content
```

每个 Provider：
- 读 `LocalLanguageTag.current` / `LocalFallbackLanguageTag.current`
- 从自己的 builtin map 解析 base pack
- 应用 caller 传入的 `Map<String, XxxPatch>` 字段级覆盖
- 用 `remember(tag, fallback, overrides)` 缓存到 immutable data class
- 通过 `staticCompositionLocalOf` 暴露给同库组件

类型安全 + 字段级覆盖 + 单点语言切换 + 应用层零样板。

## 接入步骤

下面以一个虚构的 `privchat-ui` 库为例。

### 1. 定义 strings 数据模型

```kotlin
package com.privchat.ui.i18n

import androidx.compose.runtime.Immutable

@Immutable
data class PrivChatUiStrings(
    val messageDeleted: String,
    val chatTyping: String,
    val replyExpired: String,
    // ...
)
```

### 2. 定义 patch（字段级覆盖）

```kotlin
@Immutable
data class PrivChatUiStringsPatch(
    val messageDeleted: String? = null,
    val chatTyping: String? = null,
    val replyExpired: String? = null,
)

val PrivChatUiStringsPatch.isEmpty: Boolean
    get() = messageDeleted == null && chatTyping == null && replyExpired == null

fun PrivChatUiStrings.merge(patch: PrivChatUiStringsPatch?): PrivChatUiStrings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        messageDeleted = patch.messageDeleted ?: messageDeleted,
        chatTyping = patch.chatTyping ?: chatTyping,
        replyExpired = patch.replyExpired ?: replyExpired,
    )
}
```

### 3. 内置语言包

```kotlin
val PrivChatUiStringsEnUs = PrivChatUiStrings(
    messageDeleted = "Message deleted",
    chatTyping = "Typing...",
    replyExpired = "Original message no longer available",
)

val PrivChatUiStringsZhHans = PrivChatUiStrings(
    messageDeleted = "消息已撤回",
    chatTyping = "正在输入...",
    replyExpired = "原消息已失效",
)

object PrivChatUiStringPacks {
    val English: PrivChatUiStrings = PrivChatUiStringsEnUs
    val ChineseSimplified: PrivChatUiStrings = PrivChatUiStringsZhHans

    val builtIn: Map<String, PrivChatUiStrings> = mapOf(
        "en-US" to English,
        "en" to English,
        "zh-Hans" to ChineseSimplified,
        "zh-CN" to ChineseSimplified,
        "zh" to ChineseSimplified,
    )
}
```

### 4. Provider + accessor（**核心**）

```kotlin
import com.gearui.i18n.LocalFallbackLanguageTag
import com.gearui.i18n.LocalLanguageTag
import com.gearui.i18n.normalizeLanguageTag
import com.gearui.i18n.resolveLanguagePack

val LocalPrivChatUiStrings = staticCompositionLocalOf { PrivChatUiStringPacks.English }

@Composable
fun PrivChatUiI18nProvider(
    overrides: Map<String, PrivChatUiStringsPatch> = emptyMap(),
    content: @Composable () -> Unit,
) {
    val tag = LocalLanguageTag.current             // ← 来自 GearApp / I18nRoot
    val fallback = LocalFallbackLanguageTag.current
    val strings = remember(tag, fallback, overrides) {
        val base = resolveLanguagePack(
            languageTag = tag,
            packs = PrivChatUiStringPacks.builtIn,
            defaultTag = fallback,
        )
        base.merge(resolvePatch(tag, fallback, overrides))
    }
    CompositionLocalProvider(LocalPrivChatUiStrings provides strings, content = content)
}

object PrivChatUiI18n {
    val strings: PrivChatUiStrings
        @Composable get() = LocalPrivChatUiStrings.current
}

private fun resolvePatch(
    languageTag: String,
    fallbackTag: String,
    overrides: Map<String, PrivChatUiStringsPatch>,
): PrivChatUiStringsPatch? {
    if (overrides.isEmpty()) return null
    val normalized = overrides.entries.associate { normalizeLanguageTag(it.key) to it.value }

    val candidates = mutableListOf<String>()
    var current = languageTag
    while (true) {
        candidates += current
        val idx = current.lastIndexOf('-')
        if (idx <= 0) break
        current = current.substring(0, idx)
    }
    if (!candidates.contains(fallbackTag)) candidates += fallbackTag

    for (c in candidates) {
        normalized[c]?.let { if (!it.isEmpty) return it }
    }
    return null
}
```

### 5. 在组件里使用

```kotlin
@Composable
fun ChatBubble(message: ChatMessage) {
    val strings = PrivChatUiI18n.strings
    Text(text = if (message.deleted) strings.messageDeleted else message.body)
}
```

### 6. 应用层挂载

```kotlin
@Composable
fun App() {
    val lang by appPrefs.language.collectAsState()

    GearApp(
        languageTag = lang,
        // 可选：覆盖 GearUI 内置文案
        stringsOverrides = mapOf(
            "zh-Hans" to StringsPatch(buttonConfirm = "确定一下"),
        ),
    ) {
        // 可选：覆盖 privchat-ui 内置文案
        PrivChatUiI18nProvider(
            overrides = mapOf(
                "zh-Hans" to PrivChatUiStringsPatch(messageDeleted = "这条消息已撤回"),
            ),
        ) {
            AppContent()
        }
    }
}
```

应用层只在**一处**声明 `languageTag`；切换时 GearUI Kit、privchat-ui、业务自身 strings 全部自动重组。

## 命名约定（强制）

| 角色 | 命名 |
|---|---|
| 数据模型 | `XxxStrings`（data class，全字段非 null） |
| 覆盖模型 | `XxxStringsPatch`（data class，全字段 nullable） |
| 内置语言常量集 | `XxxStringPacks`（object，含 `English` / `ChineseSimplified` / ... + `builtIn: Map<String, XxxStrings>`） |
| 单语言常量 | `XxxStringsEnUs` / `XxxStringsZhHans` / `XxxStringsZhHant` |
| CompositionLocal | `LocalXxxStrings`（`staticCompositionLocalOf { XxxStringPacks.English }`） |
| Provider | `XxxI18nProvider(overrides, content)` |
| 访问器 | `object XxxI18n { val strings: XxxStrings @Composable get }` |

跨库一致命名让消费者一眼能找到接入点。

## 性能要求（强制）

- ✅ `remember(tag, fallback, overrides)` 缓存解析结果
- ✅ `merge(patch)` 在 patch 为 null / 全 null 时直接返回 receiver，**零分配**
- ✅ 组件读取 `XxxI18n.strings` 是 O(1)（CompositionLocal lookup）
- ❌ 不要在每次组件重组时调用 `resolveLanguagePack` / `normalizeLanguageTag`
- ❌ 不要把 strings 解析放进 `LaunchedEffect` 或副作用——是纯计算

## 反模式

| ❌ 错误 | ✅ 正确 |
|---|---|
| 自己再做一个 `LocalLanguageTag` | 直接读 `com.gearui.i18n.LocalLanguageTag` |
| 应用层在 `GearApp` 后再 `I18nRoot(...)` 嵌套一层 | `GearApp(languageTag = ...)` 已经挂了 |
| 接收 `languageTag` 作为 Provider 参数 | 只接收 `overrides`，语言从 Local 读 |
| 用 `Map<String, String>` + 字符串 key 访问 | 强类型 data class + accessor |
| 把 strings 放进 `MutableState` 让组件订阅 | `staticCompositionLocalOf` + `remember` |

## SPEC 关联

- `GEARUI_SPEC_2026.md` §12 I18n 分层架构（约束规范）
- 删除 / 重命名 strings 字段 = API breaking，受 `binary-compatibility-validator` 阻断
- 新增 strings 字段 = source breaking（构造函数参数变多），需配套升级所有 builtin pack 文件
