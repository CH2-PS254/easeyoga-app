package com.dicoding.capstone_ch2ps254.data.remote.pose


import com.dicoding.capstone_ch2ps254.data.model.PosesData
import com.google.gson.annotations.SerializedName

data class ListResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: PosesData
)
