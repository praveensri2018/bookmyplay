package com.praveen.bookmyplay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.praveen.bookmyplay.screens.RegisterActivity
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.praveen.bookmyplay.apiservices.RetrofitClient
import com.praveen.bookmyplay.models.LoginRequest
import com.praveen.bookmyplay.screens.HomeActivity
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Check if user is already logged in (has auth token)
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val savedToken = prefs.getString("auth_token", null)
        if (savedToken != null) {
            // If token exists, directly navigate to MainActivity
            startActivity(Intent(this, HomeActivity::class.java))
            finish() // Close LoginActivity
            return // Stop further execution
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val forgotPasswordText = findViewById<TextView>(R.id.textForgotPassword)
        forgotPasswordText.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val signUpButton = findViewById<Button>(R.id.buttonSignUp)
        signUpButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Add login button click listener
        val loginButton = findViewById<Button>(R.id.buttonLogin)
        val emailEditText = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextPassword)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call login API asynchronously using Coroutine
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.authApi.login(LoginRequest(email, password))
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        loginResponse?.let {
                            Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_SHORT).show()

                            // 2. Save token and user details in SharedPreferences
                            prefs.edit().apply {
                                putString("auth_token", it.token)
                                putString("user_id", it.user.id)
                                putString("user_email", it.user.email)
                                putString("user_username", it.user.username)
                                putString("user_full_name", it.user.full_name)
                                putBoolean("user_is_admin", it.user.is_admin)
                                apply()
                            }

                            // 3. Navigate to MainActivity after successful login
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish() // Close LoginActivity
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                } catch (e: HttpException) {
                    Toast.makeText(this@LoginActivity, "Server error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
