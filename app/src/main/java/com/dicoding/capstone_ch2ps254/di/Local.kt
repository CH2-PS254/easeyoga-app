package com.dicoding.capstone_ch2ps254.di

import android.content.Context
import androidx.room.Room
import com.dicoding.capstone_ch2ps254.data.local.ListDao
import com.dicoding.capstone_ch2ps254.data.local.ListDatabase
import com.dicoding.capstone_ch2ps254.utils.ValManager.DB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Local {
    @Singleton
    @Provides

    fun provideDatabase(@ApplicationContext context: Context): ListDatabase {
    return Room.databaseBuilder(context, ListDatabase::class.java, DB)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideListDao(database: ListDatabase): ListDao = database.getListDao()
}