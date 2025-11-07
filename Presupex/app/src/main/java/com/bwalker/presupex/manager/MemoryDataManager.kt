package com.bwalker.presupex.manager

import com.bwalker.presupex.data.TransactionEntity

// This class stores all transactions in memory (temporary database)
class MemoryDataManager : DataManager {

    // Private list that holds all transactions
    private val transactions = mutableListOf<TransactionEntity>()

    // Add a new transaction
    override fun addTransaction(transaction: TransactionEntity) {
        transactions.add(transaction)
    }

    // Return all saved transactions
    override fun getAllTransactions(): List<TransactionEntity> {
        return transactions
    }

    // Update an existing transaction by ID
    fun updateTransaction(updated: TransactionEntity) {
        val index = transactions.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            transactions[index] = updated
        }
    }

    // Delete a transaction by ID
    fun deleteTransaction(id: Int) {
        val index = transactions.indexOfFirst { it.id == id }
        if (index != -1) {
            transactions.removeAt(index)
        }
    }

    // Calculate total income
    override fun getTotalIngresos(): Double {
        return transactions
            .filter { it.type == "income" }
            .sumOf { it.amount }
    }

    // Calculate total expenses
    override fun getTotalGastos(): Double {
        return transactions
            .filter { it.type == "expense" }
            .sumOf { it.amount }
    }
}
