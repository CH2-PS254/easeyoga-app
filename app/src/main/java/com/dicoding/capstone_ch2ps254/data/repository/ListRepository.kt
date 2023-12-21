package com.dicoding.capstone_ch2ps254.data.repository

import com.dicoding.capstone_ch2ps254.data.resource.ListResource
import com.dicoding.capstone_ch2ps254.data.remote.ApiResponse
import com.dicoding.capstone_ch2ps254.data.remote.pose.AddPoseResponse
import com.dicoding.capstone_ch2ps254.data.remote.pose.ListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListRepository @Inject constructor(private val storySource: ListResource) {

        suspend fun getPose(token: String): Flow<ApiResponse<ListResponse>> {
            return storySource.getPose(token).flowOn(Dispatchers.IO)
        }

        suspend fun addPose(token: String, file: MultipartBody.Part, description: RequestBody): Flow<ApiResponse<AddPoseResponse>> {
            return storySource.addPose(token, file, description)
        }
}