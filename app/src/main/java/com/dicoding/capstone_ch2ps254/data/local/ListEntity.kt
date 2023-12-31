package com.dicoding.capstone_ch2ps254.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_list")
data class ListEntity(

        @ColumnInfo(name = "id")
        @PrimaryKey(autoGenerate = false)
        val id: Int,
        @ColumnInfo(name = "name")
        val name: String,
        @ColumnInfo(name = "description")
        val description: String,
        @ColumnInfo(name = "image")
        val image: String

)
