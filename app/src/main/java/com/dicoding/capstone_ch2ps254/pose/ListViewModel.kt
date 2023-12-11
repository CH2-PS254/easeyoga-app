package com.dicoding.capstone_ch2ps254.pose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_ch2ps254.data.remote.pose.AddPoseResponse
import com.dicoding.capstone_ch2ps254.data.remote.ApiResponse
import com.dicoding.capstone_ch2ps254.data.repository.ListRepository
import com.dicoding.capstone_ch2ps254.data.remote.pose.ListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(private val poseRepository: ListRepository): ViewModel() {
    fun getPose(token: String) : LiveData<ApiResponse<ListResponse>>{
        val result = MutableLiveData<ApiResponse<ListResponse>>()
        viewModelScope.launch {
            poseRepository.getPose(token).collect {
                result.postValue(it)
            }
        }
        return result
    }

    fun addPose(token: String, file: MultipartBody.Part, description: RequestBody): LiveData<ApiResponse<AddPoseResponse>> {
        val result = MutableLiveData<ApiResponse<AddPoseResponse>>()
        viewModelScope.launch {
            poseRepository.addPose(token, file, description).collect {
                result.postValue(it)
            }
        }
        return result
    }
}