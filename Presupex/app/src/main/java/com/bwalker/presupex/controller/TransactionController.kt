package com.bwalker.presupex.controller

import com.bwalker.presupex.data.TransactionEntity
import com.bwalker.presupex.manager.DataManager
import com.bwalker.presupex.manager.MemoryDataManager

class TransactionController(private val dataManager: DataManager) {

    // Add a new transaction
    fun addTransaction(transaction: TransactionEntity) {
        dataManager.addTransaction(transaction)
    }

    // Get all transactions
    fun getAllTransactions(): List<TransactionEntity> {
        return dataManager.getAllTransactions()
    }

    // ✅ Expose totals so MainActivity can access them
    fun getTotalIngresos(): Double {
        return dataManager.getTotalIngresos()
    }

    fun getTotalGastos(): Double {
        return dataManager.getTotalGastos()
    }

    // Calculate total balance (income - expenses)
    fun getBalance(): Double {
        return dataManager.getTotalIngresos() - dataManager.getTotalGastos()
    }

    // Update an existing transaction
    fun updateTransaction(transaction: TransactionEntity) {
        if (dataManager is MemoryDataManager) {
            dataManager.updateTransaction(transaction)
        }
    }

    // Delete a transaction by ID
    fun deleteTransaction(id: Int) {
        if (dataManager is MemoryDataManager) {
            dataManager.deleteTransaction(id)
        }
    }
}
