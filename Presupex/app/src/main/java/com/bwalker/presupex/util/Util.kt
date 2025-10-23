package com.bwalker.presupex.util

import java.text.NumberFormat
import java.util.*

// Esta clase guarda funciones generales
object Util {
    // Da formato bonito al dinero
    fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "CR"))
        return format.format(amount)
    }
    // Verifica que el texto que ingresa el usuario sea un número válido y positivo
    fun isValidAmount(value: String): Boolean {
        return value.toDoubleOrNull()?.let { it >= 0 } == true
    }
}
