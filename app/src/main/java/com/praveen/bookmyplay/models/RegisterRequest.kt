package com.praveen.bookmyplay.models

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String,
    val mobile: String,
    val full_name: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

// Login response
data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)