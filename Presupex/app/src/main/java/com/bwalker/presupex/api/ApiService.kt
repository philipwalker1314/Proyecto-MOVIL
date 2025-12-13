package com.bwalker.presupex.api

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ============================================
    // AUTH ENDPOINTS
    // ============================================

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<MessageResponse>

    @POST("auth/verify")
    suspend fun verifyToken(@Header("Authorization") token: String): Response<Any>

    // ============================================
    // TRANSACTION ENDPOINTS
    // ============================================

    @GET("transactions")
    suspend fun getAllTransactions(
        @Header("Authorization") token: String
    ): Response<List<TransactionResponse>>

    @GET("transactions/totals")
    suspend fun getTotals(
        @Header("Authorization") token: String
    ): Response<TotalsResponse>

    @POST("transactions")
    suspend fun createTransaction(
        @Header("Authorization") token: String,
        @Body request: CreateTransactionRequest
    ): Response<MessageResponse>

    @PUT("transactions/{id}")
    suspend fun updateTransaction(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: UpdateTransactionRequest
    ): Response<MessageResponse>

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<MessageResponse>
}