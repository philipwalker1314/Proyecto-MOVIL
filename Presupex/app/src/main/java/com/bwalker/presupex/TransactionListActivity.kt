package com.bwalker.presupex

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class TransactionListActivity : AppCompatActivity() {

    private lateinit var listTransactions: ListView
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)

        listTransactions = findViewById(R.id.listTransactions)
        btnBack = findViewById(R.id.btnBack)

        // Lista de ejemplo
        val transactions = listOf("💰 Salary +₡800", "🛒 Supermarket -₡50", "🎬 Entertainment -₡30", "☕ Coffee -₡10")

        // Adaptador simple para mostrar la lista
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, transactions)
        listTransactions.adapter = adapter

        // Botón volver
        btnBack.setOnClickListener {
            finish()
        }
    }
}
