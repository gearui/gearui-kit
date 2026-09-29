package com.gearui.components.numberfield

/** Exact base-10 value. Parsing, comparison and stepping never pass through a floating point number. */
class DecimalValue private constructor(private val negative: Boolean, private val digits: String, private val scale: Int) : Comparable<DecimalValue> {
    override fun toString(): String {
        val padded = digits.padStart(scale + 1, '0')
        val text = if (scale == 0) padded else padded.dropLast(scale) + "." + padded.takeLast(scale)
        return if (negative) "-$text" else text
    }
    override fun equals(other: Any?): Boolean = other is DecimalValue && compareTo(other) == 0
    override fun hashCode(): Int = toString().hashCode()
    override fun compareTo(other: DecimalValue): Int {
        if (negative != other.negative) return if (negative) -1 else 1
        val common = maxOf(scale, other.scale)
        val order = magnitudeCompare(digits + "0".repeat(common - scale), other.digits + "0".repeat(common - other.scale))
        return if (negative) -order else order
    }
    operator fun plus(other: DecimalValue): DecimalValue {
        val common = maxOf(scale, other.scale)
        val a = digits + "0".repeat(common - scale)
        val b = other.digits + "0".repeat(common - other.scale)
        if (negative == other.negative) return normalized(negative, addMagnitude(a, b), common)
        val comparison = magnitudeCompare(a, b)
        return if (comparison >= 0) normalized(negative, subtractMagnitude(a, b), common)
            else normalized(other.negative, subtractMagnitude(b, a), common)
    }
    operator fun minus(other: DecimalValue): DecimalValue = this + normalized(!other.negative, other.digits, other.scale)
    fun coerceIn(min: DecimalValue? = null, max: DecimalValue? = null): DecimalValue {
        require(min == null || max == null || min <= max)
        return when { min != null && this < min -> min; max != null && this > max -> max; else -> this }
    }

    companion object {
        val Zero = DecimalValue(false, "0", 0)
        val One = DecimalValue(false, "1", 0)
        fun parse(text: String): DecimalValue? {
            val source = text.trim()
            if (!Regex("[+-]?(?:[0-9]+(?:\\.[0-9]+)?|\\.[0-9]+)").matches(source)) return null
            val unsigned = source.removePrefix("-").removePrefix("+")
            return normalized(source.startsWith("-"), unsigned.replace(".", ""), unsigned.substringAfter('.', "").length)
        }
        private fun normalized(negative: Boolean, digits: String, scale: Int): DecimalValue {
            var significant = digits.trimStart('0').ifEmpty { "0" }
            var places = scale
            while (places > 0 && significant.endsWith('0')) { significant = significant.dropLast(1); places-- }
            significant = significant.ifEmpty { "0" }
            if (significant == "0") return Zero
            return DecimalValue(negative, significant, places)
        }
    }
}

private fun magnitudeCompare(a: String, b: String): Int {
    val x = a.trimStart('0').ifEmpty { "0" }; val y = b.trimStart('0').ifEmpty { "0" }
    return if (x.length == y.length) x.compareTo(y) else x.length.compareTo(y.length)
}

private fun addMagnitude(a: String, b: String): String {
    var carry = 0
    val output = StringBuilder()
    for (i in 0 until maxOf(a.length, b.length)) {
        val sum = (a.getOrNull(a.lastIndex - i)?.digitToInt() ?: 0) + (b.getOrNull(b.lastIndex - i)?.digitToInt() ?: 0) + carry
        output.append(sum % 10); carry = sum / 10
    }
    if (carry != 0) output.append(carry)
    return output.reverse().toString()
}

/** a >= b */
private fun subtractMagnitude(a: String, b: String): String {
    var borrow = 0
    val output = StringBuilder()
    for (i in a.indices) {
        var n = a[a.lastIndex - i].digitToInt() - (b.getOrNull(b.lastIndex - i)?.digitToInt() ?: 0) - borrow
        borrow = if (n < 0) 1 else 0
        if (n < 0) n += 10
        output.append(n)
    }
    return output.reverse().toString().trimStart('0').ifEmpty { "0" }
}
