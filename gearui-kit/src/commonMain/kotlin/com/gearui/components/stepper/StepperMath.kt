package com.gearui.components.stepper

internal fun stepperValue(value: Int, delta: Long, min: Int, max: Int): Int =
    (value.toLong() + delta).coerceIn(min.toLong(), max.toLong()).toInt()
