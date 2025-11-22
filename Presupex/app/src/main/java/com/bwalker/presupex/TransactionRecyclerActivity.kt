package com.bwalker.presupex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import com.bwalker.presupex.adapter.TransactionRecyclerAdapter
import com.bwalker.presupex.controller.TransactionController
import com.bwalker.presupex.manager.DataProvider

class TransactionRecyclerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBack: Button
    private lateinit var controller: TransactionController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_recycler)

        controller = TransactionController(DataProvider.sharedDataManager)

        recyclerView = findViewById(R.id.recyclerTransactions)
        btnBack = findViewById(R.id.btnBackRecycler)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TransactionRecyclerAdapter(controller.getAllTransactions())

        btnBack.setOnClickListener { finish() }
    }
}
