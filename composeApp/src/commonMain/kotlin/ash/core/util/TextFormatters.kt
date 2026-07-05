package ash.core.util

import kotlinx.datetime.LocalDate
import kotlin.math.abs
import kotlin.math.roundToInt

fun formatMoney(value: Double): String {
    val sign = if (value < 0) "-" else ""
    val absolute = abs(value)
    val whole = absolute.toLong().toString().withGrouping()
    val cents = ((absolute - absolute.toLong()) * 100).roundToInt().toString().padStart(2, '0')
    return sign + "$" + whole + "." + cents
}

fun formatSignedMoney(value: Double): String {
    val sign = if (value >= 0) "+" else "-"
    return "$sign${formatMoney(abs(value))}"
}

fun LocalDate?.displayText(): String = this?.toString() ?: "Not set"

private fun String.withGrouping(): String {
    return reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}
