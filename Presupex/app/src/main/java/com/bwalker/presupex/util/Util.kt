package com.bwalker.presupex.util

import java.text.NumberFormat
import java.util.*

// This class stores general functions
object Util {
    // Da formato bonito al dinero
    fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "CR"))
        return format.format(amount)
    }
    // Verify that the text entered by the user is a valid and positive number.
    fun isValidAmount(value: String): Boolean {
        return value.toDoubleOrNull()?.let { it >= 0 } == true
    }
}
