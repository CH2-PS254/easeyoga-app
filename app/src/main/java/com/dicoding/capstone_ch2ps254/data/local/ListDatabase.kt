package com.dicoding.capstone_ch2ps254.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities =[ListEntity::class],
    version = 3,
    exportSchema = false
)

abstract class ListDatabase : RoomDatabase() {

    abstract fun getListDao(): ListDao

}