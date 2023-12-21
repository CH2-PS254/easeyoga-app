package com.dicoding.capstone_ch2ps254.data.remote.pose

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Service {
    @GET("poses")
    suspend fun getList(
        @Header("Authorization") token: String
    ): ListResponse

    @Multipart
    @POST("poses")
    suspend fun addList(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): AddPoseResponse

}