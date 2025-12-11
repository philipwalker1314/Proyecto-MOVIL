package com.bwalker.presupex

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bwalker.presupex.controller.TransactionController
import com.bwalker.presupex.manager.DataProvider
import com.bwalker.presupex.util.Util
import android.widget.LinearLayout
import android.view.ViewGroup
import android.widget.ProgressBar

class StatisticsActivity : AppCompatActivity() {

    private lateinit var tvIncomeStat: TextView
    private lateinit var tvExpenseStat: TextView
    private lateinit var tvBalance: TextView
    private lateinit var tvTransactionCount: TextView
    private lateinit var btnBackStats: Button
    private lateinit var controller: TransactionController

    private lateinit var progressIncome: ProgressBar
    private lateinit var progressExpense: ProgressBar
    private lateinit var categoryContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        controller = TransactionController(DataProvider.sharedDataManager)

        tvIncomeStat = findViewById(R.id.tvIncomeStat)
        tvExpenseStat = findViewById(R.id.tvExpenseStat)
        tvBalance = findViewById(R.id.tvBalance)
        tvTransactionCount = findViewById(R.id.tvTransactionCount)
        btnBackStats = findViewById(R.id.btnBackStats)

        progressIncome = findViewById(R.id.progressIncome)
        progressExpense = findViewById(R.id.progressExpense)
        categoryContainer = findViewById(R.id.categoryContainer)

        loadStatistics()

        btnBackStats.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadStatistics()
    }

    private fun loadStatistics() {
        val totalIncome = controller.getTotalIngresos()
        val totalExpenses = controller.getTotalGastos()
        val balance = controller.getBalance()
        val transactions = controller.getAllTransactions()

        // Display totals
        tvIncomeStat.text = Util.formatCurrency(totalIncome)
        tvExpenseStat.text = Util.formatCurrency(totalExpenses)
        tvBalance.text = Util.formatCurrency(balance)
        tvTransactionCount.text = "${transactions.size} transactions"

        // Set balance color
        tvBalance.setTextColor(
            if (balance >= 0) Color.parseColor("#4CAF50")
            else Color.parseColor("#F44336")
        )

        // Calculate progress bars
        val maxAmount = maxOf(totalIncome, totalExpenses, 1.0)
        progressIncome.progress = ((totalIncome / maxAmount) * 100).toInt()
        progressExpense.progress = ((totalExpenses / maxAmount) * 100).toInt()

        // Show category breakdown
        displayCategoryBreakdown(transactions)
    }

    private fun displayCategoryBreakdown(transactions: List<com.bwalker.presupex.data.TransactionEntity>) {
        categoryContainer.removeAllViews()

        // Group by category and calculate totals
        val categoryTotals = transactions
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }

        if (categoryTotals.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "No transactions yet"
                textSize = 16f
                setTextColor(Color.GRAY)
                setPadding(16, 16, 16, 16)
            }
            categoryContainer.addView(emptyText)
            return
        }

        val totalAmount = categoryTotals.sumOf { it.second }

        categoryTotals.forEach { (category, amount) ->
            val percentage = if (totalAmount > 0) (amount / totalAmount * 100).toInt() else 0

            // Create category row
            val categoryRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
                setPadding(16, 12, 16, 12)
                setBackgroundColor(Color.parseColor("#F5F5F5"))
            }

            // Category name
            val categoryName = TextView(this).apply {
                text = category
                textSize = 16f
                setTextColor(Color.BLACK)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            // Amount and percentage
            val categoryAmount = TextView(this).apply {
                text = "${Util.formatCurrency(amount)} ($percentage%)"
                textSize = 16f
                setTextColor(Color.parseColor("#666666"))
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            categoryRow.addView(categoryName)
            categoryRow.addView(categoryAmount)
            categoryContainer.addView(categoryRow)
        }
    }
}