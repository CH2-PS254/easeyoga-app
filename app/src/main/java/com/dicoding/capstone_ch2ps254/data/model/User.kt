package com.dicoding.capstone_ch2ps254.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("token")
    val token: String
)
