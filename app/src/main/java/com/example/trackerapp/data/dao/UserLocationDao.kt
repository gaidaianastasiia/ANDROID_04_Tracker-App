package com.example.trackerapp.data.dao

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.trackerapp.data.entity.UserLocationData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserLocationDao {
    @Insert
    @Throws(SQLiteException::class)
    suspend fun insertUserLocation(userLocation: UserLocationData): Long

    @Query("SELECT * FROM UserLocationData")
    @Throws(SQLiteException::class)
    fun getAllUserLocations(): Flow<List<UserLocationData>>

    @Query("SELECT * FROM UserLocationData ORDER BY id DESC LIMIT 1")
    suspend fun getLastUserLocation(): UserLocationData?

    @Query("SELECT SUM(distance) from UserLocationData")
    fun getCoveredDistance(): Flow<Double?>

    @Query("DELETE FROM UserLocationData")
    suspend fun deleteAllUserLocations()
}