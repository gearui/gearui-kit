package com.gearui.foundation.typography

// Resetting a font to an empty string on Web falls back to the browser's serif default.
internal actual fun nativeGenericFont(name: String): String? =
    if (name == "system-ui") "system-ui" else null
