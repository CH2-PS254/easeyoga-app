package com.dicoding.capstone_ch2ps254.data.resource

import com.dicoding.capstone_ch2ps254.data.local.ListDao
import com.dicoding.capstone_ch2ps254.data.remote.ApiResponse
import com.dicoding.capstone_ch2ps254.data.remote.pose.AddPoseResponse
import com.dicoding.capstone_ch2ps254.data.remote.pose.ListResponse
import com.dicoding.capstone_ch2ps254.data.remote.pose.toListEntity
import com.dicoding.capstone_ch2ps254.data.remote.pose.Service
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListResource @Inject constructor(
    private val dao: ListDao,
    private val service: Service
){
    suspend fun getPose(token: String): Flow<ApiResponse<ListResponse>> {
        return flow{
            try {
                emit(ApiResponse.Loading)
                val response = service.getList(token)
                if (!response.error) {
                    dao.deleteAllList()
                    val listList = response.listPose.map {
                        toListEntity(it)
                    }
                    dao.insertList(listList)
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Error(response.message))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }

    suspend fun addPose(token: String, file: MultipartBody.Part, description: RequestBody): Flow<ApiResponse<AddPoseResponse>> {
        return flow{
            try {
                emit(ApiResponse.Loading)
                val response = service.addList(token, file, description)
                if (!response.error) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Error(response.message))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }
}