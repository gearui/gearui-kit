package com.gearui.i18n

import kotlin.js.Date

internal actual fun currentEpochMillis(): Long = Date.now().toLong()

internal actual fun localMomentOf(epochMillis: Long): LocalMoment {
    val d = Date(epochMillis.toDouble())
    return LocalMoment(d.getFullYear(), d.getMonth() + 1, d.getDate(), d.getHours(), d.getMinutes())
}
