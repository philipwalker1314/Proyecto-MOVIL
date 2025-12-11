package com.bwalker.presupex

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bwalker.presupex.api.RegisterRequest
import com.bwalker.presupex.api.RetrofitClient
import com.bwalker.presupex.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvGoToLogin: TextView

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sessionManager = SessionManager(this)

        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)

        btnRegister.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (validateInputs(fullName, email, password, confirmPassword)) {
                register(fullName, email, password)
            }
        }

        tvGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (fullName.isEmpty()) {
            etFullName.error = "Full name is required"
            return false
        }
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
        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            return false
        }
        return true
    }

    private fun register(fullName: String, email: String, password: String) {
        btnRegister.isEnabled = false
        btnRegister.text = "Registering..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = RegisterRequest(email, password, fullName)
                val response = RetrofitClient.apiService.register(request)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val registerResponse = response.body()!!

                        // Guardar sesión
                        sessionManager.saveSession(
                            token = registerResponse.session.accessToken,
                            userId = registerResponse.user.id,
                            email = registerResponse.user.email,
                            name = registerResponse.user.fullName ?: fullName
                        )

                        Toast.makeText(
                            this@RegisterActivity,
                            "Account created successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                        goToMainActivity()
                    } else {
                        val errorMsg = when (response.code()) {
                            400 -> "User already exists or invalid data"
                            else -> "Registration failed: ${response.code()}"
                        }
                        Toast.makeText(
                            this@RegisterActivity,
                            errorMsg,
                            Toast.LENGTH_LONG
                        ).show()
                        btnRegister.isEnabled = true
                        btnRegister.text = "Register"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    btnRegister.isEnabled = true
                    btnRegister.text = "Register"
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