package com.gearui.i18n

// 文案按语义域拆分为小 data class，避免单类字段爆炸触发 DEX 方法字节码 / 255 参数
// 上限。门面 [Strings] 用委托 getter 暴露稳定的调用点。新增文案加到对应域即可，
// 三份语言包（en-US / zh-Hans / zh-Hant）必须同步补齐。
//
// 带 `Format` 后缀的字段是模板串，占位符形如 `{count}`，用本文件末尾的 [format]
// 展开，不要在组件里手工拼接。

import androidx.compose.runtime.Immutable

// ============================ common ============================

/** 跨组件复用的通用动作与状态文案。 */
@Immutable
data class CommonStrings(
    val confirm: String,
    val ok: String,
    val cancel: String,
    val retry: String,
    val search: String,
    val loading: String,
    val loadFailed: String,
    val noData: String,
    val noSearchResult: String,
    val networkError: String,
    val backToTop: String,
)

data class CommonStringsPatch(
    val confirm: String? = null,
    val ok: String? = null,
    val cancel: String? = null,
    val retry: String? = null,
    val search: String? = null,
    val loading: String? = null,
    val loadFailed: String? = null,
    val noData: String? = null,
    val noSearchResult: String? = null,
    val networkError: String? = null,
    val backToTop: String? = null,
)

val CommonStringsPatch.isEmpty: Boolean
    get() = confirm == null &&
        ok == null &&
        cancel == null &&
        retry == null &&
        search == null &&
        loading == null &&
        loadFailed == null &&
        noData == null &&
        noSearchResult == null &&
        networkError == null &&
        backToTop == null

fun CommonStrings.merge(patch: CommonStringsPatch?): CommonStrings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        confirm = patch.confirm ?: confirm,
        ok = patch.ok ?: ok,
        cancel = patch.cancel ?: cancel,
        retry = patch.retry ?: retry,
        search = patch.search ?: search,
        loading = patch.loading ?: loading,
        loadFailed = patch.loadFailed ?: loadFailed,
        noData = patch.noData ?: noData,
        noSearchResult = patch.noSearchResult ?: noSearchResult,
        networkError = patch.networkError ?: networkError,
        backToTop = patch.backToTop ?: backToTop,
    )
}

// ============================ theme ============================

/** 主题 / 语言切换面板文案。 */
@Immutable
data class ThemeStrings(
    val theme: String,
    val language: String,
    val light: String,
    val dark: String,
    val system: String,
)

data class ThemeStringsPatch(
    val theme: String? = null,
    val language: String? = null,
    val light: String? = null,
    val dark: String? = null,
    val system: String? = null,
)

val ThemeStringsPatch.isEmpty: Boolean
    get() = theme == null &&
        language == null &&
        light == null &&
        dark == null &&
        system == null

fun ThemeStrings.merge(patch: ThemeStringsPatch?): ThemeStrings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        theme = patch.theme ?: theme,
        language = patch.language ?: language,
        light = patch.light ?: light,
        dark = patch.dark ?: dark,
        system = patch.system ?: system,
    )
}

// ============================ field ============================

/** 输入 / 选择类组件（Select、Cascader、TreeSelect、SearchBar、Switch、Transfer、Table）。 */
@Immutable
data class FieldStrings(
    val selectPlaceholder: String,
    val searchPlaceholder: String,
    /** 已选数量，占位符 `{count}`。 */
    val selectedCountFormat: String,
    val switchOn: String,
    val switchOff: String,
    val transferSourceTitle: String,
    val transferTargetTitle: String,
    val tableEmpty: String,
)

data class FieldStringsPatch(
    val selectPlaceholder: String? = null,
    val searchPlaceholder: String? = null,
    val selectedCountFormat: String? = null,
    val switchOn: String? = null,
    val switchOff: String? = null,
    val transferSourceTitle: String? = null,
    val transferTargetTitle: String? = null,
    val tableEmpty: String? = null,
)

val FieldStringsPatch.isEmpty: Boolean
    get() = selectPlaceholder == null &&
        searchPlaceholder == null &&
        selectedCountFormat == null &&
        switchOn == null &&
        switchOff == null &&
        transferSourceTitle == null &&
        transferTargetTitle == null &&
        tableEmpty == null

fun FieldStrings.merge(patch: FieldStringsPatch?): FieldStrings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        selectPlaceholder = patch.selectPlaceholder ?: selectPlaceholder,
        searchPlaceholder = patch.searchPlaceholder ?: searchPlaceholder,
        selectedCountFormat = patch.selectedCountFormat ?: selectedCountFormat,
        switchOn = patch.switchOn ?: switchOn,
        switchOff = patch.switchOff ?: switchOff,
        transferSourceTitle = patch.transferSourceTitle ?: transferSourceTitle,
        transferTargetTitle = patch.transferTargetTitle ?: transferTargetTitle,
        tableEmpty = patch.tableEmpty ?: tableEmpty,
    )
}

// ============================ dateTime ============================

/** 日期 / 时间选择与日历。 */
@Immutable
data class DateTimeStrings(
    val datePlaceholder: String,
    val timePlaceholder: String,
    val selectDateTitle: String,
    val selectTimeTitle: String,
    val yearSuffix: String,
    val monthSuffix: String,
    val daySuffix: String,
    val hourSuffix: String,
    val minuteSuffix: String,
    /** 日历标题，占位符 `{year}` / `{month}`。 */
    val calendarYearMonthFormat: String,
    /** 星期表头，从周日开始，长度必须为 7。 */
    val weekdaysShort: List<String>,
)

data class DateTimeStringsPatch(
    val datePlaceholder: String? = null,
    val timePlaceholder: String? = null,
    val selectDateTitle: String? = null,
    val selectTimeTitle: String? = null,
    val yearSuffix: String? = null,
    val monthSuffix: String? = null,
    val daySuffix: String? = null,
    val hourSuffix: String? = null,
    val minuteSuffix: String? = null,
    val calendarYearMonthFormat: String? = null,
    val weekdaysShort: List<String>? = null,
)

val DateTimeStringsPatch.isEmpty: Boolean
    get() = datePlaceholder == null &&
        timePlaceholder == null &&
        selectDateTitle == null &&
        selectTimeTitle == null &&
        yearSuffix == null &&
        monthSuffix == null &&
        daySuffix == null &&
        hourSuffix == null &&
        minuteSuffix == null &&
        calendarYearMonthFormat == null &&
        weekdaysShort == null

fun DateTimeStrings.merge(patch: DateTimeStringsPatch?): DateTimeStrings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        datePlaceholder = patch.datePlaceholder ?: datePlaceholder,
        timePlaceholder = patch.timePlaceholder ?: timePlaceholder,
        selectDateTitle = patch.selectDateTitle ?: selectDateTitle,
        selectTimeTitle = patch.selectTimeTitle ?: selectTimeTitle,
        yearSuffix = patch.yearSuffix ?: yearSuffix,
        monthSuffix = patch.monthSuffix ?: monthSuffix,
        daySuffix = patch.daySuffix ?: daySuffix,
        hourSuffix = patch.hourSuffix ?: hourSuffix,
        minuteSuffix = patch.minuteSuffix ?: minuteSuffix,
        calendarYearMonthFormat = patch.calendarYearMonthFormat ?: calendarYearMonthFormat,
        weekdaysShort = patch.weekdaysShort ?: weekdaysShort,
    )
}

// ============================ feedback ============================

/** Result / EmptyState / Form 校验等反馈态文案。 */
@Immutable
data class FeedbackStrings(
    val notFoundTitle: String,
    val notFoundDescription: String,
    val forbiddenTitle: String,
    val forbiddenDescription: String,
    val processingTitle: String,
    val processingDescription: String,
    val networkErrorDescription: String,
    val emptyNoDataDescription: String,
    val emptyNoSearchResultDescription: String,
    val emptyNoNetworkTitle: String,
    val emptyNoNetworkDescription: String,
    val emptyErrorDescription: String,
    val emptyNoPermissionTitle: String,
    val emptyNoPermissionDescription: String,
    val validationFailed: String,
    val fieldRequired: String,
)

data class FeedbackStringsPatch(
    val notFoundTitle: String? = null,
    val notFoundDescription: String? = null,
    val forbiddenTitle: String? = null,
    val forbiddenDescription: String? = null,
    val processingTitle: String? = null,
    val processingDescription: String? = null,
    val networkErrorDescription: String? = null,
    val emptyNoDataDescription: String? = null,
    val emptyNoSearchResultDescription: String? = null,
    val emptyNoNetworkTitle: String? = null,
    val emptyNoNetworkDescription: String? = null,
    val emptyErrorDescription: String? = null,
    val emptyNoPermissionTitle: String? = null,
    val emptyNoPermissionDescription: String? = null,
    val validationFailed: String? = null,
    val fieldRequired: String? = null,
)

val FeedbackStringsPatch.isEmpty: Boolean
    get() = notFoundTitle == null &&
        notFoundDescription == null &&
        forbiddenTitle == null &&
        forbiddenDescription == null &&
        processingTitle == null &&
        processingDescription == null &&
        networkErrorDescription == null &&
        emptyNoDataDescription == null &&
        emptyNoSearchResultDescription == null &&
        emptyNoNetworkTitle == null &&
        emptyNoNetworkDescription == null &&
        emptyErrorDescription == null &&
        emptyNoPermissionTitle == null &&
        emptyNoPermissionDescription == null &&
        validationFailed == null &&
        fieldRequired == null

fun FeedbackStrings.merge(patch: FeedbackStringsPatch?): FeedbackStrings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        notFoundTitle = patch.notFoundTitle ?: notFoundTitle,
        notFoundDescription = patch.notFoundDescription ?: notFoundDescription,
        forbiddenTitle = patch.forbiddenTitle ?: forbiddenTitle,
        forbiddenDescription = patch.forbiddenDescription ?: forbiddenDescription,
        processingTitle = patch.processingTitle ?: processingTitle,
        processingDescription = patch.processingDescription ?: processingDescription,
        networkErrorDescription = patch.networkErrorDescription ?: networkErrorDescription,
        emptyNoDataDescription = patch.emptyNoDataDescription ?: emptyNoDataDescription,
        emptyNoSearchResultDescription = patch.emptyNoSearchResultDescription
            ?: emptyNoSearchResultDescription,
        emptyNoNetworkTitle = patch.emptyNoNetworkTitle ?: emptyNoNetworkTitle,
        emptyNoNetworkDescription = patch.emptyNoNetworkDescription ?: emptyNoNetworkDescription,
        emptyErrorDescription = patch.emptyErrorDescription ?: emptyErrorDescription,
        emptyNoPermissionTitle = patch.emptyNoPermissionTitle ?: emptyNoPermissionTitle,
        emptyNoPermissionDescription = patch.emptyNoPermissionDescription
            ?: emptyNoPermissionDescription,
        validationFailed = patch.validationFailed ?: validationFailed,
        fieldRequired = patch.fieldRequired ?: fieldRequired,
    )
}

// ============================ media ============================

/** Image / ImageViewer。 */
@Immutable
data class MediaStrings(
    val imageEmpty: String,
    /** 图片序号，占位符 `{index}`。 */
    val imageIndexFormat: String,
)

data class MediaStringsPatch(
    val imageEmpty: String? = null,
    val imageIndexFormat: String? = null,
)

val MediaStringsPatch.isEmpty: Boolean
    get() = imageEmpty == null && imageIndexFormat == null

fun MediaStrings.merge(patch: MediaStringsPatch?): MediaStrings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        imageEmpty = patch.imageEmpty ?: imageEmpty,
        imageIndexFormat = patch.imageIndexFormat ?: imageIndexFormat,
    )
}

// ============================ guide ============================

/** Tour / Rate 等引导与评价类组件。 */
@Immutable
data class GuideStrings(
    val tourSkip: String,
    val tourPrevious: String,
    val tourNext: String,
    val tourFinish: String,
    /** 评分档位描述，从最低分到最高分，长度必须为 5。 */
    val rateDescriptions: List<String>,
)

data class GuideStringsPatch(
    val tourSkip: String? = null,
    val tourPrevious: String? = null,
    val tourNext: String? = null,
    val tourFinish: String? = null,
    val rateDescriptions: List<String>? = null,
)

val GuideStringsPatch.isEmpty: Boolean
    get() = tourSkip == null &&
        tourPrevious == null &&
        tourNext == null &&
        tourFinish == null &&
        rateDescriptions == null

fun GuideStrings.merge(patch: GuideStringsPatch?): GuideStrings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        tourSkip = patch.tourSkip ?: tourSkip,
        tourPrevious = patch.tourPrevious ?: tourPrevious,
        tourNext = patch.tourNext ?: tourNext,
        tourFinish = patch.tourFinish ?: tourFinish,
        rateDescriptions = patch.rateDescriptions ?: rateDescriptions,
    )
}

// ============================ format ============================

/**
 * 展开模板串里的 `{name}` 占位符。未提供的占位符原样保留，方便定位漏配。
 *
 * 刻意不叫 `format`：Android(JVM) 上 stdlib 有 `String.format(vararg Any?)`，
 * 而 `Pair` 也是 `Any?`，同名会在 JVM target 上产生重载歧义。
 */
fun String.formatArgs(vararg args: Pair<String, Any?>): String {
    var out = this
    for ((key, value) in args) {
        out = out.replace("{$key}", value.toString())
    }
    return out
}
