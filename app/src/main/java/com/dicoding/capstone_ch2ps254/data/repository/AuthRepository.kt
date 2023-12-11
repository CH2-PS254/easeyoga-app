package com.dicoding.capstone_ch2ps254.data.repository

import com.dicoding.capstone_ch2ps254.data.remote.ApiResponse
import com.dicoding.capstone_ch2ps254.data.remote.authpack.AuthBody
import com.dicoding.capstone_ch2ps254.data.remote.authpack.AuthResponse
import com.dicoding.capstone_ch2ps254.data.remote.authpack.Body
import com.dicoding.capstone_ch2ps254.data.resource.AuthSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val authDataSource: AuthSource) {
    suspend fun registUser(authBody: AuthBody): Flow<ApiResponse<Response<AuthResponse>>> {
        return authDataSource.registUser(authBody).flowOn(Dispatchers.IO)
    }

    suspend fun loginUser(loginBody: Body): Flow<ApiResponse<AuthResponse>> {
        return authDataSource.loginUser(loginBody).flowOn(Dispatchers.IO)
    }
}