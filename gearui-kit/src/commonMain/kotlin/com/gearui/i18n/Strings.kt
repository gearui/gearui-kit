package com.gearui.i18n

import androidx.compose.runtime.Immutable

@Immutable
data class Strings(
    // 通用
    val buttonConfirm: String,
    val buttonCancel: String,

    // 设置
    val theme: String,
    val language: String,
    val light: String,
    val dark: String,
    val system: String,
)

@Deprecated("Use GearStringPacks")
object StringSets {
    val English: Strings = GearStringPacks.English
    val Chinese: Strings = GearStringPacks.ChineseSimplified
}
