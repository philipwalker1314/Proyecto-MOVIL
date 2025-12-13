package com.bwalker.presupex.util

import java.text.NumberFormat
import java.util.*

// This class stores general functions
object Util {
    fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "CR"))
        return format.format(amount)
    }
    fun isValidAmount(value: String): Boolean {
        return value.toDoubleOrNull()?.let { it >= 0 } == true
    }
}
