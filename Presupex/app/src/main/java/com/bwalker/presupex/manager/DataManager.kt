package com.bwalker.presupex.manager
import com.bwalker.presupex.data.TransactionEntity

// Esta interfaz definir reglas basicas
interface DataManager {
    // Agrega una transacción nueva
    fun addTransaction(transaction: TransactionEntity)
    // ver otdas las transaccciones
    fun getAllTransactions(): List<TransactionEntity>
    // Calcular el total de ingresos
    fun getTotalIngresos(): Double
    // Calcula el total de gastos
    fun getTotalGastos(): Double
}