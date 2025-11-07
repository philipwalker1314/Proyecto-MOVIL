package com.bwalker.presupex
import com.bwalker.presupex.manager.DataProvider

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bwalker.presupex.controller.TransactionController
import com.bwalker.presupex.data.TransactionEntity
import com.bwalker.presupex.manager.MemoryDataManager
import com.bwalker.presupex.util.Util

class TransactionListActivity : AppCompatActivity() {

    private lateinit var listTransactions: ListView
    private lateinit var btnBack: Button
    private lateinit var controller: TransactionController
    private lateinit var adapter: ArrayAdapter<String>

    private var transactions = mutableListOf<TransactionEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)

        // Link UI components
        listTransactions = findViewById(R.id.listTransactions)
        btnBack = findViewById(R.id.btnBack)

        // Initialize controller
        controller = TransactionController(DataProvider.sharedDataManager)

        // Load and display all transactions
        loadTransactions()

        // Item click -> show dialog for Edit/Delete
        listTransactions.setOnItemClickListener { _, _, position, _ ->
            val selectedTransaction = transactions[position]
            showActionDialog(selectedTransaction)
        }

        // Back button
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadTransactions() {
        transactions = controller.getAllTransactions().toMutableList()

        if (transactions.isEmpty()) {
            Toast.makeText(this, "No transactions available", Toast.LENGTH_SHORT).show()
        }

        val items = transactions.map {
            val sign = if (it.type == "income") "+" else "-"
            val formattedAmount = Util.formatCurrency(it.amount)
            "${it.category}: $sign$formattedAmount (${it.date})"
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listTransactions.adapter = adapter
    }

    private fun showActionDialog(transaction: TransactionEntity) {
        val options = arrayOf("Edit", "Delete", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select an action")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openEditTransaction(transaction)
                1 -> confirmDelete(transaction)
                else -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun openEditTransaction(transaction: TransactionEntity) {
        val intent = Intent(this, AddTransactionActivity::class.java)
        intent.putExtra("transaction_id", transaction.id)
        intent.putExtra("type", transaction.type)
        startActivity(intent)
    }

    private fun confirmDelete(transaction: TransactionEntity) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm deletion")
        builder.setMessage("Are you sure you want to delete this transaction?")
        builder.setPositiveButton("Yes") { _, _ ->
            controller.deleteTransaction(transaction.id)
            Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show()
            refreshList()
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }

    private fun refreshList() {
        transactions = controller.getAllTransactions().toMutableList()
        val items = transactions.map {
            val sign = if (it.type == "income") "+" else "-"
            val formattedAmount = Util.formatCurrency(it.amount)
            "${it.category}: $sign$formattedAmount (${it.date})"
        }

        adapter.clear()
        adapter.addAll(items)
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        // Reload when returning from Add/Edit screen
        refreshList()
    }
}
