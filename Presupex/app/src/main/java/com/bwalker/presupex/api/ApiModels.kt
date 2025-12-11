package com.bwalker.presupex.api

import com.google.gson.annotations.SerializedName

// ============================================
// REQUEST MODELS (lo que enviamos a la API)
// ============================================

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    @SerializedName("full_name")
    val fullName: String
)

data class CreateTransactionRequest(
    val amount: Double,
    val category: String,
    val type: String,
    val description: String,
    val date: String,
    @SerializedName("image_base64")
    val imageBase64: String? = null
)

data class UpdateTransactionRequest(
    val amount: Double,
    val category: String,
    val type: String,
    val description: String,
    val date: String,
    @SerializedName("image_base64")
    val imageBase64: String? = null
)

// ============================================
// RESPONSE MODELS (lo que recibimos de la API)
// ============================================

data class LoginResponse(
    val message: String,
    val user: UserData,
    val session: SessionData
)

data class RegisterResponse(
    val message: String,
    val user: UserData,
    val session: SessionData
)

data class UserData(
    val id: String,
    val email: String,
    @SerializedName("full_name")
    val fullName: String?
)

data class SessionData(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String?,
    @SerializedName("expires_in")
    val expiresIn: Int?
)

data class TransactionResponse(
    val id: Int,
    @SerializedName("user_id")
    val userId: String,
    val amount: Double,
    val category: String,
    val type: String,
    val description: String,
    val date: String,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("image_base64")
    val imageBase64: String?
)

data class TotalsResponse(
    val income: Double,
    val expenses: Double,
    val balance: Double,
    val count: Int
)

data class MessageResponse(
    val message: String,
    val id: Int? = null
)

data class ErrorResponse(
    val error: String
)