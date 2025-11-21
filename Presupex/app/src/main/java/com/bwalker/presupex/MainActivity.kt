package com.bwalker.presupex
import com.bwalker.presupex.manager.DataProvider

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bwalker.presupex.controller.TransactionController
import com.bwalker.presupex.util.Util

class MainActivity : AppCompatActivity() {

    private lateinit var tvBalance: TextView
    private lateinit var tvIncomeAmount: TextView
    private lateinit var tvExpenseAmount: TextView
    private lateinit var btnAddIncome: Button
    private lateinit var btnAddExpense: Button
    private lateinit var btnViewStatistics: Button
    private lateinit var btnViewTransactions: Button
    private lateinit var btnViewPhotoHistory: Button   // NEW BUTTON

    private lateinit var controller: TransactionController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Link layout components
        tvBalance = findViewById(R.id.tvBalance)
        tvIncomeAmount = findViewById(R.id.tvIncomeAmount)
        tvExpenseAmount = findViewById(R.id.tvExpenseAmount)
        btnAddIncome = findViewById(R.id.btnAddIncome)
        btnAddExpense = findViewById(R.id.btnAddExpense)
        btnViewStatistics = findViewById(R.id.btnViewStatistics)
        btnViewTransactions = findViewById(R.id.btnViewTransactions)
        btnViewPhotoHistory = findViewById(R.id.btnViewPhotoHistory) // NEW

        // Initialize controller
        controller = TransactionController(DataProvider.sharedDataManager)

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

        // View old ListView-based transaction list
        btnViewTransactions.setOnClickListener {
            val intent = Intent(this, TransactionListActivity::class.java)
            startActivity(intent)
        }

        // View Statistics
        btnViewStatistics.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }

        // NEW: View RecyclerView-based list with photos
        btnViewPhotoHistory.setOnClickListener {
            val intent = Intent(this, TransactionRecyclerActivity::class.java)
            startActivity(intent)
        }

        // Load dashboard values
        updateDashboard()
    }

    override fun onResume() {
        super.onResume()
        // Update values when returning from child activities
        updateDashboard()
    }

    private fun updateDashboard() {
        val income = controller.getTotalIngresos()
        val expense = controller.getTotalGastos()
        val balance = controller.getBalance()

        tvIncomeAmount.text = Util.formatCurrency(income)
        tvExpenseAmount.text = Util.formatCurrency(expense)
        tvBalance.text = Util.formatCurrency(balance)
    }
}
