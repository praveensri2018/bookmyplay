package com.praveen.bookmyplay.screens

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import android.widget.Button
import android.widget.Toast
import com.praveen.bookmyplay.R
import com.praveen.bookmyplay.apiservices.RetrofitClient
import com.praveen.bookmyplay.models.RegisterRequest
import com.praveen.bookmyplay.models.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var mobileEditText: TextInputEditText
    private lateinit var fullNameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var registerButton: Button




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        emailEditText = findViewById(R.id.editTextEmail)
        usernameEditText = findViewById(R.id.editTextUsername)
        mobileEditText = findViewById(R.id.editTextMobile)
        fullNameEditText = findViewById(R.id.editTextFullName)
        passwordEditText = findViewById(R.id.editTextPassword)
        registerButton = findViewById(R.id.buttonRegister)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val mobile = mobileEditText.text.toString().trim()
            val fullName = fullNameEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            // Add validation here
            val request = RegisterRequest(
                email = email,
                password = password,
                username = username,
                mobile = mobile,
                full_name = fullName
            )

            RetrofitClient.authApi.registerUser(request).enqueue(object : retrofit2.Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: retrofit2.Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        val registeredUser = response.body()
                        Toast.makeText(this@RegisterActivity, "Registered: ${registeredUser?.user?.email}", Toast.LENGTH_LONG).show()
                        // Optionally navigate to another screen
                    } else {
                        Toast.makeText(this@RegisterActivity, "Failed: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })

            // TODO: Call your backend API to register user with these fields

            //Toast.makeText(this, "Register button clicked", Toast.LENGTH_SHORT).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
