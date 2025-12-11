package com.bwalker.presupex

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bwalker.presupex.api.LoginRequest
import com.bwalker.presupex.api.RetrofitClient
import com.bwalker.presupex.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoToRegister: TextView

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        // Si ya hay sesión, ir directo a MainActivity
        if (sessionManager.isLoggedIn()) {
            goToMainActivity()
            return
        }

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvGoToRegister = findViewById(R.id.tvGoToRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                login(email, password)
            }
        }

        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid email format"
            return false
        }
        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            return false
        }
        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return false
        }
        return true
    }

    private fun login(email: String, password: String) {
        btnLogin.isEnabled = false
        btnLogin.text = "Logging in..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = LoginRequest(email, password)
                val response = RetrofitClient.apiService.login(request)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse = response.body()!!

                        // Guardar sesión
                        sessionManager.saveSession(
                            token = loginResponse.session.accessToken,
                            userId = loginResponse.user.id,
                            email = loginResponse.user.email,
                            name = loginResponse.user.fullName ?: ""
                        )

                        Toast.makeText(
                            this@LoginActivity,
                            "Welcome ${loginResponse.user.fullName ?: email}!",
                            Toast.LENGTH_SHORT
                        ).show()

                        goToMainActivity()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login failed: Invalid credentials",
                            Toast.LENGTH_LONG
                        ).show()
                        btnLogin.isEnabled = true
                        btnLogin.text = "Login"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    btnLogin.isEnabled = true
                    btnLogin.text = "Login"
                }
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}