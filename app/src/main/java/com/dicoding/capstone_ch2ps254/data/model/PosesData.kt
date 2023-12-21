package com.dicoding.capstone_ch2ps254.data.model

import com.google.gson.annotations.SerializedName

data class PosesData(
    @SerializedName("poses")
    val poses: List<Poses>
)
