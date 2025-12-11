package com.bwalker.presupex

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bwalker.presupex.controller.TransactionController
import com.bwalker.presupex.util.SessionManager
import com.bwalker.presupex.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var tvBalance: TextView
    private lateinit var tvIncomeAmount: TextView
    private lateinit var tvExpenseAmount: TextView
    private lateinit var btnAddIncome: Button
    private lateinit var btnAddExpense: Button
    private lateinit var btnViewStatistics: Button
    private lateinit var btnViewTransactions: Button
    private lateinit var btnViewPhotoHistory: Button
    private lateinit var btnLogout: Button

    private lateinit var controller: TransactionController
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)
        controller = TransactionController(this) // ✅ Ahora pasa context

        // Link layout components
        tvBalance = findViewById(R.id.tvBalance)
        tvIncomeAmount = findViewById(R.id.tvIncomeAmount)
        tvExpenseAmount = findViewById(R.id.tvExpenseAmount)
        btnAddIncome = findViewById(R.id.btnAddIncome)
        btnAddExpense = findViewById(R.id.btnAddExpense)
        btnViewStatistics = findViewById(R.id.btnViewStatistics)
        btnViewTransactions = findViewById(R.id.btnViewTransactions)
        btnViewPhotoHistory = findViewById(R.id.btnViewPhotoHistory)
        btnLogout = findViewById(R.id.btnLogout)

        // Add Income button
        btnAddIncome.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            intent.putExtra("type", "income")
            startActivity(intent)
        }

        // Add Expense button
        btnAddExpense.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            intent.putExtra("type", "expense")
            startActivity(intent)
        }

        // View transactions
        btnViewTransactions.setOnClickListener {
            val intent = Intent(this, TransactionListActivity::class.java)
            startActivity(intent)
        }

        // View Statistics
        btnViewStatistics.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }

        // View Photo History
        btnViewPhotoHistory.setOnClickListener {
            val intent = Intent(this, TransactionRecyclerActivity::class.java)
            startActivity(intent)
        }

        // Logout button
        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        // Load dashboard values
        updateDashboard()
    }

    override fun onResume() {
        super.onResume()
        updateDashboard()
    }

    // ✅ Actualizado para usar API
    private fun updateDashboard() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val (income, expenses, balance) = controller.getTotals()

                tvIncomeAmount.text = Util.formatCurrency(income)
                tvExpenseAmount.text = Util.formatCurrency(expenses)
                tvBalance.text = Util.formatCurrency(balance)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        sessionManager.logout()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}