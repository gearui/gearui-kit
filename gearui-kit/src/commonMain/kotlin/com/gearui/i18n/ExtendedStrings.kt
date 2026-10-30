package com.gearui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

// Copy added after 1.0. [Strings] is frozen — a new field would change its constructor
// and break binary compatibility — so later copy lives in its own domains, resolved from
// the same language tag. These classes have internal constructors and their patches are
// mutable builders, so a domain can grow without breaking callers: adding a field adds a
// getter (and, on a patch, a setter), never a constructor parameter.
//
// Read them as [I18n.activity] and [I18n.actions]. Override them with
// [ExtendedStringsProvider]; all three packs (en-US / zh-Hans / zh-Hant) are filled in
// together.

/** States of work in progress and the pieces that report it: reasoning, tool calls, steps. */
@Immutable
class ActivityStrings internal constructor(
    val pending: String,
    val running: String,
    val succeeded: String,
    val failed: String,
    val cancelled: String,
    /** The model is still thinking. */
    val thinking: String,
    /** The reasoning title once done, when its length is unknown. */
    val thoughts: String,
    /** `{seconds}`: how long the model thought. */
    val thoughtForFormat: String,
    val toolInput: String,
    val toolOutput: String,
    val toolError: String,
    /** `{count}`: the number of steps, in a finished trace's one-line summary. */
    val stepsSummaryFormat: String,
)

/** Generic actions on a block of content. */
@Immutable
class ActionStrings internal constructor(
    val copy: String,
    /** Spoken and shown after a copy. */
    val copied: String,
    val expand: String,
    val collapse: String,
    /** Spoken state of an open disclosure. */
    val expanded: String,
    /** Spoken state of a closed disclosure. */
    val collapsed: String,
    /** `{count}`: the number of lines a truncated block has in full. */
    val showAllLinesFormat: String,
    val moreOptions: String,
    val openMenu: String,
    val closeMenu: String,
)

/** Overrides for [ActivityStrings]; unset fields keep the built-in copy. */
class ActivityStringsPatch {
    var pending: String? = null
    var running: String? = null
    var succeeded: String? = null
    var failed: String? = null
    var cancelled: String? = null
    var thinking: String? = null
    var thoughts: String? = null
    var thoughtForFormat: String? = null
    var toolInput: String? = null
    var toolOutput: String? = null
    var toolError: String? = null
    var stepsSummaryFormat: String? = null
}

/** Overrides for [ActionStrings]; unset fields keep the built-in copy. */
class ActionStringsPatch {
    var copy: String? = null
    var copied: String? = null
    var expand: String? = null
    var collapse: String? = null
    var expanded: String? = null
    var collapsed: String? = null
    var showAllLinesFormat: String? = null
    var moreOptions: String? = null
    var openMenu: String? = null
    var closeMenu: String? = null
}

internal fun ActivityStrings.merge(p: ActivityStringsPatch?): ActivityStrings = if (p == null) this else ActivityStrings(
    pending = p.pending ?: pending,
    running = p.running ?: running,
    succeeded = p.succeeded ?: succeeded,
    failed = p.failed ?: failed,
    cancelled = p.cancelled ?: cancelled,
    thinking = p.thinking ?: thinking,
    thoughts = p.thoughts ?: thoughts,
    thoughtForFormat = p.thoughtForFormat ?: thoughtForFormat,
    toolInput = p.toolInput ?: toolInput,
    toolOutput = p.toolOutput ?: toolOutput,
    toolError = p.toolError ?: toolError,
    stepsSummaryFormat = p.stepsSummaryFormat ?: stepsSummaryFormat,
)

internal fun ActionStrings.merge(p: ActionStringsPatch?): ActionStrings = if (p == null) this else ActionStrings(
    copy = p.copy ?: copy,
    copied = p.copied ?: copied,
    expand = p.expand ?: expand,
    collapse = p.collapse ?: collapse,
    expanded = p.expanded ?: expanded,
    collapsed = p.collapsed ?: collapsed,
    showAllLinesFormat = p.showAllLinesFormat ?: showAllLinesFormat,
    moreOptions = p.moreOptions ?: moreOptions,
    openMenu = p.openMenu ?: openMenu,
    closeMenu = p.closeMenu ?: closeMenu,
)

internal object ExtendedStringPacks {
    val activityEnUs = ActivityStrings(
        pending = "Pending", running = "In progress", succeeded = "Done", failed = "Failed",
        cancelled = "Cancelled", thinking = "Thinking", thoughts = "Thoughts",
        thoughtForFormat = "Thought for {seconds}s", toolInput = "Input", toolOutput = "Output",
        toolError = "Error", stepsSummaryFormat = "{count} steps",
    )
    val activityZhHans = ActivityStrings(
        pending = "等待中", running = "进行中", succeeded = "已完成", failed = "失败",
        cancelled = "已取消", thinking = "思考中", thoughts = "思考过程",
        thoughtForFormat = "已思考 {seconds} 秒", toolInput = "参数", toolOutput = "结果",
        toolError = "错误", stepsSummaryFormat = "{count} 个步骤",
    )
    val activityZhHant = ActivityStrings(
        pending = "等待中", running = "進行中", succeeded = "已完成", failed = "失敗",
        cancelled = "已取消", thinking = "思考中", thoughts = "思考過程",
        thoughtForFormat = "已思考 {seconds} 秒", toolInput = "參數", toolOutput = "結果",
        toolError = "錯誤", stepsSummaryFormat = "{count} 個步驟",
    )
    val actionsEnUs = ActionStrings(
        copy = "Copy", copied = "Copied", expand = "Expand", collapse = "Collapse",
        expanded = "Expanded", collapsed = "Collapsed", showAllLinesFormat = "Show all {count} lines",
        moreOptions = "More options", openMenu = "Open menu", closeMenu = "Close menu",
    )
    val actionsZhHans = ActionStrings(
        copy = "复制", copied = "已复制", expand = "展开", collapse = "收起",
        expanded = "已展开", collapsed = "已收起", showAllLinesFormat = "展开全部（共 {count} 行）",
        moreOptions = "更多操作", openMenu = "打开菜单", closeMenu = "关闭菜单",
    )
    val actionsZhHant = ActionStrings(
        copy = "複製", copied = "已複製", expand = "展開", collapse = "收合",
        expanded = "已展開", collapsed = "已收合", showAllLinesFormat = "展開全部（共 {count} 行）",
        moreOptions = "更多操作", openMenu = "打開選單", closeMenu = "關閉選單",
    )

    private fun <T> packs(en: T, hans: T, hant: T): Map<String, T> = mapOf(
        StringPacks.ENGLISH_TAG to en, StringPacks.CHINESE_SIMPLIFIED_TAG to hans,
        StringPacks.CHINESE_TRADITIONAL_TAG to hant, "zh-CN" to hans, "zh-TW" to hant,
        "zh-HK" to hant, "zh" to hans, "en" to en,
    )

    val activity: Map<String, ActivityStrings> = packs(activityEnUs, activityZhHans, activityZhHant)
    val actions: Map<String, ActionStrings> = packs(actionsEnUs, actionsZhHans, actionsZhHant)
}

private class ExtendedOverrides(
    val activity: Map<String, ActivityStringsPatch>,
    val actions: Map<String, ActionStringsPatch>,
)

private val LocalExtendedOverrides = staticCompositionLocalOf { ExtendedOverrides(emptyMap(), emptyMap()) }

/**
 * Overrides the copy of [I18n.activity] and [I18n.actions] for some languages, keyed by
 * language tag (`"zh-Hans"`), with the same fallback chain as the kit's own strings.
 * Place it inside `App`, around the content that should see the overrides.
 */
@Composable
fun ExtendedStringsProvider(
    activity: Map<String, ActivityStringsPatch> = emptyMap(),
    actions: Map<String, ActionStringsPatch> = emptyMap(),
    content: @Composable () -> Unit,
) {
    val overrides = remember(activity, actions) { ExtendedOverrides(activity, actions) }
    CompositionLocalProvider(LocalExtendedOverrides provides overrides, content = content)
}

/** The patch for [tag], walking the same fallback chain as the packs. */
internal fun <P> resolveExtendedPatch(tag: String, fallback: String, overrides: Map<String, P>): P? {
    if (overrides.isEmpty()) return null
    val normalized = overrides.entries.associate { normalizeLanguageTag(it.key) to it.value }
    var current = normalizeLanguageTag(tag)
    while (true) {
        normalized[current]?.let { return it }
        val idx = current.lastIndexOf('-')
        if (idx <= 0) break
        current = current.substring(0, idx)
    }
    return normalized[normalizeLanguageTag(fallback)]
}

@Composable
internal fun resolvedActivityStrings(): ActivityStrings {
    val tag = LocalLanguageTag.current
    val fallback = LocalFallbackLanguageTag.current
    val overrides = LocalExtendedOverrides.current.activity
    return remember(tag, fallback, overrides) {
        resolveLanguagePack(tag, ExtendedStringPacks.activity, fallback).merge(resolveExtendedPatch(tag, fallback, overrides))
    }
}

@Composable
internal fun resolvedActionStrings(): ActionStrings {
    val tag = LocalLanguageTag.current
    val fallback = LocalFallbackLanguageTag.current
    val overrides = LocalExtendedOverrides.current.actions
    return remember(tag, fallback, overrides) {
        resolveLanguagePack(tag, ExtendedStringPacks.actions, fallback).merge(resolveExtendedPatch(tag, fallback, overrides))
    }
}
