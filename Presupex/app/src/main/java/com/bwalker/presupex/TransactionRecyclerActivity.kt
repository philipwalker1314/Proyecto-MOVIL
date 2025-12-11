package com.bwalker.presupex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import com.bwalker.presupex.adapter.TransactionRecyclerAdapter
import com.bwalker.presupex.controller.TransactionController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionRecyclerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBack: Button
    private lateinit var controller: TransactionController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_recycler)

        controller = TransactionController(this)

        recyclerView = findViewById(R.id.recyclerTransactions)
        btnBack = findViewById(R.id.btnBackRecycler)

        recyclerView.layoutManager = LinearLayoutManager(this)

        loadTransactions()

        btnBack.setOnClickListener { finish() }
    }

    private fun loadTransactions() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val transactions = controller.getAllTransactions()
                recyclerView.adapter = TransactionRecyclerAdapter(transactions)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }
}