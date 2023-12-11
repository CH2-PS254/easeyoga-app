package com.dicoding.capstone_ch2ps254.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ListDao {
    @Query("SELECT * FROM table_list")
    fun getPose(): List<ListEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(list: List<ListEntity>)

    @Query("DELETE FROM table_list")
    suspend fun deleteAllList()
}