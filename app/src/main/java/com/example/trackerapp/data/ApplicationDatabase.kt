package com.example.trackerapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.trackerapp.data.typeconverters.LocalDateTimeConverter
import com.example.trackerapp.data.dao.UserLocationDao
import com.example.trackerapp.data.entity.UserLocationData

private const val DATABASE_VERSION = 1

@Database(entities = [UserLocationData::class], version = DATABASE_VERSION)
@TypeConverters(LocalDateTimeConverter::class)
abstract class ApplicationDatabase: RoomDatabase() {
    abstract fun userLocationDao(): UserLocationDao
}