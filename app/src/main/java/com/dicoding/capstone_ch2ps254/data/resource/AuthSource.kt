package com.dicoding.capstone_ch2ps254.data.resource

import com.dicoding.capstone_ch2ps254.data.remote.ApiResponse
import com.dicoding.capstone_ch2ps254.data.remote.authpack.AuthBody
import com.dicoding.capstone_ch2ps254.data.remote.authpack.AuthResponse
import com.dicoding.capstone_ch2ps254.data.remote.authpack.AuthService
import com.dicoding.capstone_ch2ps254.data.remote.authpack.Body
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthSource @Inject constructor(private val authService: AuthService) {
    suspend fun registUser (authBody: AuthBody): Flow<ApiResponse<Response<AuthResponse>>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = authService.registUser(authBody)
                if (response.code() == 201) {
                    emit(ApiResponse.Success(response))
                } else if (response.code() == 400) {
                    val errorBody = JSONObject(response.errorBody()!!.string())
                    emit(ApiResponse.Error(errorBody.getString("message")))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }

    suspend fun loginUser(loginBody: Body): Flow<ApiResponse<AuthResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = authService.loginUser(loginBody)
                if (!response.error) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Error(response.status))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }
}