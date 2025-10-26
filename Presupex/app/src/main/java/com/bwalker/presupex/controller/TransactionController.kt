package com.bwalker.presupex.controller

import com.bwalker.presupex.data.TransactionEntity
import com.bwalker.presupex.manager.DataManager

class TransactionController(private val dataManager: DataManager) {
    // Agrega una nueva transacción (usa el DataManager)
    fun addTransaction(transaction: TransactionEntity) {
        dataManager.addTransaction(transaction)
    }
    // Devuelve la lista completa de transacciones
    fun getAllTransactions(): List<TransactionEntity> {
        return dataManager.getAllTransactions()
    }
    // Calcula el balance total (ingresos - gastos)
    fun getBalance(): Double {
        return dataManager.getTotalIngresos() - dataManager.getTotalGastos()
    }
}
