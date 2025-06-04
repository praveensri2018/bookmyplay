package com.praveen.bookmyplay.apiservices

import com.praveen.bookmyplay.models.LoginRequest
import com.praveen.bookmyplay.models.LoginResponse
import com.praveen.bookmyplay.models.RegisterRequest
import com.praveen.bookmyplay.models.RegisterResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

}