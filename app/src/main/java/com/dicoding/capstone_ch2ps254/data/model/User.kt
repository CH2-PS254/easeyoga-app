package com.dicoding.capstone_ch2ps254.data.model

import com.google.gson.annotations.SerializedName

data class User(
//    @SerializedName("id")
//    val id: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("token")
    val token: String
)
