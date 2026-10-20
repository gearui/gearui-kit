# The app's own strings

The kit ships its own copy ("Confirm", "Cancel", picker titles) and switches it with
`App(languageTag = …)`. The app's copy follows the same language through the app's own
typed pack. Language is set in one place; nothing else in the app passes a language tag.

Why typed packs and not `Map<String, String>` or resources: a missing key is a compile
error instead of blank text on one language, the IDE finds every use, and the same code
runs on all four platforms with no resource pipeline.

## The pattern

```kotlin
package myapp.i18n

import androidx.compose.runtime.*
import com.gearui.i18n.LocalFallbackLanguageTag
import com.gearui.i18n.LocalLanguageTag
import com.gearui.i18n.resolveLanguagePack

// 1. One small data class per screen or feature ("domain"). Keep each to about a dozen
//    fields: one giant class hits Android's 255-constructor-parameter and DEX limits and
//    fails with a VerifyError on Android only.
@Immutable
data class SettingsStrings(
    val title: String,
    val general: String,
    val profile: String,
    val notifications: String,
)

@Immutable
data class TabStrings(val home: String, val discover: String, val me: String)

@Immutable
data class AppStrings(
    val settings: SettingsStrings,
    val tabs: TabStrings,
)

// 2. One constant per language. Every language fills every field — the compiler checks it.
val AppStringsEnUs = AppStrings(
    settings = SettingsStrings(title = "Settings", general = "General", profile = "Profile", notifications = "Notifications"),
    tabs = TabStrings(home = "Home", discover = "Discover", me = "Me"),
)
val AppStringsZhHans = AppStrings(
    settings = SettingsStrings(title = "设置", general = "通用", profile = "个人资料", notifications = "通知"),
    tabs = TabStrings(home = "首页", discover = "发现", me = "我的"),
)

object AppStringPacks {
    val builtIn: Map<String, AppStrings> = mapOf(
        "en-US" to AppStringsEnUs, "en" to AppStringsEnUs,
        "zh-Hans" to AppStringsZhHans, "zh-CN" to AppStringsZhHans, "zh" to AppStringsZhHans,
    )
}

// 3. Provider: reads the language App already provides; takes no language parameter.
val LocalAppStrings = staticCompositionLocalOf { AppStringsEnUs }

@Composable
fun AppI18nProvider(content: @Composable () -> Unit) {
    val tag = LocalLanguageTag.current
    val fallback = LocalFallbackLanguageTag.current
    val strings = remember(tag, fallback) { resolveLanguagePack(tag, AppStringPacks.builtIn, fallback) }
    CompositionLocalProvider(LocalAppStrings provides strings, content = content)
}

// 4. Accessor.
object AppI18n {
    val strings: AppStrings
        @Composable get() = LocalAppStrings.current
}
```

Mount it once, inside `App` and around the `Navigator` (see app-structure.md §3). Read it
in a screen as `val strings = AppI18n.strings.settings`.

## Rules

- **Placeholders, not concatenation.** Word order differs between languages:
  ```kotlin
  // pack: selectedCount = "{count} selected" / "已选择 {count} 项"
  Text(strings.selectedCount.formatArgs("count" to n))   // import com.gearui.i18n.formatArgs
  ```
- **Accessibility text is text.** `contentDescription` of an icon-only button comes from
  the pack too.
- **Kit copy** is `I18n.strings.*` (`I18n.strings.common.confirm`, `.cancel`, `.back`);
  reuse it instead of duplicating "OK" and "Cancel" in your pack. Override a kit string
  per language with `App(stringsOverrides = mapOf("en-US" to StringsPatch(common = CommonStringsPatch(confirm = "Got it"))))`.
- **Outside composition** (a validator, a view model) take the text as a constructor
  parameter and pass `AppI18n.strings…` in from the screen; never put a literal there.
- **Adding a language** = one more `AppStrings` constant + its tags in `builtIn`. Tags are
  normalised (`zh_CN` → `zh-CN`) and resolved along the subtag chain
  (`zh-Hant-HK → zh-Hant → zh → fallback`).
- **Do not** mount a second `I18nRoot`, add a `languageTag` parameter to the provider, or
  keep strings in `MutableState`.
- What is *not* user-facing needs no pack: log messages, analytics event names,
  animation labels, route names, keys.
