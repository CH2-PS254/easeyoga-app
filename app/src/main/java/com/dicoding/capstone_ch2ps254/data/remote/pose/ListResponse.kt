package com.dicoding.capstone_ch2ps254.data.remote.pose

import com.dicoding.capstone_ch2ps254.data.model.Pose
import com.google.gson.annotations.SerializedName

data class ListResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("listPose")
    val listPose: List<Pose>
)
