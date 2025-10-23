package com.bwalker.presupex.data

data class TransactionEntity(
    val id: Int,              // este es el id de la transaccion
    val amount: Double,       // monto de la transacción
    val category: String,     // categoría
    val type: String,         // ingreso
    val description: String,  // descrip
    val date: String          // fecha del
)

