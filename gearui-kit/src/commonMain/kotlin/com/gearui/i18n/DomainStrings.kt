package com.gearui.i18n

// Copy is split into small per-domain data classes. A single class with dozens
// of fields generates enough copy/equals/hashCode/componentN bytecode to
// approach the DEX method and 255-parameter limits, which surfaces as an
// Android-only runtime VerifyError on adding one more key. The [Strings] facade
// exposes delegating getters so call sites stay stable. New copy goes into the
// matching domain, and all three packs (en-US / zh-Hans / zh-Hant) must be
// filled in together.
//
// Fields suffixed `Format` are templates with `{name}` placeholders. Expand
// them with [formatArgs] at the bottom of this file; never concatenate by hand
// in a component.

import androidx.compose.runtime.Immutable

// ============================ common ============================

/** Actions and states reused across components. */
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

/** Theme and language switcher copy. */
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

/** Input and selection controls: Select, Cascader, TreeSelect, SearchBar, Switch, Transfer, Table. */
@Immutable
data class FieldStrings(
    val selectPlaceholder: String,
    val searchPlaceholder: String,
    /** Selected count; placeholder `{count}`. */
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

/** Date and time pickers, and the calendar. */
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
    /** Calendar heading; placeholders `{year}` and `{month}`. */
    val calendarYearMonthFormat: String,
    /** Weekday headings, starting Sunday. Must have exactly 7 entries. */
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

/** Feedback states: Result, EmptyState and form validation. */
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

/** Image and ImageViewer. */
@Immutable
data class MediaStrings(
    val imageEmpty: String,
    /** Image index; placeholder `{index}`. */
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

/** Guidance and rating components: Tour, Rate. */
@Immutable
data class GuideStrings(
    val tourSkip: String,
    val tourPrevious: String,
    val tourNext: String,
    val tourFinish: String,
    /** Rating descriptions, lowest to highest. Must have exactly 5 entries. */
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
 * Expands `{name}` placeholders in a template. Unsupplied placeholders are left
 * as-is so a missing argument is visible rather than silently blank.
 *
 * Deliberately not named `format`: on the Android (JVM) target the stdlib has
 * `String.format(vararg Any?)`, and `Pair` is an `Any?`, so the same name would
 * make the two overloads ambiguous there.
 */
fun String.formatArgs(vararg args: Pair<String, Any?>): String {
    var out = this
    for ((key, value) in args) {
        out = out.replace("{$key}", value.toString())
    }
    return out
}
