package com.bwalker.presupex.data

import android.graphics.Bitmap

data class TransactionEntity(
    val id: Int,
    val amount: Double,
    val category: String,
    val type: String,
    val description: String,
    val date: String,
    val image: Bitmap? = null   // NEW FIELD
)


