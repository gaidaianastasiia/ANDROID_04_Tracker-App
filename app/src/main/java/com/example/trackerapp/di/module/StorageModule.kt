package com.example.trackerapp.di.module

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.trackerapp.R
import com.example.trackerapp.data.ApplicationDatabase
import com.example.trackerapp.data.dao.UserLocationDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

const val PREFERENCE_FILE_KEY = "Tracker preferences"

@Module
class StorageModule {
    @Provides
    @Singleton
    fun provideDataBase(context: Context): ApplicationDatabase =
        Room.databaseBuilder(
            context,
            ApplicationDatabase::class.java,
            "ApplicationDatabase"
        ).build()

    @Provides
    @Singleton
    fun provideUserLocationDao(database: ApplicationDatabase): UserLocationDao =
        database.userLocationDao()
}

