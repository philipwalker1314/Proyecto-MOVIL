package com.bwalker.presupex

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StatisticsActivity : AppCompatActivity() {

    private lateinit var tvIncomeStat: TextView
    private lateinit var tvExpenseStat: TextView
    private lateinit var btnBackStats: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        tvIncomeStat = findViewById(R.id.tvIncomeStat)
        tvExpenseStat = findViewById(R.id.tvExpenseStat)
        btnBackStats = findViewById(R.id.btnBackStats)

        // Datos simulados (más adelante se calcularán desde la lógica)
        tvIncomeStat.text = "Income: ₡1,200.00"
        tvExpenseStat.text = "Expenses: ₡950.00"

        btnBackStats.setOnClickListener {
            finish()
        }
    }
}
