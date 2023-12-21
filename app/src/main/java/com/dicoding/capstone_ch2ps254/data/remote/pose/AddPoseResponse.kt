package com.dicoding.capstone_ch2ps254.data.remote.pose

import com.google.gson.annotations.SerializedName

data class AddPoseResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)
