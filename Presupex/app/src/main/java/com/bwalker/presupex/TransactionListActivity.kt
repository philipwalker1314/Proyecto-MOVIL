package com.bwalker.presupex

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bwalker.presupex.controller.TransactionController
import com.bwalker.presupex.data.TransactionEntity
import com.bwalker.presupex.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionListActivity : AppCompatActivity() {

    private lateinit var listTransactions: ListView
    private lateinit var btnBack: Button
    private lateinit var controller: TransactionController
    private lateinit var adapter: ArrayAdapter<String>

    private var transactions = mutableListOf<TransactionEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)

        listTransactions = findViewById(R.id.listTransactions)
        btnBack = findViewById(R.id.btnBack)

        controller = TransactionController(this) // ✅ Ahora pasa context

        loadTransactions()

        listTransactions.setOnItemClickListener { _, _, position, _ ->
            val selectedTransaction = transactions[position]
            showActionDialog(selectedTransaction)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadTransactions() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                transactions = controller.getAllTransactions().toMutableList()

                if (transactions.isEmpty()) {
                    Toast.makeText(this@TransactionListActivity, "No transactions available", Toast.LENGTH_SHORT).show()
                }

                val items = transactions.map {
                    val sign = if (it.type == "income") "+" else "-"
                    val formattedAmount = Util.formatCurrency(it.amount)
                    "${it.category}: $sign$formattedAmount (${it.date})"
                }

                adapter = ArrayAdapter(this@TransactionListActivity, android.R.layout.simple_list_item_1, items)
                listTransactions.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@TransactionListActivity, "Error loading transactions", Toast.LENGTH_SHORT).show()
            }
        }
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
            deleteTransaction(transaction.id)
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }

    private fun deleteTransaction(id: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val success = controller.deleteTransaction(id)
                if (success) {
                    Toast.makeText(this@TransactionListActivity, "Transaction deleted", Toast.LENGTH_SHORT).show()
                    refreshList()
                } else {
                    Toast.makeText(this@TransactionListActivity, "Error deleting transaction", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@TransactionListActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ✅ Refrescar lista desde API
    private fun refreshList() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                transactions = controller.getAllTransactions().toMutableList()
                val items = transactions.map {
                    val sign = if (it.type == "income") "+" else "-"
                    val formattedAmount = Util.formatCurrency(it.amount)
                    "${it.category}: $sign$formattedAmount (${it.date})"
                }

                adapter.clear()
                adapter.addAll(items)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }
}