package com.bwalker.presupex.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "PresupexSession"
        private const val KEY_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    // Guardar sesión después del login
    fun saveSession(token: String, userId: String, email: String, name: String?) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    // Obtener el token para las peticiones
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    // Obtener token con formato Bearer
    fun getAuthToken(): String? {
        val token = getToken()
        return if (token != null) "Bearer $token" else null
    }

    // Verificar si hay sesión activa
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getToken() != null
    }

    // Obtener datos del usuario
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    // Cerrar sesión
    fun logout() {
        prefs.edit().clear().apply()
    }
}