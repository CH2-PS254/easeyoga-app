package com.dicoding.capstone_ch2ps254.data.remote.authpack

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("register")
    suspend fun registUser(
        @Body authBody: AuthBody
    ): Response<AuthResponse>

    @POST("login")
    suspend fun loginUser(
        @Body loginBody: com.dicoding.capstone_ch2ps254.data.remote.authpack.Body
    ): AuthResponse
}