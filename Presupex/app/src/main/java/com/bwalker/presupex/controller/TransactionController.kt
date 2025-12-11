package com.bwalker.presupex.controller

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.bwalker.presupex.api.*
import com.bwalker.presupex.data.TransactionEntity
import com.bwalker.presupex.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class TransactionController(private val context: Context) {

    private val sessionManager = SessionManager(context)
    private val TAG = "TransactionController"

    // Convertir Bitmap a Base64
    private fun bitmapToBase64(bitmap: Bitmap?): String? {
        if (bitmap == null) return null
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // ✅ Convertir Base64 a Bitmap (SIMPLE)
    private fun base64ToBitmap(base64: String?): Bitmap? {
        if (base64.isNullOrEmpty()) return null
        return try {
            val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Log.e(TAG, "Error decoding base64 to bitmap: ${e.message}", e)
            null
        }
    }

    // ✅ Agregar una transacción nueva
    suspend fun addTransaction(transaction: TransactionEntity): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val token = sessionManager.getAuthToken() ?: return@withContext false

                Log.d(TAG, "Creating transaction: ${transaction.category}")

                val request = CreateTransactionRequest(
                    amount = transaction.amount,
                    category = transaction.category,
                    type = transaction.type,
                    description = transaction.description,
                    date = transaction.date,
                    imageBase64 = bitmapToBase64(transaction.image)
                )

                val response = RetrofitClient.apiService.createTransaction(token, request)

                if (response.isSuccessful) {
                    Log.d(TAG, "Transaction created successfully")
                } else {
                    Log.e(TAG, "Failed to create transaction: ${response.code()}")
                }

                response.isSuccessful
            } catch (e: Exception) {
                Log.e(TAG, "Error creating transaction: ${e.message}", e)
                false
            }
        }
    }

    // ✅ Obtener todas las transacciones - SIMPLIFICADO CON BASE64
    suspend fun getAllTransactions(): List<TransactionEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val token = sessionManager.getAuthToken() ?: return@withContext emptyList()

                Log.d(TAG, "Fetching transactions from API")
                val response = RetrofitClient.apiService.getAllTransactions(token)

                if (response.isSuccessful && response.body() != null) {
                    val transactions = response.body()!!
                    Log.d(TAG, "Fetched ${transactions.size} transactions")

                    // Convertir Base64 a Bitmap directamente
                    transactions.map { apiTransaction ->
                        val bitmap = base64ToBitmap(apiTransaction.imageBase64)

                        if (bitmap != null) {
                            Log.d(TAG, "Transaction ${apiTransaction.id} has image: ${bitmap.width}x${bitmap.height}")
                        } else {
                            Log.d(TAG, "Transaction ${apiTransaction.id} has no image")
                        }

                        TransactionEntity(
                            id = apiTransaction.id,
                            amount = apiTransaction.amount,
                            category = apiTransaction.category,
                            type = apiTransaction.type,
                            description = apiTransaction.description,
                            date = apiTransaction.date,
                            image = bitmap  // ✅ Convertido de Base64
                        )
                    }
                } else {
                    Log.e(TAG, "Failed to fetch transactions: ${response.code()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching transactions: ${e.message}", e)
                emptyList()
            }
        }
    }

    // ✅ Obtener totales del usuario actual
    suspend fun getTotals(): Triple<Double, Double, Double> {
        return withContext(Dispatchers.IO) {
            try {
                val token = sessionManager.getAuthToken() ?: return@withContext Triple(0.0, 0.0, 0.0)

                val response = RetrofitClient.apiService.getTotals(token)

                if (response.isSuccessful && response.body() != null) {
                    val totals = response.body()!!
                    Triple(totals.income, totals.expenses, totals.balance)
                } else {
                    Triple(0.0, 0.0, 0.0)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching totals: ${e.message}", e)
                Triple(0.0, 0.0, 0.0)
            }
        }
    }

    // ✅ Actualizar una transacción
    suspend fun updateTransaction(transaction: TransactionEntity): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val token = sessionManager.getAuthToken() ?: return@withContext false

                val request = UpdateTransactionRequest(
                    amount = transaction.amount,
                    category = transaction.category,
                    type = transaction.type,
                    description = transaction.description,
                    date = transaction.date,
                    imageBase64 = bitmapToBase64(transaction.image)
                )

                val response = RetrofitClient.apiService.updateTransaction(
                    token = token,
                    id = transaction.id,
                    request = request
                )
                response.isSuccessful
            } catch (e: Exception) {
                Log.e(TAG, "Error updating transaction: ${e.message}", e)
                false
            }
        }
    }

    // ✅ Eliminar una transacción
    suspend fun deleteTransaction(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val token = sessionManager.getAuthToken() ?: return@withContext false

                val response = RetrofitClient.apiService.deleteTransaction(token, id)
                response.isSuccessful
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting transaction: ${e.message}", e)
                false
            }
        }
    }
}