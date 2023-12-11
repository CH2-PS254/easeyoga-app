package com.dicoding.capstone_ch2ps254.data.remote.pose

import com.dicoding.capstone_ch2ps254.data.local.ListEntity
import com.dicoding.capstone_ch2ps254.data.model.Pose

fun toListEntity(list: Pose): ListEntity {
    return ListEntity(
        id = list.id,
        image = list.image
    )
}