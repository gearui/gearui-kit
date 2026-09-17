package com.gearui.foundation.typography

/** UIKit expects PostScript names rather than CSS generic family names. */
internal actual fun nativeGenericFont(name: String): String? = when (name) {
    "serif" -> "TimesNewRomanPSMT"
    "monospace" -> "Menlo-Regular"
    "cursive" -> "SnellRoundhand"
    else -> null
}
