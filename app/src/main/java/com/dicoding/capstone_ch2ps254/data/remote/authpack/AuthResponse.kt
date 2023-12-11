package com.dicoding.capstone_ch2ps254.data.remote.authpack

import com.dicoding.capstone_ch2ps254.data.model.User
import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("loginResult")
    val loginResult: User
)
