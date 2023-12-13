package com.dicoding.capstone_ch2ps254.data.remote.pose

import com.dicoding.capstone_ch2ps254.data.local.ListEntity
import com.dicoding.capstone_ch2ps254.data.model.Poses

fun toListEntity(poses: Poses): ListEntity {
    return ListEntity(
        id = poses.id,
        name = poses.name,
        description = poses.description,
        image = poses.image
    )
}