package com.bwalker.presupex.manager

import com.bwalker.presupex.data.TransactionEntity

// Esta clase guarda todas las transacciones
class MemoryDataManager : DataManager {
    // Lista privada donde se guardan las transac
    private val transactions = mutableListOf<TransactionEntity>()
    // Agrega una nueva transacción a la lista
    override fun addTransaction(transaction: TransactionEntity) {
        transactions.add(transaction)
    }
    // Devuelve todas las transacciones guardadas
    override fun getAllTransactions(): List<TransactionEntity> {
        return transactions
    }
    // Calcula el total de ingresos sumando los amount donde type = "ingreso"
    override fun getTotalIngresos(): Double {
        return transactions
            .filter { it.type == "ing   reso" }
            .sumOf { it.amount }
    }
    // Calcula el total de gastos sumando los amount donde type = "gasto"
    override fun getTotalGastos(): Double {
        return transactions
            .filter { it.type == "gasto" }
            .sumOf { it.amount }
    }
}
