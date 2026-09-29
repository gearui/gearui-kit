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
    /** Accessibility labels for icon-only controls. */
    val back: String,
    val close: String,
    val remove: String,
    val add: String,
    /** Accessibility state of a selectable item. */
    val selected: String,
    val unselected: String,
    val pullToRefresh: String,
    val releaseToRefresh: String,
    val refreshing: String,
    /** Spoken state of a switch that is on. */
    val on: String,
    /** Spoken state of a switch that is off. */
    val off: String,
    /** Spoken state of a checkbox with some children selected. */
    val partiallySelected: String,
    /** Footer after a page failed; tapping it retries. */
    val loadMoreFailed: String,
    /** Footer once the list has no more pages. */
    val noMoreData: String,
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
    val back: String? = null,
    val close: String? = null,
    val remove: String? = null,
    val add: String? = null,
    val selected: String? = null,
    val unselected: String? = null,
    val pullToRefresh: String? = null,
    val releaseToRefresh: String? = null,
    val refreshing: String? = null,
    val on: String? = null,
    val off: String? = null,
    val partiallySelected: String? = null,
    val loadMoreFailed: String? = null,
    val noMoreData: String? = null,
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
        backToTop == null &&
        back == null &&
        close == null &&
        remove == null &&
        add == null &&
        selected == null &&
        unselected == null &&
        pullToRefresh == null &&
        releaseToRefresh == null &&
        refreshing == null &&
        on == null &&
        off == null &&
        partiallySelected == null &&
        loadMoreFailed == null &&
        noMoreData == null

fun CommonStrings.merge(patch: CommonStringsPatch?): CommonStrings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        noMoreData = patch.noMoreData ?: noMoreData,
        loadMoreFailed = patch.loadMoreFailed ?: loadMoreFailed,
        partiallySelected = patch.partiallySelected ?: partiallySelected,
        off = patch.off ?: off,
        on = patch.on ?: on,
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
        back = patch.back ?: back,
        close = patch.close ?: close,
        remove = patch.remove ?: remove,
        add = patch.add ?: add,
        selected = patch.selected ?: selected,
        unselected = patch.unselected ?: unselected,
        pullToRefresh = patch.pullToRefresh ?: pullToRefresh,
        releaseToRefresh = patch.releaseToRefresh ?: releaseToRefresh,
        refreshing = patch.refreshing ?: refreshing,
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

/** Input and selection controls: Select, Cascader, SearchBar, Switch, Table. */
@Immutable
data class FieldStrings(
    val selectPlaceholder: String,
    val searchPlaceholder: String,
    /** Selected count; placeholder `{count}`. */
    val selectedCountFormat: String,
    val switchOn: String,
    val switchOff: String,
    val tableEmpty: String,
    /** Accessibility label of a rating star; placeholder `{value}`. */
    val ratingValueFormat: String,
    /** Accessibility label of the password visibility toggle. */
    val showPassword: String,
    val hidePassword: String,
    /** Table header selection checkbox. */
    val selectAll: String,
    /** Table row selection checkbox; placeholder `{index}`. */
    val selectRowFormat: String,
    /** Clear-text button on fields. */
    val clear: String,
    /** Spoken name of a one-time-code field. */
    val verificationCode: String,
)

data class FieldStringsPatch(
    val selectPlaceholder: String? = null,
    val searchPlaceholder: String? = null,
    val selectedCountFormat: String? = null,
    val switchOn: String? = null,
    val switchOff: String? = null,
    val tableEmpty: String? = null,
    val ratingValueFormat: String? = null,
    val showPassword: String? = null,
    val hidePassword: String? = null,
    val selectAll: String? = null,
    val selectRowFormat: String? = null,
    val clear: String? = null,
    val verificationCode: String? = null,
)

val FieldStringsPatch.isEmpty: Boolean
    get() = selectPlaceholder == null &&
        searchPlaceholder == null &&
        selectedCountFormat == null &&
        switchOn == null &&
        switchOff == null &&
        tableEmpty == null &&
        ratingValueFormat == null &&
        showPassword == null &&
        hidePassword == null &&
        selectAll == null &&
        selectRowFormat == null &&
        clear == null &&
        verificationCode == null

fun FieldStrings.merge(patch: FieldStringsPatch?): FieldStrings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        verificationCode = patch.verificationCode ?: verificationCode,
        clear = patch.clear ?: clear,
        selectRowFormat = patch.selectRowFormat ?: selectRowFormat,
        selectAll = patch.selectAll ?: selectAll,
        hidePassword = patch.hidePassword ?: hidePassword,
        showPassword = patch.showPassword ?: showPassword,
        selectPlaceholder = patch.selectPlaceholder ?: selectPlaceholder,
        searchPlaceholder = patch.searchPlaceholder ?: searchPlaceholder,
        selectedCountFormat = patch.selectedCountFormat ?: selectedCountFormat,
        switchOn = patch.switchOn ?: switchOn,
        switchOff = patch.switchOff ?: switchOff,
        tableEmpty = patch.tableEmpty ?: tableEmpty,
        ratingValueFormat = patch.ratingValueFormat ?: ratingValueFormat,
    )
}

// ============================ dateTime ============================

/** Date and time pickers, and the calendar. */
@Immutable
data class DateTimeStrings(
    val datePlaceholder: String,
    val rangeStartPlaceholder: String,
    val rangeEndPlaceholder: String,
    val selectRangeTitle: String,
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
    /** Calendar navigation, for screen readers. */
    val previousMonth: String,
    val nextMonth: String,
    val secondSuffix: String = "s",
)

data class DateTimeStringsPatch(
    val datePlaceholder: String? = null,
    val rangeStartPlaceholder: String? = null,
    val rangeEndPlaceholder: String? = null,
    val selectRangeTitle: String? = null,
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
    val previousMonth: String? = null,
    val nextMonth: String? = null,
    val secondSuffix: String? = null,
)

val DateTimeStringsPatch.isEmpty: Boolean
    get() = datePlaceholder == null &&
        rangeStartPlaceholder == null &&
        rangeEndPlaceholder == null &&
        selectRangeTitle == null &&
        timePlaceholder == null &&
        selectDateTitle == null &&
        selectTimeTitle == null &&
        yearSuffix == null &&
        monthSuffix == null &&
        daySuffix == null &&
        hourSuffix == null &&
        minuteSuffix == null &&
        secondSuffix == null &&
        calendarYearMonthFormat == null &&
        weekdaysShort == null &&
        previousMonth == null &&
        nextMonth == null

fun DateTimeStrings.merge(patch: DateTimeStringsPatch?): DateTimeStrings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        nextMonth = patch.nextMonth ?: nextMonth,
        previousMonth = patch.previousMonth ?: previousMonth,
        datePlaceholder = patch.datePlaceholder ?: datePlaceholder,
        rangeStartPlaceholder = patch.rangeStartPlaceholder ?: rangeStartPlaceholder,
        rangeEndPlaceholder = patch.rangeEndPlaceholder ?: rangeEndPlaceholder,
        selectRangeTitle = patch.selectRangeTitle ?: selectRangeTitle,
        timePlaceholder = patch.timePlaceholder ?: timePlaceholder,
        selectDateTitle = patch.selectDateTitle ?: selectDateTitle,
        selectTimeTitle = patch.selectTimeTitle ?: selectTimeTitle,
        yearSuffix = patch.yearSuffix ?: yearSuffix,
        monthSuffix = patch.monthSuffix ?: monthSuffix,
        daySuffix = patch.daySuffix ?: daySuffix,
        hourSuffix = patch.hourSuffix ?: hourSuffix,
        minuteSuffix = patch.minuteSuffix ?: minuteSuffix,
        secondSuffix = patch.secondSuffix ?: secondSuffix,
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
    /** Carousel arrows, for screen readers. */
    val previousSlide: String,
    val nextSlide: String,
)

data class MediaStringsPatch(
    val imageEmpty: String? = null,
    val imageIndexFormat: String? = null,
    val previousSlide: String? = null,
    val nextSlide: String? = null,
)

val MediaStringsPatch.isEmpty: Boolean
    get() = imageEmpty == null && imageIndexFormat == null &&
        previousSlide == null &&
        nextSlide == null

fun MediaStrings.merge(patch: MediaStringsPatch?): MediaStrings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        nextSlide = patch.nextSlide ?: nextSlide,
        previousSlide = patch.previousSlide ?: previousSlide,
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
