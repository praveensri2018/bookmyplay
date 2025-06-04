package com.praveen.bookmyplay.models

data class RegisterResponse(
    val message: String,
    val token: String,
    val user: User
)

data class User(
    val id: String,
    val email: String,
    val username: String,
    val mobile: String,
    val full_name: String,
    val is_admin: Boolean
)