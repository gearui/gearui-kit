package com.gearui.unit

/**
 * GearUI Unit 系统
 *
 * 封装底层 Kuikly Compose UI 单位类型，保持 GearUI 框架独立性
 * 使用方式：import com.gearui.unit.*
 */

// Type aliases
typealias Dp = com.tencent.kuikly.compose.ui.unit.Dp
typealias TextUnit = com.tencent.kuikly.compose.ui.unit.TextUnit
typealias DpSize = com.tencent.kuikly.compose.ui.unit.DpSize

// Dp extensions
val Int.dp: Dp get() = com.tencent.kuikly.compose.ui.unit.Dp(this.toFloat())
val Float.dp: Dp get() = com.tencent.kuikly.compose.ui.unit.Dp(this)
val Double.dp: Dp get() = com.tencent.kuikly.compose.ui.unit.Dp(this.toFloat())

// TextUnit extensions (sp)
val Int.sp: TextUnit
    get() = com.tencent.kuikly.compose.ui.unit.TextUnit(
        this.toFloat(),
        com.tencent.kuikly.compose.ui.unit.TextUnitType.Sp
    )
val Float.sp: TextUnit
    get() = com.tencent.kuikly.compose.ui.unit.TextUnit(
        this,
        com.tencent.kuikly.compose.ui.unit.TextUnitType.Sp
    )
val Double.sp: TextUnit
    get() = com.tencent.kuikly.compose.ui.unit.TextUnit(
        this.toFloat(),
        com.tencent.kuikly.compose.ui.unit.TextUnitType.Sp
    )

// TextUnit extensions (em)
val Int.em: TextUnit
    get() = com.tencent.kuikly.compose.ui.unit.TextUnit(
        this.toFloat(),
        com.tencent.kuikly.compose.ui.unit.TextUnitType.Em
    )
val Float.em: TextUnit
    get() = com.tencent.kuikly.compose.ui.unit.TextUnit(
        this,
        com.tencent.kuikly.compose.ui.unit.TextUnitType.Em
    )
val Double.em: TextUnit
    get() = com.tencent.kuikly.compose.ui.unit.TextUnit(
        this.toFloat(),
        com.tencent.kuikly.compose.ui.unit.TextUnitType.Em
    )
