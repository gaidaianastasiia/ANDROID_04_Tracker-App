package com.example.trackerapp.di.module

import android.content.Context
import androidx.room.Room
import com.example.trackerapp.data.ApplicationDatabase
import com.example.trackerapp.data.dao.UserLocationDao
import com.example.trackerapp.data.dao.WalkDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

const val DATABASE_NAME = "TrackerDatabase"

@Module
class StorageModule {
    @Provides
    @Singleton
    fun provideDataBase(context: Context): ApplicationDatabase =
        Room.databaseBuilder(
            context,
            ApplicationDatabase::class.java,
            DATABASE_NAME
        ).build()

    @Provides
    @Singleton
    fun provideUserLocationDao(database: ApplicationDatabase): UserLocationDao =
        database.userLocationDao()

    @Provides
    @Singleton
    fun provideWalkDao(database: ApplicationDatabase): WalkDao =
        database.walkDao()
}

